package com.gill.jlox.scratch;

public class GenericVisitorPatternExample {
    public static void main(String[] args) {
        BinaryAdditionExpression<Double> expr = new BinaryAdditionExpression<>(
                new Number<>(1.0),
                new Number<>(2.0)
        );

        ExpressionEval eval = new ExpressionEval();
        System.out.println(eval.visitBinaryAdditionExpression(expr));

        ExpressionPrinter printer = new ExpressionPrinter();
        System.out.println(
            printer.visitBinaryAdditionExpression(expr)
        );
    }
}

// Generic visitor interface, accepted by various
// data com.gill.jlox.types and flipped-around to call visit()
interface BaseExpressionVisitor<T> {
    T visitNumberExpression(Number expr);
    T visitBinaryAdditionExpression(BinaryAdditionExpression expr);
}

interface MyBaseExpr<T> {
    T accept(BaseExpressionVisitor<T> visitor);
}
class Number<T> implements MyBaseExpr<T> {
    Object value;
    Number(Object value) {
        this.value = value;
    }
    @Override
    public T accept(BaseExpressionVisitor<T> visitor) {
        return visitor.visitNumberExpression(this);
    }
}

class BinaryAdditionExpression<T> implements MyBaseExpr<T> {
    Number<T> lhs;
    Number<T> rhs;

    BinaryAdditionExpression(Number<T> lhs, Number<T> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public T accept(BaseExpressionVisitor<T> visitor) {
        return visitor.visitBinaryAdditionExpression(this);
    }
}

// com.gill.jlox.types.operations
class ExpressionEval implements BaseExpressionVisitor {

    @Override
    public Double visitNumberExpression(Number expr) {
        return (Double) expr.value;
    }

    @Override
    public Double visitBinaryAdditionExpression(BinaryAdditionExpression expr) {
        Double lhsResult = (Double) expr.lhs.accept(this);
        Double rhsResult = (Double) expr.rhs.accept(this);
        return lhsResult + rhsResult;
    }
}

class ExpressionPrinter implements BaseExpressionVisitor {

    @Override
    public String visitNumberExpression(Number expr) {
        return "<com.gill.jlox.scratch.Number: " + expr.value + ">";
    }

    @Override
    public String visitBinaryAdditionExpression(BinaryAdditionExpression expr) {
        return "<BinaryExpr: " +
            expr.lhs.accept(this) +
            " + " +
            expr.rhs.accept(this) +
            ">";
    }
}