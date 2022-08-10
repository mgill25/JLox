# Functions

## I would first extend the Grammar

### How is a function called?

- How should our grammar be extended?
- What is the precedence of () operators?
- How many max. arguments can a function have?
- Do we support currying? `foo()()()` :- support needs to be baked into the Parser
- Represent `FunCall` as an AST Node

### How is a function declared (or defined)?

- How should the function declaration be called?
- What should be the precedence
    - Right at the lower end - along with variable name declaration.
- Parse the
    - Identifier
    - Parenthesis
    - Arguments
    - Closing Paren
    - Body which is a Block
- Represent FunStmt as an AST Node

### Declaration vs Call
- function declaration is a statement
- function call is an expression

### Representing Functions
- The AST Node of FunCall does not get stored in the Runtime itself
- It's why we have a Callable (`LoxCallable`) interface which serves as an intermediate object
- So, once the interpreter executes a FunStmt, it:
    - Creates a `LoxCallable` for the function
    - Stores the LoxCallable object in the env

- visitCallExpr
    - evaluate the callee
    - Ensure the result is a LoxCallable, otherwise we can't call it
    - evaluate all the arguments
    - Check arity
    - invoke `.call()` on the LoxCallable object

### LoxCallable.call()

- for User-defined functions:
    - Creates a new environment
    - Defines all declarations in this new environment
    - Executes the Body (which is block() statement). Supply the functionEnv

### Contents of the AST Nodes and the Function object
- FunStmt
    - name, `list<params>`, body base expr
