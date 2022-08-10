package com.gill.jlox.ast.statements;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.ast.expressions.BaseExpr;

// Statement wrapping an expression.
public class ExprStmt<T> implements BaseStmt<T> {
    public final BaseExpr expression;

    public ExprStmt(BaseExpr expr) {
        this.expression = expr;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError {
        return visitor.visitExpressionStmt(this);
    }
}
