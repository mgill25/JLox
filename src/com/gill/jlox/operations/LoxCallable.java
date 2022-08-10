package com.gill.jlox.operations;

import com.gill.jlox.runtime.RuntimeError;

import java.util.List;

public interface LoxCallable {
    Object call(Evaluator evaluator, List<Object> args) throws RuntimeError;

    int arity();
    boolean isVariadic();
}
