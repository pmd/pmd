/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import io.github.apexdevtools.apexparser.ApexLexer;
import io.github.apexdevtools.apexparser.ApexParser;
import io.github.apexdevtools.apexparser.CaseInsensitiveInputStream;

/**
 * This is an exploration test for {@link ApexLexer}.
 */
class ApexLexerTest {
    private static final String CODE = "public class Foo {\n"
            + "   public List<SObject> test1() {\n"
            + "       return Database.query('Select Id from Account LIMIT 100');\n"
            + "   }\n"
            + "}\n";

    @Test
    void testLexer() {
        CharStream in = CharStreams.fromString(CODE);
        ApexLexer lexer = new ApexLexer(in);

        Token token = lexer.nextToken();
        int tokenCount = 0;
        while (token.getType() != Token.EOF) {
            tokenCount++;
            token = lexer.nextToken();
        }
        assertEquals(35, tokenCount);
    }

    @Test
    void testParser() {
        CharStream in = CharStreams.fromString(CODE);
        ApexLexer lexer = new ApexLexer(in);
        ApexParser parser = new ApexParser(new CommonTokenStream(lexer));
        ApexParser.CompilationUnitContext compilationUnit = parser.compilationUnit();
        assertNotNull(compilationUnit);
    }

    @Test
    void testLexerUnicodeEscapes() {
        String s = "'Fran\\u00E7ois'";
        // note: with apex-parser 4.3.1, no errors are reported anymore
        assertEquals(0, getLexingErrors(CharStreams.fromString(s)));
        assertEquals(0, getLexingErrors(new CaseInsensitiveInputStream(CharStreams.fromString(s))));
    }

    private int getLexingErrors(CharStream stream) {
        ApexLexer lexer = new ApexLexer(stream);
        ErrorListener errorListener = new ErrorListener();
        lexer.removeErrorListeners(); // Avoid distracting "token recognition error" stderr output
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        return errorListener.getErrorCount();
    }

    private static class ErrorListener extends BaseErrorListener {
        private int errorCount = 0;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
            int charPositionInLine, String msg, RecognitionException e) {
            ++errorCount;
        }

        public int getErrorCount() {
            return errorCount;
        }
    }
}
