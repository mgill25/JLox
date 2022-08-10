package com.gill.jlox.runtime;

import com.gill.jlox.tokens.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    public Environment getEnclosing() {
        return enclosing;
    }

    public Environment() {
        // System.out.println("[debug] created top-level, no enclosing");
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
        // System.out.println("[debug] created Environment. Enclosing: " + enclosing);
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object getEnvByToken(Token token) throws RuntimeError {
        if (values.containsKey(token.lexeme)) {
            return values.get(token.lexeme);
        }
        if (enclosing != null) {
            // Walk up the food chain
            return enclosing.getEnvByToken(token);
        }
        throw new RuntimeError("Undefined variable '" + token.lexeme + '"');
    }

    public void assign(Token token, Object value) throws RuntimeError {
        if (values.containsKey(token.lexeme)) {
            values.put(token.lexeme, value);
            return;
        }
        if (enclosing != null) {
            // Walk up the food chain
            enclosing.assign(token, value);
            return;
        }
        throw new RuntimeError("Undefined variable '" + token.lexeme + '"');
    }

    public String toString() {
        return this.values.toString();
    }
}
