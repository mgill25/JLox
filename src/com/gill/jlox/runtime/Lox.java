package com.gill.jlox.runtime;

import com.gill.jlox.operations.Evaluator;
import com.gill.jlox.operations.Lexer;
import com.gill.jlox.operations.Parser;
import com.gill.jlox.operations.SyntaxTree;
import com.gill.jlox.tokens.Token;
import com.gill.jlox.tokens.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static boolean hadOutput = false;
    static String runtimeErrorMsg;
    static Object lastOutput;
    static Evaluator evaluator = new Evaluator();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // launch the repl loop
        for (;;) {
            hadError = false;
            hadRuntimeError = false;
            hadOutput = false;
            runtimeErrorMsg = "";
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                continue;
            }
            run(line);
            if (hadRuntimeError || hadError) {
                System.out.println("[ERR]: " + runtimeErrorMsg);
            } else if (hadOutput) {
                System.out.println("[OUT]: " + lastOutput);
            }
        }
    }

    // Execute from a file.
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        } else if (hadRuntimeError) {
            System.exit(70);
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        List<Token> tokenStream = new ArrayList<>();
        try {
            tokenStream = lexer.scan(source);
            tokenStream = tokenStream.subList(0, tokenStream.size());
            if (tokenStream.size() == 1 && tokenStream.get(0).tokenType == TokenType.EOF) {
                return;
            }
            // System.out.println(tokenStream);
        } catch (Lexer.LexerError e) {
            hadError = true;
            runtimeErrorMsg = e.getMessage();
        }
        if (hadError) {
            return;
        }
        SyntaxTree ast = null;
        try {
            ast = parser.parse(tokenStream);
        } catch (Parser.ParseError e) {
            hadError = true;
            runtimeErrorMsg = e.getMessage();
        }
        if (hadError) {
            System.err.println(runtimeErrorMsg);
            return;
        }
        Object evalResult = null;
        try {
            evalResult = evaluator.evaluate(ast.statements);
            lastOutput = evalResult;
            hadOutput = true;
        } catch (RuntimeError e) {
            hadRuntimeError = true;
            runtimeErrorMsg = e.getMessage();
        }
    }

    // Error
    static void error(int line, String msg) {
        report(line, "", msg);
    }

    private static void report(int line, String loc, String msg) {
        System.err.println(
                "[line " + line + "] Error " + loc + ": " + msg
        );
        hadError = true;
    }

    public static void error(Token token, String msg) {
        if (token.tokenType == TokenType.EOF) {
            report(token.line, " at end", msg);
        } else {
            report(token.line, " at '" + token.lexeme + "'", msg);
        }
    }
}
