package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;

public interface BaseStmtVisitor<T> {
    T visitExpressionStmt(ExprStmt<T> tExprStmt) throws RuntimeError;
    void visitPrintStmt(PrintStmt<T> tPrintStmt) throws RuntimeError;

    T visitVarStmt(VarStmt<T> varStmt) throws RuntimeError;

    T visitBlockStmt(BlockStmt<T> tBlockStmt) throws RuntimeError, BreakError;

    T visitIfStmt(IfStmt<T> tIfStmt) throws RuntimeError, BreakError;

    T visitWhileStmt(WhileStmt<T> tWhileStmt) throws RuntimeError;

    T visitForStmt(ForStmt<T> tForStmt) throws RuntimeError, BreakError;

    T visitBreakStmt(BreakStmt<T> tBreakStmt) throws RuntimeError, BreakError;

    T visitFunStmt(FunStmt<T> tFunStmt) throws RuntimeError;

    T visitReturnStmt(ReturnStmt<T> tReturnStmt) throws RuntimeError;
}