- FunCall
    - callee Expr, list<args>
    - closingParen (for some reason we don't know yet!)
- LoxCallable
    - `.arity()` and `.call()`

## Closures
- Right now, call() creates a new env, and the parent of that env is always the global env.
- But, we want Lox to support Local functions as well: Functions that are not part of the global
  scope, but live inside other functions, for example.

```javascript
fun makeCounter() {
    var i = 0;
    fun count() {
        i = i + 1;
        print i;
    }
    return count;
}

var counter = makeCounter();
counter();  // 1
counter();  // 2
```

In the above example, `count()` is a local function which should not be visible to the global scope.

The problem here is that as it stands, we get "undefined variable `i`" error. Why?

- The parent of every call is the global env. <-- This is a problem.
- `i` is defined inside the environment of `makeCounter()` which gets created on `makeCounter` call.
- upon getting the reference to the `count()` function, we call that.
    - when we invoke `count()`, we create a new clean env for _its_ own call. This env has no reference to i.
    - We lost the definition of i that we had stored.
    - The reference was also not available in globals either.
        - And storing it in globals is anyway not a great idea.
    - So, we need to keep the environment of the _outer_ scope alive and make it accessible
      to every element in the inner scope.

When we exit from `makeCounter()` body, its environment is discarded.
It is up to the function object to hang on to that environment.
This data structure is called a _Closure_ because it "closes over" and holds the surrounding
environment when the function is declared.

Closures have been around since the early Lisp days and language hackers have come up with
various ways to implement them.

We just enhance our `LoxFunction` to also have an Environment called `closure`

Okay, during our Parsing, we encounter function definition, as well as function call.

We represent functions via Callable interface. The implementation
of that is LoxFunction, which knows the arity of the function,
as well as knows how to .call() it.
.call() on a function's body is going to execute the function itself.

Not only that, the creation of an entirely new environment dedicated to the function itself happens at call-time. This happens multiple times for every function call. If a function is called 5 times, we will create 5 new environments. This is clear.

Why would we do this?
- The reason presented is that the parameter -> argument parsing happens in lockstep **during** the calling of the function. That information itself is stored within the function's unique environment, and nowhere else.

    This basically means a function encapsulates itself and at call time, the definition and calling of parameter is resolved.


Okay, what have we done to implement functions?

- First we made the interpreter recognize function calls
    - Ensure that currying works
    - Think of () as an operator: high precedence
    - Loop to parse zero or more identifiers b/w left and right parenthesis
    - Keep doing this as long as we have at least one left paren open after a successful call()
        - This supports parsing of currying like foo()() or bar(x)(y, z)
    - AST node will be FunCall expression, which contains:
        - Expr callee
        - Token closingParen (for better error messages)
        - List<Expr> arguments
    - Now the language can recognize function calls. But what does the evaluator/interpreter do with these calls?

Call implementation:
- Ensure there exists a LoxCallable interface, which supports `.call()` and `.arity()` methods
- Upon evaluating a function call, we:
- evaluate the callee expression (the callee itself can also be a function etc)
- ensure the final evaluated callee is a LoxCallable (otherwise it can be a non-callable expression)
- we don't want things like "123"() : here "123" can be successfully evaluated but it is not a callable.
- iterate over each argument and evaluate that (because arguments can also be expressions!)
- Store the evaluated args in a List<Object>
- Match callee function's arity() with size() of evaluated args: they must be equal
- This is a design decision but for the sake of simplicity we are rigid about it
- foo(x, y) must mean the call is foo(1, 2) and everything else generates an error
- finally, do function.call(this, evaluatedArgs)
- Key insight: note, we pass the Interpreter itself to the call. Why did we do that?
- Because we need to change the "current env" that the interpreter has in its scope and is looking at
- We also need to define a brand new environment for every single function call invocation.

The .call() implementation
- Creates a brand new environment
- Iterates over all declaration's parameters
- Gets the Token and defines them in the newly created environment
- In this way, Upon the function call, the parameters get defined inside the env of the function itself
- Functions here are thus, self contained. The global environment is not polluted by function parameters
- We execute the Function's Body


- Parser parses Function declaration "fun <identifier>(args) { }"
- funDeclaration() within the recursive descent parser creates a FunStmt :- A function statement
    - This statement contains the function Name, List of parameters and the Body (a Block)
- Returns the funStmt
- Upon Interpretation of FunStmt
    - Create a new Function Object (LoxFunction, which is a callable)
    - Defines the fun name, along with the Function object in the global env.

So, LoxFunction() when it gets created, it gets an Environment parameter,
which represents the closure.

New Question: How is this closure changing as we enter new levels of nesting?
- Because it clearly is. Just need to trace back where.
- Function object is created when we visitFunStmt()
- That is, when we evaluate the function declaration.
- The only place where we _receive_ a `newEnv` and make that our current env,
and then restore the previous env later happens inside blocks.
- And because function body is always a block, it makes sense that this is probably where
that happens!


First, the parser tokenizes all statements and makes an AST

During interpretation:
- We have a global environment which has builtins
- As we interpret FunStmt, we simply don't evaluate the body (that happens at call)
- And as such, the creation of dedicated envs for Function body also hasn't happened yet
- The function gets defined and gets added to the top-level env

- During the call of the outer function, we create the dedicated env (with global as enclosing)
- When the inner function is declared, it gets added to the current env's scope
- Closure closes over the entire env, regardless of whether definitions came before or after the function declaration
    - But it should obviously come before the call() of the closure function,
      otherwise we will have a lookup failure (since the definition will never be defined in the environment)
      When we visitBlock(), we executeBlock(), and pass in a new Environment(env), which encloses the existing one.
      Inside executeBlock:
      - We keep aside the current env, mark the new one as current and then execute all statements
      - Once we are done executing all statements, we restore the env.

  When we visitCall, we evaluate the callee, evaluate the args, cast callee as LoxCallable
  then .call() the callable function
  Inside .call()
  - We create a new Environment which encloses the closed over env (this
  was stored in callee when we parsed FunStmt)    
  Also, we use executeBlock() inside the function body, so the same logic of switching env works here :)
  - So in the functions, we use the same logic we use in blocks:
  - Creation of a new Env
  - Keeping existing env aside, executing statements with the new one
  - Restoring
  - The only difference is what's the enclosing env
  - For functions, it's the closure, which gets created when we parse the FunStmt during declaration
  - For blocks, it's whatever happens to be the current env at the moment.

### Lambda Implementation

1. Extend the language grammar to support anonymous functions as arguments.
2. Represent lambdas via `LambdaExpr` AST node. Here, the design decision is to make lambda an expression.
   1. The lambda expression actually encapsulates a `FunStmt` - which in turn is just like a regular function statement.
   2. The only difference is that there is no `funIdentifier` available for lambda's funStmt.
