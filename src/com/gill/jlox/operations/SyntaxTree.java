package com.gill.jlox.operations;

import com.gill.jlox.ast.expressions.BaseExpr;
import com.gill.jlox.ast.statements.BaseStmt;

import java.util.List;

/**
 * This is just a wrapper class over the arrangement
 * of BaseExpr subtypes in a Tree structure.
 * The real meat is in the nodes themselves.
 * (This is just to make me feel better about the fact that the
 * top-down recursive descent parser does indeed produce a tree object!)
 */
public class SyntaxTree {
    public BaseExpr root;
    public List<BaseStmt> statements;
    SyntaxTree(BaseExpr root) {
        this.root = root;
    }

    SyntaxTree(List<BaseStmt> statements) {
        this.statements = statements;
    }
}
