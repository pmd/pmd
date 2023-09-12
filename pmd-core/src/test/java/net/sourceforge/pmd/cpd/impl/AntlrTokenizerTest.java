/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AntlrTokenizerTest {

    static class AntlrTokenizerTester extends AntlrTokenizer {
        @Override
        protected Lexer getLexerForSource(CharStream charStream) {
            return null;
        }
    }
    
    @Test
    void testStreamWithBOM() {
        CharStream inputStream = CharStreams.fromString("//Header Comment\n\uFEFFusing System");
        CharStream expectedStream = CharStreams.fromString("//Header Comment\nusing System");
        final AntlrTokenizerTester genericToken = new AntlrTokenizerTester();
        final CharStream outputStream = genericToken.filterBomChar(inputStream);

        assertEquals(outputStream.toString(), expectedStream.toString());
    }

    @Test
    void testStreamWithoutBOM() {
        CharStream inputStream = CharStreams.fromString("//Header Comment\nusing System");
        final AntlrTokenizerTester genericToken = new AntlrTokenizerTester();
        final CharStream outputStream = genericToken.filterBomChar(inputStream);

        assertEquals(outputStream.toString(), inputStream.toString());
    }
}
