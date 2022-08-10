package com.gill.jlox.operations;

import com.gill.jlox.LoxKeywords;
import com.gill.jlox.tokens.Token;
import com.gill.jlox.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.gill.jlox.operations.Lexer/Scanner of the source code in JLox.
 * com.gill.jlox.operations.Lexer scans the token character-by-character (sometimes doing
 * lookaheads) and produces a list of com.gill.jlox.tokens.
 */
public class Lexer {

    String src;
    Integer startIdx = 0;
    Integer currentIdx = 0;
    Integer line = 1;
    Integer sourceSize = 0;
    List<Token> tokenStream;

    static final Map<String, TokenType> keywords = LoxKeywords.keywords;

    public List<Token> scan(String src) throws LexerError {
        this.tokenStream = new ArrayList<>();
        this.src = src;
        this.sourceSize = this.src.length();
        this.currentIdx = 0;
        while (this.currentIdx < this.sourceSize) {
            addNextToken();
            this.startIdx = this.currentIdx;
        }
        this.tokenStream.add(new Token("", TokenType.EOF, null, this.line));
        return this.tokenStream;
    }

    private void addNextToken() throws LexerError {
        char c = advance();
        switch (c) {
            // let the fun begin
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-':
                addToken(currentMatch('-') ? TokenType.MINUS_MINUS: TokenType.MINUS);
                break;
            case '+':
                addToken(currentMatch('+') ? TokenType.PLUS_PLUS : TokenType.PLUS);
                break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '%': addToken(TokenType.PERCENT); break;
            // 2-char com.gill.jlox.tokens!
            case '!':
                addToken(currentMatch('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(currentMatch('=') ? TokenType.EQUAL_EQUAL: TokenType.EQUAL);
                break;
            case '<':
                addToken(currentMatch('=') ? TokenType.LESS_EQUAL: TokenType.LESS);
                break;
            case '>':
                TokenType tt = currentMatch('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER;
                addToken(tt);
                break;
            case '&':
                addToken(currentMatch('&') ? TokenType.AND : null);
                break;
            case '|':
                addToken(currentMatch('|') ? TokenType.OR : null);
                break;
            // Comments
            case '/':
                addTokenOrComment(TokenType.SLASH);
                break;
            // Whitespace
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                this.line++;
                break;
            case '"':
                addString();
                break;
            default: {
                if (isDigit(c)) {
                    addNumber();
                } else if (isAlphaNumeric(c)) {
                    addIdentifier();
                } else {
                    throw new Lexer.LexerError(line + ": Unexpected Character: '" + c + "'");
                }
            }
        }
    }

    private void addString() throws LexerError {
        while (peek() != '"' && !isEnd()) {
            if (peek() == '\n') this.line++;
            advance();
        }
        if (isEnd()) {
            throw new Lexer.LexerError(this.line + ": Unterminated string.");
        } else {
            // Consume the terminating quote symbol
            advance();
        }
        String stringValue = this.src.substring(this.startIdx + 1, this.currentIdx - 1);
        addToken(TokenType.STRING, stringValue);
    }

    private boolean currentMatch(char expected) {
        if (isEnd()) return false;
        if (this.src.charAt(this.currentIdx) != expected) {
            return false;
        }
        advance();
        return true;
    }

    private void addIdentifier() {
        while (isAlphaNumeric(peek()))
            advance();
        String text = src.substring(startIdx, currentIdx);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
       return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    // Parse an integer or a float from source code
    private void addNumber() {
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(lookAhead())) {
            advance();
            while (isDigit(peek())) advance();
        }
        // start...current now encapsulates the token which we think will represent the number.
        String substr = src.substring(this.startIdx, this.currentIdx);
        addToken(TokenType.NUMBER, substr);
    }

    private char lookAhead() {
        if (this.currentIdx + 1 > this.sourceSize) return '\0';
        return this.src.charAt(this.currentIdx + 1);
    }

    private char advance() {
        return this.src.charAt(this.currentIdx++);
    }

    private char peek() {
        if (isEnd()) return '\0';
        return this.src.charAt(this.currentIdx);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Forward slash can represent:
     *  // like single-line comment beginning
     *  Or a regular mathematical division binary operator
     * @param slash
     */
    private void addTokenOrComment(TokenType slash) {
        char nextChar = peek();
        if (nextChar == '/') {
            while (peek() != '\n' && !isEnd()) {
                advance();
            }
        } else {
            addToken(slash);
        }
    }

    private boolean isEnd() {
        return this.currentIdx >= this.sourceSize;
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }
    private void addToken(TokenType tokenType, Object literal) {
        if (tokenType == null) return;
        String substring = this.src.substring(this.startIdx, this.currentIdx);
        if (substring.isEmpty()) return;
        Token t = new Token(substring, tokenType, literal, this.line);
        this.tokenStream.add(t);
    }

    public class LexerError extends Exception {
        public LexerError(String s) {
            super(s);
        }
    }
}
