package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class Unary<T> implements BaseExpr<T> {
    public Token operator;
    public BaseExpr right;

    public Unary(Token operator, BaseExpr right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitUnary(this);
    }
}
