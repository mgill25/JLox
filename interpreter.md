# Interpreter

Seems like the dedicated Interpreter class is also heavily dependent upon the
Visitor pattern. Same with the Parenthesize (ASTPrinter) class.

The idea, then is that if we want to do _something_ with the AST nodes,
what we *would* like is to have dedicated classes.

But because that can only happen with the Visitor pattern, what we have right
now is the exact problem the Visitor pattern tries to solve: `The Expression Problem`.

## The Expression Problem

We have 2 set of things:
- Types
- Operations

Sometimes, we need to add more com.gill.jlox.ast.operations to all existing com.gill.jlox.ast.
Sometimes, we need to add more com.gill.jlox.ast and make sure all com.gill.jlox.ast.operations work on them.

However, sometimes we need both - and herein lies the problem.
Most mainstream programming languages do not provide good tools to add both new com.gill.jlox.ast,
and new com.gill.jlox.ast.operations to an existing system _without having to change existing code_.

Studying the Expression Problem gives us insight into fundamental differences between
object-oriented and functional programming, as well as concepts such as interfaces,
and multiple dispatch.

```c++
class Expr {
public:
    virtual std::string ToString() const = 0;
    virtual double Eval() const = 0;
}

class Constant : public Expr {
public:
    Constant(double value) : value_(value) {}
    std::string ToString() const {
        // some code
    }
    double Eval() const {
        return value_;
    }
}
private:
    double value_;
```

- Adding new com.gill.jlox.ast is fairly easy with this design. We just need to inherit from the
base Expr class and implement them.
- But what about when we want to add new com.gill.jlox.ast.operations?
  - Right now we support `ToString()` and `Eval()`.
  - What if we want to support more com.gill.jlox.ast.operations like 
    - `TypeCheck()` or 
    - `Serialize()` or 
    - `CompileToMachineCode()`
    - or whatever
 
Adding new com.gill.jlox.ast.operations is not as easy. We would have to
- Change the `Expr` interface
- Consequently, change every existing expression type to support the new method(s)
- If we don't control the original code or it's hard to change for some other reason,
  - We are in trouble.
  
- In other words, we would have to _violate_ the **Open-Close Principle**
  - Which is one of the main principles of Object Oriented Design.
  - Which says: Software Entities (classes, modules, functions) etc should be
    - **Open** for Extension, but
    - **Closed** for Modification
    - Which basically means an Entity should allow the extension of its behaviour
    **without** modification of its source code.
  

The expression problem bites functional programming languages as well!

OOP tends to collect functionality in objects (com.gill.jlox.ast). Functional languages 
collect functionality in, well, _functions_ (com.gill.jlox.ast.operations). Types are usually thin
data containers.

The functional paradigm does not escape the expression problem. It just manifests
differently there.

```haskell
module Expression where
   
data Expr = Constant Double
          | BinaryPlus Expr Expr
          
stringify :: Expr -> String
stringify (Constant c) = show c
stringify (BinaryPlus lhs rhs) = stringify lhs ++ " + " ++ stringify rhs

evaluate :: Expr -> Double
evaluate (Constant c) = c
evaluate (BinaryPlus lhs rhs) = evaluate lhs + evaluate rhs
```

### Dangling Else Problem

Given an expression like so:

`if (foo) if (bar) doBlah() else doSomethingElse()`

Which `if` clause should the `else` match with? The answer will result in different semantics.

- Scenario 1: `else` matches the _inner_ `if`. In this case, 
  - `doSomethingElse()` will only execute 
    - if `foo` evaluates to True and `bar` evaluates to False.
- Scenario 2: `else` matches the _outer_ `if`. In this case,
  - `doSomethingElse()` will execute if `foo` evaluates to False.

This makes the grammar _ambiguous_!

Most programming language stick to the convention of matching else with the innermost if.
(AKA Scenario 1).

This is also easily done in our parser by eagerly consuming the else when we parse an if.
That means all else clauses stick to their nearest if.

### De-Sugaring

This is essentially the process of re-representing a programming construct of the language
into some other construct that the backend already understands.

For example: If we have already implemented `while` loop support in Lox, adding support for
`for` loops should be fairly trivial - as we can re-use the while-loop machinery. We can, in other words,
_desugar_ a for loop into a while loop, and simply execute the while loop like we normally would.

for loop construct has the following things:
  - initializer
  - condition
  - incremental

key insight: all 3 of them are optional. we can choose not to have any of them, and then our
for loop would simply be an infinite while loop.

initialization can be of various kinds:
  - either it is simply an assignment of variable that was already declared
  - OR it is a completely new variable declaration. In this case, the variable will remain
  - alive for the scope of the loop itself and _should_ stop existing afterwards.
    - lets check

Adding support for `break` statement:
  - Break is a _statement_
  - But it is a conditional statement : It makes no sense to have break when we are not inside
  - the body of a loop.
  - Should we represent this condition as part of our grammar, or should it just be baked into the
  - implementation with the conditionality being implicit?
  - Lets start with the easy thing: just bake it as any regular statement