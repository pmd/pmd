/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.junit.jupiter.api.Test;

import apex.jorje.data.ast.CompilationUnit;
import apex.jorje.parser.impl.ApexLexer;
import apex.jorje.parser.impl.ApexParser;

class ApexLexerTest {

    private static final String CODE = "public class Foo {\n"
            + "   public List<SObject> test1() {\n"
            + "       return Database.query(\"Select Id from Account LIMIT 100\");\n"
            + "   }\n"
            + "}\n";

    @Test
    void testLexer() throws Exception {
        CharStream in = new ANTLRStringStream(CODE);
        ApexLexer lexer = new ApexLexer(in);

        Token token = lexer.nextToken();
        int tokenCount = 0;
        while (token.getType() != Token.EOF) {
            tokenCount++;
            token = lexer.nextToken();
        }
        assertEquals(43, tokenCount);
    }

    @Test
    void testParser() throws Exception {
        CharStream in = new ANTLRStringStream(CODE);
        ApexLexer lexer = new ApexLexer(in);
        ApexParser parser = new ApexParser(new CommonTokenStream(lexer));
        CompilationUnit compilationUnit = parser.compilationUnit();
        assertNotNull(compilationUnit);
    }
}
