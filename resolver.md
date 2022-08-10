# Resolver

Fundamentally, this will be a second pass
    - done after parsing
    - but before code execution

Once we have an AST, we can do multiple passes over it to do things such as:

- Scope Resolution
- Type Checking
- Optimizations

before sending off the final form of the AST to be executed.

## Resolver

Our resolver is kind of like a mini-interpreter, with a few key differences

1. No side-effects. (eg. print statement does nothing)
2. No control-flow 
   1. for loop is looked at only once. 
   2. Both branches of if-else are inspected...
   3. Logical operators do not short-circuit
   4. (So basically we look at all possible code states in the AST without running it)

### Interesting nodes for resolver

1. BlockStmt: introduces new scope for stmt it contains
2. FunDecl  : introduces new scope for its body. binds parameters in that scope.
3. VarDecl  : adds a new variable to current scope.
4. Var and Assign Expr: need to have their variables resolved.

#### Representation of Scopes

We solve the problem of closure scope mutation by keeping track of the distance.
**Key idea**: Ensure a variable lookup always walks the same number of links in the
parent-pointer tree.

**Resolve**: Calculate how many "hops" away the declared variable will be in the env chain.

Interesting question: When to do this? Surely before the execution, and because
this is a static property based on the structure of the code, during parsing.

But because we want to also show the fact that we can do multiple passes over the AST,
we use a dedicated second pass just for resolution.


