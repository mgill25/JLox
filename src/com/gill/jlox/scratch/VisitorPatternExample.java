package com.gill.jlox.scratch;

public class VisitorPatternExample {
    public static void main(String[] args) {
        BinaryPlusExpr e = new BinaryPlusExpr(
            new ConstantExpr(1.0),
            new ConstantExpr(2.0)
        );

        ExprPrinter printer = new ExprPrinter();
        printer.visitBinaryPlus(e);

        MyEvaluator evaluator = new MyEvaluator();
        evaluator.visitBinaryPlus(e);
    }
}

// We have a Visitor base class
interface ExprVisitor {
    void visitBinaryPlus(BinaryPlusExpr e);
    void visitConstant(ConstantExpr e);
}

// Our Expr type now just uses an accept() to accept visitors.
interface MyExpr {
    void accept(ExprVisitor visitor);
}

// All of our actual Expr com.gill.jlox.types just accept a visitor
// and the visitor in turn just calls the corresponding visit() method.
// Note: Our Expr classes aren't thinking about com.gill.jlox.types.operations now,
// they just accept visitors and flip around to call visit()
class ConstantExpr implements MyExpr {
    Object value;
    ConstantExpr(Object value) {
        this.value = value;
    }

    @Override
    public void accept(ExprVisitor visitor) {
        visitor.visitConstant(this);
    }
}

class BinaryPlusExpr implements MyExpr {

    ConstantExpr lhs;
    ConstantExpr rhs;
    BinaryPlusExpr(ConstantExpr lhs, ConstantExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(ExprVisitor visitor) {
        visitor.visitBinaryPlus(this);
    }
}

// Now we define our com.gill.jlox.types.operations
class ExprPrinter implements ExprVisitor {
    @Override
    public void visitBinaryPlus(BinaryPlusExpr e) {
        ConstantExpr lhs = e.lhs;
        ConstantExpr rhs = e.rhs;
        System.out.print("<BinaryPlus: ");
        lhs.accept(this);
        System.out.print(" + ");
        rhs.accept(this);
        System.out.println(">");
    }

    @Override
    public void visitConstant(ConstantExpr e) {
        System.out.print("<com.gill.jlox.scratch.ConstantExpr: " + e.value + ">");
    }
}

class MyEvaluator implements ExprVisitor {

    @Override
    public void visitBinaryPlus(BinaryPlusExpr e) {
        double result = (Double) e.lhs.value + (Double) e.rhs.value;
        System.out.println(result);
    }

    @Override
    public void visitConstant(ConstantExpr e) {
        double result = (Double) e.value;
        System.out.println(result);
    }
}