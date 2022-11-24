/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;


class JavaEscapeReaderTest {

    TextDocument readString(String input) {
        TextDocument intext = TextDocument.readOnlyString(Chars.wrap(input), DummyLanguageModule.getInstance().getDefaultVersion());
        return new JavaEscapeTranslator(intext).translateDocument();
    }


    @Test
    void testSimpleRead() throws IOException {

        String input = "abcdede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    void testNotAnEscape1Read() throws IOException {

        String input = "abc\\dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    void testNotAnEscape1Read2() throws IOException {

        String input = "abc\\\\\\dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap(input), r.getText());
        }
    }

    @Test
    void testAnEscapeStopAtEnd() throws IOException {

        String input = "abc\\\\\\u00a0dede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0dede"), r.getText());
        }
    }

    @Test
    void testSeveralEscapes() throws IOException {

        String input = "abc\\\\\\u00a0d\\uu00a0ede";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0d\u00a0ede"), r.getText());
        }
    }

    @Test
    void testAnEscapeInsideBlock() throws IOException {

        String input = "abc\\\\\\u00a0dede\\u00a0";
        try (TextDocument r = readString(input)) {
            assertEquals(Chars.wrap("abc\u00a0dede\u00a0"), r.getText());
        }
    }
}
