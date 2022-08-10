package com.gill.jlox.ast.statements;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.ast.expressions.BaseExpr;

public class PrintStmt<T> implements BaseStmt<T> {
    public final BaseExpr expression;

    public PrintStmt(BaseExpr expr) {
        this.expression = expr;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError {
        visitor.visitPrintStmt(this);
        return null;
    }
}
