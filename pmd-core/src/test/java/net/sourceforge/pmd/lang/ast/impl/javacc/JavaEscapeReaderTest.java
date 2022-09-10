/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;


public class JavaEscapeReaderTest {

    public TextDocument readString(String input) {
        TextDocument intext = TextDocument.readOnlyString(Chars.wrap(input), LanguageRegistry.getDefaultLanguage().getDefaultVersion());
        return new JavaEscapeTranslator(intext).translateDocument();
    }


    @Test
    public void testSimpleRead() throws IOException {

        String input = "abcdede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    public void testNotAnEscape1Read() throws IOException {

        String input = "abc\\dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    public void testNotAnEscape1Read2() throws IOException {

        String input = "abc\\\\\\dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    public void testAnEscapeStopAtEnd() throws IOException {

        String input = "abc\\\\\\u00a0dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0dede"), r.getText());
        }
    }

    @Test
    public void testSeveralEscapes() throws IOException {

        String input = "abc\\\\\\u00a0d\\uu00a0ede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0d\u00a0ede"), r.getText());
        }
    }

    @Test
    public void testAnEscapeInsideBlock() throws IOException {

        String input = "abc\\\\\\u00a0dede\\u00a0";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0dede\u00a0"), r.getText());
        }
    }
}
