package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;

public class Grouping<T> implements BaseExpr<T> {

    public BaseExpr expr;
    public Grouping(BaseExpr expr) {
        this.expr = expr;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitGrouping(this);
    }
}
