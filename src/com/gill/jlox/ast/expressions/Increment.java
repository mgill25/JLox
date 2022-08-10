package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class Increment<T> implements BaseExpr<T> {
    public Token operandToken;
    public BaseExpr expr;

    public Increment(BaseExpr expr, Token token) {
        this.expr = expr;
        this.operandToken = token;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitIncrement(this);
    }
}
