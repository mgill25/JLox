package com.gill.jlox.ast.statements;

import com.gill.jlox.operations.BreakError;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

import java.util.List;

/**
 * Represents the AST Node for the function declaration
 */
public class FunStmt<T> implements BaseStmt<T> {
    public final Token funIdentifier;
    public final List<Token> parameters;
    public final List<BaseStmt> body;

    public FunStmt(Token funIdentifier, List<Token> parameters, List<BaseStmt> body) {
        this.funIdentifier = funIdentifier;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public Object accept(BaseStmtVisitor<T> visitor) throws RuntimeError, BreakError {
        return visitor.visitFunStmt(this);
    }
}
