package com.gill.jlox.operations;

import com.gill.jlox.runtime.Environment;
import com.gill.jlox.runtime.RuntimeError;
import com.gill.jlox.tokens.Token;
import com.gill.jlox.tokens.TokenType;
import com.gill.jlox.ast.expressions.*;
import com.gill.jlox.ast.statements.*;

import java.util.ArrayList;
import java.util.List;

public class Evaluator implements BaseVisitor, BaseStmtVisitor {

    public Environment globalEnv = new Environment();
    public Environment env = globalEnv;
    private boolean insideLoop = false;

    public Evaluator() {
        BuiltIns builtIns = new BuiltIns();
        env.define("clock", new BuiltIns.Clock());
        env.define("env", new BuiltIns.ScopeEnv());
        env.define("println", new BuiltIns.RawPrint());
    }

    public Object evaluate(BaseExpr expr) throws RuntimeError {
        return expr.accept(this);
    }

    public Void execute(BaseStmt stmt) throws RuntimeError, BreakError {
        stmt.accept(this);
        return null;
    }

    public Object evaluate(List<BaseStmt> statements) throws RuntimeError {
        Object lastResult = null;
        for (BaseStmt statement : statements) {
            try {
                lastResult = statement.accept(this);
            } catch (RuntimeError runtimeError) {
                // throw new RuntimeException(runtimeError);
                System.err.println(runtimeError.getMessage());
            } catch (BreakError e) {
                throw new RuntimeException(e);
            }
        }
        return lastResult;
    }
    @Override
    public Object visitGrouping(Grouping tGrouping) throws RuntimeError {
        return evaluate(tGrouping.expr);
    }

