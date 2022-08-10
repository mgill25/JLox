package com.gill.jlox.ast.statements;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;
import com.gill.jlox.ast.expressions.BaseExpr;

public class VarStmt<T> implements BaseStmt<T> {
    public Token name;
    public BaseExpr initializer;

    public VarStmt(Token name, BaseExpr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError {
        return visitor.visitVarStmt(this);
    }
}
