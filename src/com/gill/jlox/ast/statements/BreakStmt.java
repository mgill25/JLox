package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;

public class BreakStmt<T> implements BaseStmt<T> {
    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitBreakStmt(this);
    }
}
