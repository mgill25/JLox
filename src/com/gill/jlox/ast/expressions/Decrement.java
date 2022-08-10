package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class Decrement<T> implements BaseExpr<T> {

    public BaseExpr expr;
    public Token token;
    public Decrement(BaseExpr expr, Token token) {
        this.expr = expr;
        this.token = token;
    }
    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitDecrement(this);
    }
}