3. Add the rules to the parser - within the function's arguments, we can have expressionsOrLambdas.
4. Upon visiting the lambda expr, we create a `LoxFunction`, which is our language's representation of function objects.
   1. `LoxFunction` is now aware of whether it is representing a lambda or not (via a boolean)
   2. Based on that, the constructor represents the function name (and can in future do other things)
   3. The `.call()` behaviour is the same (as it should be)
5. During the evaluation of a CallExpr, now we need to check if an arg is a Callable or not.
   1. For Callable args, we don't `evaluate()` them, but do `env.define` the `LoxCallable` casted version.
   2. Now, the lambda's FunStmt is stored inside the encapsulated fun env, and can be looked up and executed successfully.


### Closure Scope Leak

With the introduction of closures, we have inadvertently introduced a correctness issue
in the interpreter.

Even though we can define closures, if a closure is using/evaluating a global variable,
we can create a new variable with the same name in the env tree that comes before global env.
Then, the second call to the same closure function will produce a different result.

```javascript
var a = "global";
{
    fun showA() { print a; }
    showA(); // prints "global";
    var a = "local"; 
    showA(); // prints "local";
}
```

In the above example, the first call to `showA()` looks up a in the global environment,
but when we define `var a = "local"` in the block scope, the subsequent call to `showA()`
will create the block scope env and add the local a to that scope.
The closure will look up the block scope env first and will find a.

Thus, 2 calls of the same function are referring to 2 different variables.
This is happening even though we do not mutate our variables. This is happening because
of scoping rules. When we create the env during `LoxFunction.call`, it is capturing
2 different states of the env that are not the same, even though the call of the function
itself is exactly the same.

#### Intuition of Block and Scope

Intuitively, we think of the _block_ and the _scope_ as the same thing. But as the above example shows,
that is not always the case.

- We think that all code in a block -> belonging to the same scope.
- We also represent 1 environment (internally a mutable hash table) for 1 scope.
- When a new local variable is created, it gets added to the existing environment for that scope.

This intuition is not quite right. **A block is not necessarily the same as the scope.**

{
    var a; // 1     <- Only a is in scope
    var b; // 2     <- Both a and b are in scope
}

By the above, the 2 declarations are not the same "scope". But in our impl,
env act like the entire block is one scope - it is just a scope that changes over time.

**closures do not like that**: when a function is declared, it *captures a frozen snapshot*
of the environment _as it existed at the moment the function was declared_.

But instead, in our Java implementation, the closure has a reference to a mutable environment
object, which gets modified over time.
    - So when a new variable is created, it updates the env that was supposedly captured
    - and hence, the call to the variable results in different result.
    - This isn't good for correctness, since our function call doesn't return predictable
      results, despite us not doing any mutations to the variables we captured!

### Persistent Environments

**Persistent Data Structures**: Essentially immutable data structures, which, when modified,
produce new instance copies. Original are left unchanged.

Thus, any var declaration would do a split/copy of the env, and we would have an old env and a new env.
The old env is what would be referred to by the closure, and the new one would be the latest env for the block.
This is a legit way to solve this problem (even though it sounds inefficient with all those copies)
- This is apparently the classic way to solve the problem in scheme interpreters.

### Semantic Analysis

Right now, we have dynamic resolution of variables. Every time we refer to a variable,
we go look it up in our env. Now imagine using a variable inside a for loop that runs 10K times.

Why are we needlessly resolving the variable again and again? Not only did this introduce the leak,
it is also slow.

Better approach: resolve each variable use _once_.

- Inspect the user's program.
- Find every variable mentioned.
- Figure out which declaration each variable refers to.

This process is an example of **semantic analysis**. The parser only tells if a program
is grammatically correct. With semantic analysis techniques, we can start to figure out
what the code _means_ - without executing that code.

In our case, our analysis will resolve variable bindings. We will know not only that an expression
is a variable, but _which variable_ it is. See? Semantic Analysis.

