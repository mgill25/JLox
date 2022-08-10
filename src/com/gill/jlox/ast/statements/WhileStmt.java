package com.gill.jlox.ast.statements;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.ast.expressions.BaseExpr;

public class WhileStmt<T> implements BaseStmt<T> {
    public final BaseExpr condition;
    public final BaseStmt body;

    public WhileStmt(BaseExpr cond, BaseStmt body) {
        this.condition = cond;
        this.body = body;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError {
        return visitor.visitWhileStmt(this);
    }
}
