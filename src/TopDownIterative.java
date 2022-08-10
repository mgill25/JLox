import com.gill.jlox.tokens.Token;

import java.util.List;

/**
 * Based on a Hacker News Comment
 * https://news.ycombinator.com/item?id=13915458
 *
 * Technique: Precedence Climbing
 * Read: https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm#climbing
 *
 */
public class TopDownIterative {
    int currentPrecedence = 0;
    public Expr parse(List<Token> tokenStream) {
        try {
            return expression(currentPrecedence);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Expr expression(int currentPrecedence) {
        return null;
    }
}
