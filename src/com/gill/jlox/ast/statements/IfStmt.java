package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.ast.expressions.BaseExpr;

public class IfStmt<T> implements BaseStmt<T> {
    public BaseExpr condition;
    public BaseStmt thenBranch;
    public BaseStmt elseBranch;

    public IfStmt(BaseExpr condition, BaseStmt thenBranch, BaseStmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitIfStmt(this);
    }
}
