package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;

import java.util.List;

public class BlockStmt<T> implements BaseStmt<T> {

    public final List<BaseStmt> statements;

    public BlockStmt(List<BaseStmt> statements) {
        this.statements = statements;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitBlockStmt(this);
    }
}
