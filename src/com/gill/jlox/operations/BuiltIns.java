package com.gill.jlox.operations;

import com.gill.jlox.runtime.RuntimeError;

import java.util.List;

public class BuiltIns {

    // TODO: Make RawPrint support variadic arguments (how to do arity matching?)
    public static class RawPrint implements LoxCallable {

        private List<Object> args;

        @Override
        public Object call(Evaluator evaluator, List<Object> args) throws RuntimeError {
            this.args = args;
            for (Object arg: args) {
                System.out.print(arg);
            }
            System.out.println();
            return null;
        }

        @Override
        public int arity() {
            if (args != null) {
                return args.size();
            } else return 0;
        }

        @Override
        public String toString() { return "<native fn>"; }

        @Override
        public boolean isVariadic() {
            return true;
        }
    }
    public static class ScopeEnv implements LoxCallable {

        @Override
        public Object call(Evaluator evaluator, List<Object> args) throws RuntimeError {
            return evaluator.env;
        }

        @Override
        public int arity() {
            return 0;
        }

        @Override
        public String toString() { return "<native fn>"; }

        @Override
        public boolean isVariadic() {
            return false;
        }
    }
    public static class Clock implements LoxCallable {

        @Override
        public Object call(Evaluator evaluator, List<Object> args) {
            return System.currentTimeMillis() / 1000.0;
        }

        @Override
        public int arity() {
            return 0;
        }

        @Override
        public String toString() { return "<native fn>"; }

        @Override
        public boolean isVariadic() {
            return false;
        }
    }
}
