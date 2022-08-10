package com.gill.jlox.tokens;

import com.gill.jlox.operations.Associativity;

public class Token {
    public Object literal;
    public Integer line;
    public String lexeme;
    public TokenType tokenType;

    public Associativity assoc;
    public Integer precedence;

    public Token(String lexeme, TokenType tokenType, Object literal, Integer line) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.literal = literal;
        this.line = line;
        if (lexeme.equals("^")) {
           this.assoc = Associativity.RIGHT;
        } else {
            this.assoc = Associativity.LEFT;
        }
        this.precedence = getOperatorPrecedence();
    }

    private Integer getOperatorPrecedence() {
        return switch (this.lexeme) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }

    @Override
    public String toString() {
        return "<Token type=" + this.tokenType + ",lexeme=" + this.lexeme + ", literal=" + this.literal + ">";
        // return this.lexeme;
    }
}
