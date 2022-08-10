package com.gill.jlox.ast.expressions;

import com.gill.jlox.runtime.RuntimeError;

public interface BaseExpr<T> {
    T accept(BaseVisitor<T> visitor) throws RuntimeError;
}