    @Override
    public Object visitUnary(Unary expr) throws RuntimeError {
        Object right = evaluate(expr.right);
        switch (expr.operator.tokenType) {
            case BANG -> {
                return !isTruthy(right);
            }
            case MINUS -> {
                checkNumber(right);
                return -1 * Double.parseDouble(right.toString());
            }
        }
        // This should be unreachable
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    @Override
    public Object visitLiteral(Literal literal) {
        return literal.value;
    }

    @Override
    public Object visitBinary(Binary binary) throws RuntimeError {
        Object lhsResult = evaluate(binary.left);
        Object rhsResult = evaluate(binary.right);
        Token operator = binary.operator;

        switch (operator.tokenType) {
            case PLUS :
                if (isDouble(lhsResult) && isDouble(rhsResult))
                    return Double.parseDouble(lhsResult.toString()) +
                            Double.parseDouble(rhsResult.toString());
                else if (isString(lhsResult) && isString(rhsResult))
                    return (String) lhsResult + (String) rhsResult;
                else return null;
            case MINUS:
                if (isDouble(lhsResult) && isDouble(rhsResult))
                    return Double.parseDouble(lhsResult.toString()) -
                            Double.parseDouble(rhsResult.toString());
            case STAR:
                if (isDouble(lhsResult) && isDouble(rhsResult))
                    return Double.parseDouble(lhsResult.toString()) *
                            Double.parseDouble(rhsResult.toString());
            case PERCENT:
                if (isDouble(lhsResult) && isDouble(rhsResult)) {
                    lhsResult = lhsResult.toString();
                    rhsResult = rhsResult.toString();
                    if (((String) lhsResult).contains(".")) {
                        lhsResult = ((String) lhsResult).split("\\.")[0];
                    }
                    if (((String) rhsResult).contains(".")) {
                        rhsResult = ((String) rhsResult).split("\\.")[0];
                    }
                    return Integer.parseInt(lhsResult.toString()) %
                            Integer.parseInt(rhsResult.toString());
                }
            case SLASH: {
                checkZeroDivision(rhsResult);
                if (isDouble(lhsResult) && isDouble(rhsResult))
                    return Double.parseDouble(lhsResult.toString()) /
                            Double.parseDouble(rhsResult.toString());
                else return null;
            }
            case GREATER:
                checkNumber(lhsResult, rhsResult);
                return Double.parseDouble(lhsResult.toString()) >
                        Double.parseDouble(rhsResult.toString());
            case GREATER_EQUAL:
                checkNumber(lhsResult, rhsResult);
                return Double.parseDouble(lhsResult.toString()) >=
                        Double.parseDouble(rhsResult.toString());
            case LESS:
                checkNumber(lhsResult, rhsResult);
                return Double.parseDouble(lhsResult.toString()) <
                        Double.parseDouble(rhsResult.toString());
            case LESS_EQUAL:
                checkNumber(lhsResult, rhsResult);
                return Double.parseDouble(lhsResult.toString()) <=
                        Double.parseDouble(rhsResult.toString());
            case EQUAL_EQUAL:
                if (isDouble(lhsResult) && isDouble(rhsResult))
                    return isEqual(Double.parseDouble(lhsResult.toString()),
                                   Double.parseDouble(rhsResult.toString()));
                else if (isString(lhsResult) && isString(rhsResult))
                    return isEqual(lhsResult.toString(), rhsResult.toString());
            case BANG_EQUAL:
                return !isEqual(lhsResult, rhsResult);
            default:
                throw new RuntimeError("Unsupported Operation: " + operator.lexeme);
        }
    }

    @Override
    public Object visitVarExpr(VarExpr varExpr) throws RuntimeError {
        return lookupVariable(varExpr.name, varExpr);
    }

    private Object lookupVariable(Token name, VarExpr varExpr) throws RuntimeError {
        return env.getEnvByToken(name);
    }

    @Override
    public Object visitAssignExpr(AssignExpr assignExpr) throws RuntimeError {
        Object value = evaluate(assignExpr.value);
        env.assign(assignExpr.name, value);
        return value;
    }

    @Override
    public Object visitLogicalExpr(LogicalExpr logicalExpr) throws RuntimeError {
        Object left = evaluate(logicalExpr.lhs);
        // Short-circuit evaluations -> we try to be lazy
        // foo and bar
        if (logicalExpr.operator.tokenType == TokenType.OR) {
            // foo or bar. foo is truthy, return foo
            if (isTruthy(left)) return left;
        } else {
            // foo and bar. foo is not truthy. short-circuit and return foo.
            // I don't understand this use-case. shouldn't this always return `False` ?
            // what is Lox trying to do here?
            // actually, if left isn't truthy, left will by definition be false,
            // and we return exactly that. So there is no difference b/w left and false here.
            if (!isTruthy(left)) return left;
        }
        // all short-circuits exhausted. must evaluate RHS and return it.
        // this makes sense.
        // foo or bar, foo is falsy comes here.
            // 1 == 2 or "something" will evaluate and return "something"
        // foo and bar, foo is truthy comes here.
            // 1 == 1 and "something" will evaluate and return "something"
        return evaluate(logicalExpr.rhs);
    }

    @Override
    public Object visitIncrement(Increment increment) throws RuntimeError {
        Object result = evaluate(increment.expr);
        if (isDouble(result)) {
            Double increased = Double.parseDouble(result.toString()) + 1;
            env.assign(increment.operandToken, increased);
            return increased;
        }
        throw new RuntimeError("Invalid data type for Increment operator");
    }

    @Override
    public Object visitDecrement(Decrement decrement) throws RuntimeError {
        Object result = evaluate(decrement.expr);
        if (isDouble(result)) {
            Double decreased = Double.parseDouble(result.toString()) - 1;
            env.assign(decrement.token, decreased);
            return decreased;
        }
        throw new RuntimeError("Invalid data type for Increment operator");
    }

    @Override
    public Object visitCallExpr(FunCall funCall) throws RuntimeError {
        Object callee = evaluate(funCall.callee);
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError("Can only call functions and classes");
        }
        List<Object> evaluatedArgs = new ArrayList<>();
        int i = 0;
        for (Object arg : funCall.args) {
            if (arg instanceof LoxCallable callableArg) {
                LoxFunction func = (LoxFunction) callee;
                Token thisArg = (Token) func.declaration.parameters.get(i);
                env.define(thisArg.lexeme, callableArg);
            } else {
                evaluatedArgs.add(evaluate((BaseExpr) arg));
            }
            i++;
        }
        LoxCallable function = (LoxCallable) callee;
        if (!function.isVariadic() && evaluatedArgs.size() != function.arity()) {
            int expected = function.arity();
            int got = evaluatedArgs.size();
            throw new RuntimeError("Expected " + expected + " arguments but got " + got);
        }
        return function.call(this, evaluatedArgs);
    }

