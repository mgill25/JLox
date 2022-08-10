import com.gill.jlox.tokens.Token;
import com.gill.jlox.tokens.TokenType;

// Nodes that are part of the AST
public abstract class Expr {
    public abstract Object eval();
    public static class Literal extends Expr {
        Object value;
        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value.toString();
        }

        public Object eval() {
            return this.value;
        }
    }

    public static class Unary extends Expr {
        Token operator;
        Expr right;
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + this.operator + " " + this.right + ")";
        }

        public Object eval() {
            TokenType tt = this.operator.tokenType;
            if (tt == TokenType.MINUS || tt == TokenType.BANG) {
                Object right = this.right.eval();
                switch (this.operator.tokenType) {
                    case BANG:
                        return !isTruthy(right);
                    case MINUS:
                        return negateNumber(right);
                }
            }
            throw new RuntimeException("Cannot evaluate Unary of com.gill.jlox.tokens.TokenType: " + this.operator.tokenType);
        }

        private Object negateNumber(Object obj) {
            try {
                return -1 * Integer.parseInt(String.valueOf(obj));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Trying to negate a non-numeric value");
            }
        }

        private boolean isTruthy(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Boolean) {
                return (boolean) obj;
            }
            return true;
        }
    }

    public static class Binary extends Expr {
        Expr left, right;
        Token operator;
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        private void checkNumberOperands(TokenType operator, Object left, Object right) {
            try {
                Double.parseDouble(String.valueOf(left));
                Double.parseDouble(String.valueOf(right));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Bad operand: " + left.toString());
            }
        }

        private Double parseDouble(Object obj) {
            return Double.parseDouble(String.valueOf(obj));
        }

        public Object eval() {
            TokenType tt = this.operator.tokenType;
            Object left = this.left.eval();
            Object right = this.right.eval();
            switch (tt) {
                case PLUS -> {
                    checkNumberOperands(tt, left, right);
                    return parseDouble(left) + parseDouble(right);
                }
                case MINUS -> {
                    checkNumberOperands(tt, left, right);
                    return parseDouble(left) - parseDouble(right);
                }
                case STAR -> {
                    checkNumberOperands(tt, left, right);
                    return parseDouble(left) * parseDouble(right);
                }
                case SLASH -> {
                    checkNumberOperands(tt, left, right);
                    checkNonZero(right);
                    return parseDouble(left) / parseDouble(right);
                }
            }
            throw new RuntimeException("Failed to evaluate operator of com.gill.jlox.tokens.TokenType = " + tt);
        }

        private void checkNonZero(Object right) {
            if (parseDouble(right) == 0) {
                throw new RuntimeException("Cannot divide by Zero!");
            }
        }

        @Override
        public String toString() {
            return "(" +
                    this.operator +
                    " " +
                    this.left +
                    " " +
                    this.right +
                    ")";
        }
    }

    public static class Grouping extends Expr {

        final Expr expr;

        public Grouping(Expr expr) {
            this.expr = expr;
        }

        @Override
        public Object eval() {
            return null;
        }
    }
}
