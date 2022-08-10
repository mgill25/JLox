package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;

public interface BaseStmt<T> {
    Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError;
}
