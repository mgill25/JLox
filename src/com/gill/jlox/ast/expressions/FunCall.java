package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

import java.util.List;

/**
 * Represents the AST node for a Function call expression.
 */
public class FunCall<T> implements BaseExpr<T> {
    public final BaseExpr callee;
    public final Token closingParen;
    public final List<BaseExpr> args;

    public FunCall(BaseExpr callee, Token closingParen, List<BaseExpr> args) {
        this.callee = callee;
        this.closingParen = closingParen;
        this.args = args;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitCallExpr(this);
    }
}
