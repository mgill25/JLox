package com.gill.jlox.ast.statements;

import com.gill.jlox.ast.expressions.BaseExpr;
import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;

public class ReturnStmt<T> implements BaseStmt<T> {
    public final BaseExpr returnValue;

    public ReturnStmt(BaseExpr returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitReturnStmt(this);
    }
}
