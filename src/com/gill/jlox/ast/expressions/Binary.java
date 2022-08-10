package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class Binary<T> implements BaseExpr<T> {
    public BaseExpr<T> left;
    public BaseExpr<T> right;
    public Token operator;

    public Binary(BaseExpr left, Token operator, BaseExpr right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) {
        try {
            return visitor.visitBinary(this);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }
}
