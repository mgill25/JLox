package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.ast.expressions.BaseExpr;

public class ForStmt<T> implements BaseStmt<T> {
    public BaseStmt body;
    public BaseExpr condition;

    ForStmt(BaseExpr condition, BaseStmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitForStmt(this);
    }
}
