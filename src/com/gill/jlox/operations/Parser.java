package com.gill.jlox.operations;

import com.gill.jlox.runtime.Lox;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;
import com.gill.jlox.tokens.TokenType;
import com.gill.jlox.ast.expressions.*;
import com.gill.jlox.ast.statements.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gill.jlox.tokens.TokenType.*;


public class Parser {
    public static class ParseError extends RuntimeException {
        ParseError(String msg) {
            super(msg);
        }
    }
    List<Token> tokenStream;
    private int currentIndex = 0;
    private boolean insideLoop = false;

    public SyntaxTree parse(List<Token> tokenStream) throws ParseError {
        this.currentIndex = 0; // Ensure we always start from the beginning
        this.tokenStream = tokenStream;
        // BaseExpr root = equality();  // Evolving from Expr-based language to Statement-based language
        List<BaseStmt> program = new ArrayList<>();
        // A program is just a list of statements.
        while (!isAtEnd()) {
            BaseStmt statement = declaration();
            if (statement == null) {
                System.out.println("Found an null/illegal statement. What is going on?");
            }
            program.add(statement);
        }
        // System.out.println("[");
        // program.forEach(System.out::println);
        // System.out.println("]");
        return new SyntaxTree(program);
    }

    private BaseStmt declaration() {
        if (nextMatch(FUN)) return funDeclaration(false);
        if (nextMatch(VAR)) return varDeclaration();
        else {
            return statement();
        }
    }

    private FunStmt funDeclaration(Boolean isLambda) {
        // 1. Identifier
        Token funIdentifier = null;
        if (!isLambda && nextMatch(IDENTIFIER)) {
            funIdentifier = prev();
        }
        // 2. Parameters
        // Lets start without currying
        List<Token> parameters = new ArrayList<>();
        consume(LEFT_PAREN, "Expect '(' after function name in declaration");
        if (!nextMatch(RIGHT_PAREN)) {
            // Function has at least 1 parameter. Lets start parsing...
            int argLimit = 255;
            if (nextMatch(IDENTIFIER))
                parameters.add(prev());

            while (nextMatch(COMMA)) {
                if (nextMatch(RIGHT_PAREN)) break;
                if (parameters.size() > argLimit) {
                    throw new ParseError("Can't have more than " + argLimit + " arguments!");
                }
                if (nextMatch(IDENTIFIER))
                    parameters.add(prev());
            }
            consume(RIGHT_PAREN, "Expect ')' after function parameters in declaration");
        }
        // 3. Function Body
        consume(LEFT_BRACE, "Expect '{' before function body");
        List<BaseStmt> body = block();
        return new FunStmt(funIdentifier, parameters, body);
    }

