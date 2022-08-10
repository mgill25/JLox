package com.gill.jlox.operations;

public class ReturnObjectException extends RuntimeException {
    public final Object value;

    public ReturnObjectException(Object value) {
        super(null, null, false, false); // disable some JVM machinery that we don't need.
        this.value = value;
    }
}
