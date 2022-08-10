package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class LogicalExpr<T> implements BaseExpr<T> {
    public BaseExpr lhs;
    public BaseExpr rhs;
    public Token operator;

    public LogicalExpr(BaseExpr lhs, Token operator, BaseExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }
    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitLogicalExpr(this);
    }
}
