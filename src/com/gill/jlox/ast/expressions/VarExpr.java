package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class VarExpr<T> implements BaseExpr<T> {

    public Token name;

    public VarExpr(Token name) {
        this.name = name;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitVarExpr(this);
    }
}
