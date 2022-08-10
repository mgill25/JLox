package com.gill.jlox.runtime;

public class RuntimeError extends Exception {
    public RuntimeError(String msg) {
        super(msg);
    }
}