    @Override
    public Object visitLambdaExpr(LambdaExpr lambdaExpr) throws RuntimeError {
        LoxFunction lambdaFn = new LoxFunction(lambdaExpr.loxLambda, env, true);
        // env.define(funStmt.funIdentifier.lexeme, function);
        return lambdaFn;
    }

    private boolean isString(Object object) {
        return object instanceof String;
    }

    private boolean isDouble(Object object) {
        try {
            Double.parseDouble(String.valueOf(object));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean makeTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object lhsResult, Object rhsResult) {
        if (lhsResult == null && rhsResult == null) return true;
        if (lhsResult == null) return false;
        return lhsResult.equals(rhsResult);
    }

    private void checkNumber(Object lhsResult) {
        Double.parseDouble(lhsResult.toString());
    }
    private void checkNumber(Object lhsResult, Object rhsResult) {
        Double.parseDouble(lhsResult.toString());
        Double.parseDouble(rhsResult.toString());
    }

    private void checkZeroDivision(Object rhsResult) throws RuntimeError {
        if (Double.parseDouble(rhsResult.toString()) == 0) {
            throw new RuntimeError("Unsupported Operation: Division by Zero");
        }
    }

    @Override
    public Object visitExpressionStmt(ExprStmt exprStmt) throws RuntimeError {
        return evaluate(exprStmt.expression);
    }

    @Override
    public void visitPrintStmt(PrintStmt printStmt) throws RuntimeError {
        Object evaluate = evaluate(printStmt.expression);
        System.out.println(evaluate);
    }

    @Override
    public Void visitVarStmt(VarStmt varStmt) throws RuntimeError {
        env.define(varStmt.name.lexeme, evaluate(varStmt.initializer));
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt blockStmt) throws RuntimeError, BreakError {
        executeBlock(blockStmt.statements, new Environment(env));
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ifStmt) throws RuntimeError, BreakError {
        Object condResult = evaluate(ifStmt.condition);
        // TODO: Look into the dangling else problem.
        // Our parser conveniently solves it already : if eagerly looks for an else
        // before returning.
        if (isTruthy(condResult)) {
            execute(ifStmt.thenBranch);
        } else if (ifStmt.elseBranch != null) {
            execute(ifStmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt whileStmt) throws RuntimeError {
        insideLoop = true;
        BlockStmt body = (BlockStmt) whileStmt.body;
        while (isTruthy(evaluate(whileStmt.condition))) {
            if (insideLoop) {
                try {
                    execute(body);
                } catch (BreakError e) {
                    break;
                }
            }
        }
        insideLoop = false;
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt forStmt) throws RuntimeError, BreakError {
        while (isTruthy(evaluate(forStmt.condition))) {
            execute(forStmt.body);
        }
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt breakStmt) throws RuntimeError, BreakError {
        insideLoop = false;
        throw new BreakError("Got a break!");
    }

    @Override
    public Object visitFunStmt(FunStmt funStmt) throws RuntimeError {
        // Takes the syntax node and converts that into the function's
        // runtime representation (LoxFunction).
        LoxFunction function = new LoxFunction(funStmt, env, false);
        // Function declarations also bind the resulting object
        // into a new variable.
        env.define(funStmt.funIdentifier.lexeme, function);
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt returnStmt) throws RuntimeError {
        Object value = null;
        if (returnStmt.returnValue != null) {
            value = evaluate(returnStmt.returnValue);
        }
        // We use Exceptions to unwind the call stack and go to the end
        // of the function.
        throw new ReturnObjectException(value);
    }

    public void executeBlock(List<BaseStmt> statements, Environment newEnv) throws RuntimeError, BreakError {
        Environment prev = this.env;
        try {
            this.env = newEnv;
            for (BaseStmt stmt : statements) {
                execute(stmt);
            }
        } finally {
            // Restore the environment
            this.env = prev;
        }
    }
}
