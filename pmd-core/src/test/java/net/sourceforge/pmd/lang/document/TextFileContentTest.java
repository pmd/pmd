/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TextFileContentTest {

    // in real life it's System.lineSeparator()
    // to make the class more testable (and avoid some test failures being hidden depending on the platform),
    // we use this dummy value
    private static final String LINESEP_SENTINEL = ":fallback:";

    @ParameterizedTest
    @EnumSource
    void testMixedDelimiters(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\r\nb\n\rc");
        assertEquals(Chars.wrap("a\nb\n\nc"), content.getNormalizedText());
        assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testFormFeedIsNotNewline(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\f\nb\nc");
        assertEquals(Chars.wrap("a\f\nb\nc"), content.getNormalizedText());
        assertEquals("\n", content.getLineTerminator());
    }

    @Test
    void testNormTextPreservation() {
        Chars input = Chars.wrap("a\nb\nc");
        TextFileContent content = TextFileContent.fromCharSeq(input);
        assertSame(input, content.getNormalizedText());
        assertEquals("\n", content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testBomElimination(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("\ufeffabc");
        Chars normalizedText = content.getNormalizedText();
        assertEquals(Chars.wrap("abc"), normalizedText);
        // This means the string underlying the Chars does not start with the bom marker.
        // It's useful for performance to have `textDocument.getText().toString()` be O(1),
        // and not create a new string.
        assertTrue(normalizedText.isFullString(), "should be full string");
        assertSame(normalizedText.toString(), normalizedText.toString());
    }

    @ParameterizedTest
    @EnumSource
    void testNoExplicitLineMarkers(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a");
        assertEquals(Chars.wrap("a"), content.getNormalizedText());
        assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testEmptyFile(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("");
        assertEquals(Chars.wrap(""), content.getNormalizedText());
        assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @Test
    void testCrlfSplitOnBuffer() throws IOException {
        StringReader reader = new StringReader("a\r\nb");
        // now the buffer is of size 2, so we read first [a\r] then [\nb]
        // but there is a single line
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, System.lineSeparator());
        assertEquals(Chars.wrap("a\nb"), content.getNormalizedText());
        assertEquals("\r\n", content.getLineTerminator());
    }

    @Test
    void testCrSplitOnBufferFp() throws IOException {
        StringReader reader = new StringReader("a\rb\n");
        // the buffer is of size 2, so we read first [a\r] then [b\n]
        // the \r is a line terminator on its own
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, LINESEP_SENTINEL);
        assertEquals(Chars.wrap("a\nb\n"), content.getNormalizedText());
        assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testCrCr(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\r\rb");
        assertEquals(Chars.wrap("a\n\nb"), content.getNormalizedText());
        assertEquals("\r", content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testCrIsEol(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\rb\rdede");
        assertEquals(Chars.wrap("a\nb\ndede"), content.getNormalizedText());
        assertEquals("\r", content.getLineTerminator());
    }

    @ParameterizedTest
    @EnumSource
    void testLfAtStartOfFile(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("\nohio");
        assertEquals(Chars.wrap("\nohio"), content.getNormalizedText());
        assertEquals("\n", content.getLineTerminator());
    }

    @Test
    void testCrCrSplitBuffer() throws IOException {
        StringReader reader = new StringReader("a\r\r");
        // the buffer is of size 2, so we read first [a\r] then [\ro]
        // the \r is not a line terminator though
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, LINESEP_SENTINEL);
        assertEquals(Chars.wrap("a\n\n"), content.getNormalizedText());
        assertEquals("\r", content.getLineTerminator());
    }

    enum TextContentOrigin {
        INPUT_STREAM {
            @Override
            TextFileContent normalize(String text) throws IOException {
                Charset charset = StandardCharsets.UTF_8;
                byte[] input = text.getBytes(charset);
                TextFileContent content;
                try (ByteArrayInputStream bar = new ByteArrayInputStream(input)) {
                    content = TextFileContent.fromInputStream(bar, charset, LINESEP_SENTINEL);
                }
                return content;
            }
        },
        READER {
            @Override
            TextFileContent normalize(String input) throws IOException {
                return TextFileContent.normalizingRead(new StringReader(input), 4096, LINESEP_SENTINEL);
            }
        },
        STRING {
            @Override
            TextFileContent normalize(String input) throws IOException {
                return TextFileContent.normalizeCharSeq(input, LINESEP_SENTINEL);
            }
        };

        abstract TextFileContent normalize(String input) throws IOException;
    }
}