    private BaseStmt varDeclaration() {
        consume(IDENTIFIER, "Expected variable name here!");
        Token identifierToken = prev();
        BaseExpr initializer = null;
        if (nextMatch(EQUAL)) {
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration");
        return new VarStmt(identifierToken, initializer);
    }

    private BaseStmt statement() {
        if (nextMatch(IF)) {
            return ifStatement();
        } else if (nextMatch(PRINT)) {
            return printStatement();
        } else if (nextMatch(BREAK)) {
            if (!insideLoop) {
                throw new RuntimeException("Break cannot occur outside for loop");
            }
            return breakStatement();
        } else if (nextMatch(WHILE)) {
            insideLoop = true;
            return whileStatement();
        } else if (nextMatch(FOR)) {
            insideLoop = true;
            return forStatement();
        } else if (nextMatch(RETURN)) {
            return returnStatement();
        } else if (nextMatch(LEFT_BRACE)) {
            return new BlockStmt(block());
        } else {
            return expressionStatement();
        }
    }

    private BaseStmt returnStatement() {
        BaseExpr returnValue = null;
        if (!checkCurrentTokenType(SEMICOLON)) {
            returnValue = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value");
        return new ReturnStmt(returnValue);
    }

    private BaseStmt breakStatement() {
        consume(SEMICOLON, "Expect ';' after break");
        return new BreakStmt();
    }

    private BaseStmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'");
        BaseStmt initializer;
        // for loop can have many kinds of initializations
        if (nextMatch(SEMICOLON)) {
            initializer = null;
        } else if (nextMatch(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        BaseExpr condition = null;
        if (!checkCurrentTokenType(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after the loop condition");
        BaseExpr increment = null;
        if (!checkCurrentTokenType(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after 'for' clause");
        BaseStmt body = statement();
        // Now we have everything. We just need to desugar
        if (increment != null) {
            body = new BlockStmt(Arrays.asList(
                body,
                new ExprStmt(increment)
            ));
        }
        if (condition == null) {
            condition = new Literal(true);
        }
        body = new WhileStmt(condition, body);
        if (initializer != null) {
            body = new BlockStmt(Arrays.asList(initializer, body));
        }
        insideLoop = false;
        return body;
    }

    private BaseStmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'");
        BaseExpr cond = expression();
        consume(RIGHT_PAREN, "Expect ')' after 'while condition");
        BaseStmt body = statement();
        insideLoop = false;
        return new WhileStmt(cond, body);
    }

    private BaseStmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after if");
        BaseExpr cond = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition");
        BaseStmt thenBranch = statement();
        BaseStmt elseBranch = null;
        if (nextMatch(ELSE)) {
            elseBranch = statement();
        }
        return new IfStmt(cond, thenBranch, elseBranch);
    }

    private List<BaseStmt> block() {
        List<BaseStmt> statements = new ArrayList<>();
        while (!checkCurrentTokenType(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private BaseStmt printStatement() {
        BaseExpr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression: " + expr);
        return new PrintStmt(expr);
    }

    private BaseStmt expressionStatement() {
        BaseExpr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression: " + expr);
        return new ExprStmt(expr);
    }

    private BaseExpr expression() {
        return assignment();
    }

    private BaseExpr assignment() {
        BaseExpr expr = or();
        if (nextMatch(EQUAL)) {
            BaseExpr value = assignment();
            if (expr instanceof VarExpr) {
                Token name = ((VarExpr)expr).name;
                return new AssignExpr(name, value);
            }
            throw new RuntimeException("Invalid assignment target");
        }
        return expr;
    }

    private BaseExpr or() {
        BaseExpr expr = and();
        while (nextMatch(OR)) {
            Token operator = prev();
            BaseExpr right = and();
            expr = new LogicalExpr(expr, operator, right);
        }
        return expr;
    }

    private BaseExpr and() {
        BaseExpr expr = equality();
        while (nextMatch(AND)) {
            Token operator = prev();
            BaseExpr right = equality();
            expr = new LogicalExpr(expr, operator, right);
        }
        return expr;
    }


    private boolean isAtEnd() {
        return peekToken() != null && peekToken().tokenType == EOF;
    }

    private BaseExpr equality() {
        BaseExpr left = comparison();
        while (nextMatch(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = prev();
            BaseExpr right = comparison();
            left = new Binary(left, operator, right);
        }
        return left;
    }

    private BaseExpr comparison() {
        BaseExpr left = term();
        while (nextMatch(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = prev();
            BaseExpr right = term();
            left = new Binary(left, operator, right);
        }
        return left;
    }

    private BaseExpr term() {
        BaseExpr left = factor();
        while (nextMatch(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = prev();
            BaseExpr right = factor();
            left = new Binary(left, operator, right);
        }
        return left;
    }

    private BaseExpr factor() {
        BaseExpr left = unary();
        while (nextMatch(TokenType.SLASH, TokenType.STAR, PERCENT)) {
            Token operator = prev();
            BaseExpr right = unary();
            left = new Binary(left, operator, right);
        }
        return left;
    }

    private BaseExpr unary() {
        if (nextMatch(TokenType.BANG, TokenType.MINUS)) {
            Token operator = prev();
            BaseExpr right = unary();
            return new Unary(operator, right);
        }
        return IncrementOrDecrement();
    }

    private BaseExpr IncrementOrDecrement() {
        BaseExpr left = primary();
        Token operand = prev();
        if (nextMatch(PLUS_PLUS)) {
            return new Increment(left, operand);
        } else if (nextMatch(MINUS_MINUS)) {
            return new Decrement(left, operand);
        } else {
            // The outer loop supports currying
            while (true) {
                if (nextMatch(LEFT_PAREN)) {
                    left = parseArgsAndCreateCall(left, operand);
                } else {
                    break;
                }
            }
        }
        return left;
    }

    private FunCall parseArgsAndCreateCall(BaseExpr left, Token operand) {
        List<BaseExpr> arguments = new ArrayList<>();
        if (!checkCurrentTokenType(RIGHT_PAREN)) {
            BaseExpr argument = expressionOrLambda();
            arguments.add(argument);
        }
        int argLimit = 255;
        while (nextMatch(COMMA)) {
            if (arguments.size() > argLimit - 1) {
                throw new ParseError("Can't have more than " + argLimit + " arguments!");
            }
            BaseExpr nextArg;
            try {
                nextArg = expressionOrLambda();
            } catch (ParseError e) {
                String fnName = operand.lexeme;
                throw new ParseError("Error: No valid argument provided after ',' in function call '" + fnName + "'");
            }
            arguments.add(nextArg);
            if (checkCurrentTokenType(RIGHT_PAREN)) break;
        }
        consume(RIGHT_PAREN, "Expect ')' after function arguments");
        return new FunCall(left, prev(), arguments);
    }

    private BaseExpr expressionOrLambda() {
        if (nextMatch(FUN)) {
            FunStmt lambda = lambda();
            return new LambdaExpr(lambda);
        }
        return expression();
    }

    private FunStmt lambda() {
        return funDeclaration(true);
    }

    private boolean nextMatch(TokenType... types) {
        if (this.currentIndex >= this.tokenStream.size()) return false;
        for (TokenType type : types) {
            if (checkCurrentTokenType(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean checkCurrentTokenType(TokenType type) {
        if (this.currentIndex >= this.tokenStream.size()) return false;
        Token current = this.tokenStream.get(this.currentIndex);
        return current.tokenType.equals(type);
    }

    private void advance() {
        if (this.currentIndex < this.tokenStream.size()) {
            this.currentIndex++;
        }
    }

    private BaseExpr primary() {
        if (nextMatch(TokenType.FALSE)) return new Literal(false);
        if (nextMatch(TokenType.TRUE)) return new Literal(true);
        if (nextMatch(TokenType.NIL)) return new Literal(null);
        if (nextMatch(TokenType.NUMBER, TokenType.STRING)) {
            return new Literal(prev().literal);
        }

        if (nextMatch(IDENTIFIER)) {
            return new VarExpr(prev());
        }

        // Handle parenthesis grouping
        if (nextMatch(TokenType.LEFT_PAREN)) {
            BaseExpr expr = term();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Grouping(expr);
        }
        throw new ParseError("ParserError: Cannot parse the expression: " + peekToken().lexeme);
    }

    private void consume(TokenType tokenType, String msg) {
        Token peekedToken = peekToken();
        if (peekedToken == null) {
            throw new ParseError(msg); // TODO
        }
        if (peekToken().tokenType == tokenType) {
            advance();
            return;
        }
        System.out.println("Tried to consume: '" + tokenType + "' but got: '" + peekToken().lexeme + "'");
        throw parseError(peekToken(), msg); // TODO
    }

    private ParseError parseError(Token token, String message) {
        Lox.error(token, message);
        return new ParseError(message);
    }


    private Token prev() {
        return this.tokenStream.get(this.currentIndex - 1);
    }

    // Iterative Algorithm to parse binary expressions: precedence climbing
    // Assumes com.gill.jlox.tokens know (or can deduce) their precedence and associativity
    SyntaxTree parseIterative(List<Token> tokenStream) {
        try {
            this.currentIndex = 0; // Ensure we always start from the beginning
            this.tokenStream = tokenStream;
            BaseExpr root = precedenceClimbing(parsePrimary(), 0);
            return new SyntaxTree(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BaseExpr parsePrimary() {
        Token nextToken = this.tokenStream.get(currentIndex++);
        if (nextToken.tokenType == TokenType.NUMBER) {
            return new Literal(prev().literal);
        }
        throw new RuntimeException("Unexpected symbol: " + prev().lexeme);
    }

    private BaseExpr precedenceClimbing(BaseExpr left, Integer minPrecedence) {
        while (true) {
            Token op = peekToken();
            if (!isOp(op) || op.precedence < minPrecedence) {
                break;
            }
            this.currentIndex++;
            BaseExpr right = parsePrimary();
            while (true) {
                Token lookahead = peekToken();
                if (!isOp(lookahead) ||
                    lookahead.precedence > op.precedence ||
                    lookahead.assoc == Associativity.RIGHT &&
                    lookahead.precedence.equals(op.precedence)
                ) {
                    break;
                } else {
                    right = precedenceClimbing(right, lookahead.precedence);
                }
            }
            return new Binary(left, op, right);
        }
        return left;
    }

    private boolean isOp(Token op) {
        return op != null && (
                op.tokenType == PLUS
                || op.tokenType == MINUS
                || op.tokenType == STAR
                || op.tokenType == SLASH
                || op.tokenType == PERCENT
        );
    }

    private Token peekToken() {
        if (this.currentIndex >= this.tokenStream.size()) {
            // throw new RuntimeException("Too far ahead, out of bounds");
            return null;
        }
        return this.tokenStream.get(this.currentIndex);
    }

    public static void main(String[] args) {
        // Create a stream of com.gill.jlox.tokens: -2 + 3 * -4
        List<Token> tokenStream = genTokenStream();
        tokenStream.forEach(System.out::println);
        Parser parser = new Parser();

        List<Token> simpleTokenStream = new ArrayList<>();
        simpleTokenStream.add(new Token("2", TokenType.NUMBER, 2, 1));
        simpleTokenStream.add(new Token("+", TokenType.PLUS, null, 1));
        simpleTokenStream.add(new Token("3", TokenType.NUMBER, 3, 1));
        SyntaxTree tree = parser.parse(simpleTokenStream);
        System.out.println(tree.root);

        List<Token> secondStream = genBoolTokenStream();
        SyntaxTree secondTree = parser.parse(secondStream);
        System.out.println(secondTree.root);

        Evaluator evaluator = new Evaluator();
        Object result = null;
        try {
            result = evaluator.visitUnary((Unary) secondTree.root);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
        System.out.println("Eval result = " + result);
    }

    public static List<Token> genTokenStream() {
        List<Token> tokenStream = new ArrayList<Token>();
        tokenStream.add(new Token("-", TokenType.MINUS, null, 1));
        tokenStream.add(new Token("2", TokenType.NUMBER, 2, 1));
        tokenStream.add(new Token("+", TokenType.PLUS, null, 1));
        tokenStream.add(new Token("3", TokenType.NUMBER, 3, 1));
        tokenStream.add(new Token("*", TokenType.STAR, null, 1));
        tokenStream.add(new Token("-", TokenType.MINUS, null, 1));
        tokenStream.add(new Token("4", TokenType.NUMBER, 4, 1));
        return tokenStream;
    }

    public static List<Token> genBoolTokenStream() {
        List<Token> tokenStream = new ArrayList<Token>();
        tokenStream.add(new Token("!", TokenType.BANG, null, 1));
        tokenStream.add(new Token("true", TokenType.TRUE, true, 1));
        return tokenStream;
    }
}
