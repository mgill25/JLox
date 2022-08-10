package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;

/**
 * Visitor interface which will be extended
 * by various com.gill.jlox.types.operations implemented by our com.gill.jlox.types.
 * @param <T>
 */
public interface BaseVisitor<T> {
    T visitGrouping(Grouping tGrouping) throws RuntimeError;
    T visitUnary(Unary tUnary) throws RuntimeError;
    T visitLiteral(Literal tLiteral);
    T visitBinary(Binary tBinary) throws RuntimeError;

    T visitVarExpr(VarExpr<T> tVarExpr) throws RuntimeError;

    T visitAssignExpr(AssignExpr<T> tAssignExpr) throws RuntimeError;

    T visitLogicalExpr(LogicalExpr<T> tLogicalExpr) throws RuntimeError;

    T visitIncrement(Increment<T> tIncrement) throws RuntimeError;

    T visitDecrement(Decrement<T> tDecrement) throws RuntimeError;

    T visitCallExpr(FunCall<T> tFunCall) throws RuntimeError;

    T visitLambdaExpr(LambdaExpr<T> tLambdaExpr) throws RuntimeError;
}
