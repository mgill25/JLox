package com.gill.jlox.ast.expressions;

import com.gill.jlox.ast.statements.FunStmt;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

public class LambdaExpr<T> implements BaseExpr<T> {
    public final FunStmt loxLambda;

    public void setParam(Token param) {
        this.param = param;
    }

    public Token param;
    public LambdaExpr(FunStmt loxLambda) {
        this.loxLambda = loxLambda;
    }

    @Override
    public T accept(BaseVisitor<T> visitor) throws RuntimeError {
        return visitor.visitLambdaExpr(this);
    }
}
