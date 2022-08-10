package com.gill.jlox.ast.expressions;

public class Literal<T> implements BaseExpr<T> {
    public Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
