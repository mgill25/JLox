package com.gill.jlox.operations;

import com.gill.jlox.ast.statements.FunStmt;
import com.gill.jlox.runtime.Environment;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;

import java.util.List;

/**
 * Q: Why does LoxFunction exist when we have FunStmt for our implementation,
 * just like we have been doing so all this time?
 * A: FunStmt is basically a place where we keep track of parameters, body etc
 * so we can bind the parameters to argument values and execute the body.
 *
 * But if we go back to our caller implementation, we had LoxCallable interface
 * defined, which provides the spec of how we _actually call_ the function.
 *
 * Imp: We don't want the runtime phase to bleed into the front-end's syntax
 * classes. So we don't want FunStmt itself to implement the calling functionality.
 *  - Seems like it's done as a sort of bridge and/or proxy
 *  - We can of course, also call it the Function object. :-)
 */
public class LoxFunction implements LoxCallable {

    public final FunStmt declaration;
    private final Environment closure;
    private final String funcName;
    public final Boolean isLambda;

    public LoxFunction(FunStmt declaration, Environment closure, Boolean isLambda) {
        this.declaration = declaration;
        this.closure = closure;
        this.isLambda = isLambda;
        if (!isLambda)
            this.funcName = declaration.funIdentifier.lexeme;
        else
            this.funcName = "anonFn";
        // System.out.println("closure for " + declaration.funIdentifier.lexeme + " = " + closure);
    }

    @Override
    public Object call(Evaluator evaluator, List<Object> args) throws RuntimeError {
        Environment functionEnv = new Environment(closure);
        for (int i = 0; i < this.declaration.parameters.size(); ++i) {
            Token currParam = (Token) this.declaration.parameters.get(i);
            functionEnv.define(currParam.lexeme, args.get(i));
        }
        try {
            evaluator.executeBlock(this.declaration.body, functionEnv);
        } catch (ReturnObjectException returnValue) {
            return returnValue.value;
        } catch (BreakError e) {
            throw new RuntimeError("This should never happen - you're doing something wrong");
        }
        return null;
    }

    @Override
    public int arity() {
        return this.declaration.parameters.size();
    }

    @Override
    public boolean isVariadic() {
        return false;
    }

    @Override
    public String toString() {
        return "<fn:" + this.funcName + ">";
    }
}
