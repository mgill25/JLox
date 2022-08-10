precedence based grammar
  - top->bottom, we have 
    lowest->highest precedence

```
expression   -> factor ( ("-" | "+") factor )* ;
factor       -> unary ( ( "/" | "*" ) unary )* ;
unary        -> ("!" | "-") unary | primary ;
primary      -> NUMBER | STRING | "true" | "false";
```

Everything is left associative (except assignment, but we don't support that yet, so nvm)
some grammars have trouble with left-recursion, it's why all our recursion happens after at least 1 match.

## JLox Grammar

```
expr            -> equality;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")"
```

### Adding Statements to the Grammar

```
program         -> statement* EOF ;
statement       -> exprStmt | printStmt ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;

expr            -> equality;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")"
```

### Accomodate for Declarations
```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | printStmt ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> equality;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Adding Support for Assignment

In most C-derived languages, assignment is an _expression_, not a statement.

```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | printStmt ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;

expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | equality ;

equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Block Syntax - Support for Lexical Scoping

```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | printStmt | block;
block           -> "{" declaration* "}" ;

exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | equality ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Conditional Execution
```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | ifStmt | printStmt | block;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;

block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | equality ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```
### Logical Operators

**Precedence** is why we did not add logical operators with Binary operators

```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | ifStmt | printStmt | block;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;

assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;

equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### While Loops


```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | ifStmt | printStmt | whileStmt | block;
whileStmt       -> "while" "(" expr ")" statement ;

ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### For Loops

```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | forStmt | ifStmt | printStmt | whileStmt | block;

forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment

whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | primary ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

- Also added in `break` as a keyword which breaks out of all loops at runtime.
- Also trying to add ++ and -- operators. What should be the precedence of these operators? I think it's the highest.
  - So in case of a++ + b, the a++ will evaluate first, and then the result would be added to b.
  - This makes the ++ an operator with higher than +/- precedence.
  - It is also a unary operator, come to think about it.
    - -a++ -> a is negated based on the old value (before increment) and that should be the result of the expression
      - while in the environment, the value should be incremented, so that future uses of a will get the newer value.
    - But if we have -++a, then a gets incremented first and that incremented value is negated
      - This suggests that ++ operator (at least pre-increment ++) has a higher precedence than unary.
- lets try and modify our grammar to include those

```
unary           -> ( "!" | "-") unary | incr_or_decr ;
incr_or_decr    -> primary ("++" | "--")* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```
### Function Calls

Parenthesis for callee have highest precedence because:
  - anything that is followed by `()` can be a call.
  - So, think of a call as a sort of postfix operator that starts with `(`
  - Following that analogy, it must be of very high precedence (just like ++ is)

`Example: getCallBack()();`. We have to move it higher than even unary, 
which has had the highest precedence so far.
  - `getCallBack` is the first callee
  - `getCallBack()` is evaluates to the second callee

```
program         -> declaration* EOF ;
declaration     -> varDecl | statement ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | whileStmt | block;
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;

unary           -> ( "!" | "-" ) unary | call ;
call            -> primary ( "(" arguments? ")" ) ;
arguments       -> expr ( "," expr )* ;


primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```
OR...
```
unary           -> ( "!" | "-") unary | incr_or_decr ;
incr_or_decr    -> primary ("++" | "--")* | call;
call            -> primary ( "(" arguments? ")" )*;
arguments       -> expr ( "," expr )*;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Function Declarations

Function declarations, like variables, bind a new name.
This means they are only allowed in places where declarations are permitted.

```
program         -> declaration* EOF ;

declaration     -> funDecl | varDecl | statement ;
funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;

varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | whileStmt | block;
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;
call            -> primary ( "(" arguments? ")" ) ;
arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Return Statements

We also need to get data out of our functions! If lox were an expression-oriented language like Ruby,
the body would be an expression whose value is implicitly the function's result.
But in com.gill.jlox.runtime.Lox, the body is a series of statements that do not produce a value.

Hence, we need an explicit return statement.

```
program         -> declaration* EOF ;
declaration     -> funDecl | varDecl | statement ;
funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;

statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"

forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;
call            -> primary ( "(" arguments? ")" ) ;
arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Lambdas

Can we simply define and run anonymous functions, without having to bind them
to names within the environment? These would be anonymous functions aka lambdas.
How can we implement lambdas?

```jsregexp
call(fun (arg) { });

lambda -> "fun" "(" arguments? ")" block;

// Now we know how a lambda looks like, the minimum thing to do would
// be to say that any function argument can also be a lambda.
// Javascript's naive lambda with function argument can also work in a similar way.

// So far, we have:
arguments       -> expr ( "," expr )* ;

This means arguments can be one or more expressions separated by comma.
We will simply extend this to mean: arguments can either be expressions or lambdas

arguments       -> exprOrLambda ( "," exprOrLambda )* ;
exprOrLambda    -> expr | lambda;
lambda          -> "fun" "(" arguments? ")" block;
```

Okay, so lets say we can parse lambdas. What would be next?
We can simply bind each lambda to the original function's parameter

### Class Declaration

com.gill.jlox.runtime.Lox has some OOP functionality built-in. You can write classes in com.gill.jlox.runtime.Lox. :)

```
program         -> declaration* EOF ;

declaration     -> classDecl | funDecl | varDecl | statement ;
classDecl       -> "class" IDENTIFIER "{" function* "}" ;

funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;
call            -> primary ( "(" arguments? ")" ) ;
arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```
### Properties on instances

Once we have class instances, we need to make them useful. Properties are accessed using dot `.` syntax.
We slot that into the grammar in our existing `call` rule.

```
program         -> declaration* EOF ;
declaration     -> classDecl | funDecl | varDecl | statement ;
classDecl       -> "class" IDENTIFIER "{" function* "}" ;
funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;

call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;

arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Set Expressions

Setters use the same syntax as Getters, except they appear on the left side of an assignment.
We extend our grammar production rule for assignment to allow dotted identifiers on the left-hand side.

```
program         -> declaration* EOF ;
declaration     -> classDecl | funDecl | varDecl | statement ;
classDecl       -> "class" IDENTIFIER "{" function* "}" ;
funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;

assignment      -> ( call "." )? IDENTIFIER "=" assignment | logic_or ;

logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;

call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;

arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Inheritance

We need a way to specify a superclass when declaring a class. There is a lot of variety of syntax for this.

C++ uses a colon, Java uses the `extends` keyword, Python puts superclasses in parentheses,
Simula puts superclass before the class name. This late in the game, we would rather not add new syntax/reserve keywords.
Lets just do what Ruby does and use the `<` symbol.

```
program         -> declaration* EOF ;
declaration     -> classDecl | funDecl | varDecl | statement ;

classDecl       -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;

funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> ( call "." )? IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;

call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;

arguments       -> expr ( "," expr )* ;
primary         -> NUMBER | STRING | "true" | "false" | "nil" | "(" expr ")" | IDENTIFIER ;
```

### Superclass method lookup via dot

We can't have bare super com.gill.jlox.tokens all by themselves (unlike this). The new clause is added to the primary
rule in our grammar for property access.

Oh, and somewhere along the way we had also added `this` to the primary as a reserve keyword.

```
program         -> declaration* EOF ;
declaration     -> classDecl | funDecl | varDecl | statement ;
classDecl       -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;
funDecl         -> "fun" function ;
function        -> IDENTIFIER "(" parameters? ")" block ;
parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
varDecl         -> "var" IDENTIFIER ( "=" expr )? ";" ;
statement       -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block;
returnStmt      -> "return" expr? ";"
forStmt         -> "for" "(" (varDecl | exprStmt | ";" )    // initializer
                    expr? ";"                               // condition
                    expr? ")" statement ;                   // increment
whileStmt       -> "while" "(" expr ")" statement ;
ifStmt          -> "if" "(" expr ")" statement ( "else" statement )? ;
block           -> "{" declaration* "}" ;
exprStmt        -> expr ";" ;
printStmt       -> "print" expr ";" ;
expr            -> assignment ;
assignment      -> ( call "." )? IDENTIFIER "=" assignment | logic_or ;
logic_or        -> logic_and ( "or" logic_and )* ;
logic_and       -> equality ( "and" equality )* ;
equality        -> comparison ( ("!=" | "==") comparison )* ;
comparison      -> term ( (">" | ">=" | "<" | "<=") term)* ;
term            -> factor ( ("-" | "+") factor )*;
factor          -> unary ( ( "/" | "*" ) unary )* ;
unary           -> ( "!" | "-" ) unary | call ;
call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
arguments       -> expr ( "," expr )* ;

primary         -> NUMBER ("++" | "--") 
                    | STRING 
                    | "true" | "false"
                    | "nil" | "this" 
                    | (" expr ")"
                    | IDENTIFIER 
                    | "super" "." IDENTIFIER;
```

# ...and the lexical grammar

```
NUMBER          -> DIGIT+ ( "." DIGIT+)? ;
STRING          -> "\"" <any-char-except "\"">* "\"" ;
IDENTIFIER      -> ALPHA ( ALPHA | DIGIT )* ;
ALPHA           -> "a" ... "z" | "A" ... "Z" | "_" ;
DIGIT           -> "0" ... "9" ;
```