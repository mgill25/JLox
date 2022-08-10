package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class AssignExpr<T> implements BaseExpr<T> {
    public Token name;
    public BaseExpr value;

    public AssignExpr(Token name, BaseExpr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitAssignExpr(this);
    }
}
