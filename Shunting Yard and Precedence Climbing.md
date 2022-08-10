/**
 * Recursive Descent Algorithm
 *  - Transform each non-terminal of the grammar into its own subroutine.
 *  - Left-recursive Grammar is unsuitable for recursive descent (productions lead to infinite recursion)
 *  - Transform left-recursive grammar into non-left-recursive (if needed)
 *
 * Shunting Yard algorithm (not implemented here)
 *  Idea: Keep 2 stacks: operatorStack and operandStack
 *  - operators in the operatorStack are pushed to the stack based on precedence
 *  - operators of higher precedence must live on top of stack, lowest at the bottom
 *  - before pushing a new operator onto the operatorStack, all higher precedence operators
 *    (and their corresponding operands) must be popped from their respective stacks.
 *      - make new nodes out of these popped operators and operands
 *      - push the newly created nodes on the operandStack
 *  - At the end of an expression, the remaining operators are put into the tree with their operands
 *
 *  2 algorithms for doing parsing:
 *     - Recursive Descent
 *     - Precedence Climbing
 *
 * - RD is easier to understand/write and requires less information
 * - But, RD has some problems!
 *
 * 1. Operator Associativity (only in BNF grammar). EBNF without left-recursion can solve this issue.
 * 2. Right associative operators might still pose a problem! Solved via power rule in EBNF
 * 3. Efficiency: Too many function calls just to descent and parse out single primary tokens.
 *     - Nesting is inefficient
 *     - Hybrid algorithm: Shunting Yard, which can help via 2 stacks that keep operators and operands.
 *
 * Shunting Yard algorithm by Dijkstra
 * You can write a hybrid parser which a) uses shunting yard to evaluate expressions,
 * and a top-down parser for statements.
 *
 * 4. Flexibility: Adding/Modifying the grammar requires changing around a lot of code, since the grammar is
 * tightly coupled to the code structure itself.
 *
 * Shunting Yard also solves that problem. But as per Eli Bendersky, the performance of shunting yard
 * was actually worse compared to top-down RD (Python implementation)
 *
 * (No clue why)
 *
 * Links:
 *
 * [RD Parser Algorithm Survey](https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm)
 * [Recursive Descent](https://eli.thegreenplace.net/2008/09/26/recursive-descent-ll-and-predictive-parsers/)
 * [Shunting Yard](https://eli.thegreenplace.net/2009/03/20/a-recursive-descent-parser-with-an-infix-expression-evaluator/)
 * [Precedence Climbing](https://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing)
 * [TDOP Parsing](https://eli.thegreenplace.net/2010/01/02/top-down-operator-precedence-parsing/)
 *
 * Precedence Climbing:
 *     - 1 recursive call per-binary-operator
 *     - Some calls are short-lived (while loop is never entered!)
 *     - Other calls are long-lived (while we go and parse nested operators of same or lower precedence)
 *     - Can handle both left and right associative operators in an elegent way
 *
 * Precedence Climbing algorithm is used by Clang. :O
 *
 * Interesting insight: _Pratt Parser and Precedence Climbing are the same algorithm,
 * just formulated a bit differently_ : http://www.oilshell.org/blog/2016/11/01.html
 */