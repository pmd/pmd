/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TextFileContentTest {

    // in real life it's System.lineSeparator()
    // to make the class more testable (and avoid some test failures being hidden depending on the platform),
    // we use this dummy value
    private static final String LINESEP_SENTINEL = ":fallback:";

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testMixedDelimiters(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\r\nb\n\rc");
        Assert.assertEquals(Chars.wrap("a\nb\n\rc"), content.getNormalizedText());
        Assert.assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testFormFeedIsNotNewline(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\f\nb\nc");
        Assert.assertEquals(Chars.wrap("a\f\nb\nc"), content.getNormalizedText());
        Assert.assertEquals("\n", content.getLineTerminator());
    }

    @Test
    public void testNormTextPreservation() {
        Chars input = Chars.wrap("a\nb\nc");
        TextFileContent content = TextFileContent.fromCharSeq(input);
        Assert.assertSame(input, content.getNormalizedText());
        Assert.assertEquals("\n", content.getLineTerminator());
    }

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testBomElimination(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("\ufeffabc");
        Chars normalizedText = content.getNormalizedText();
        Assert.assertEquals(Chars.wrap("abc"), normalizedText);
        // this means the underlying string does not start with the bom marker
        // it's useful for performance to have `textDocument.getText().toString()` be O(1).
        Assert.assertSame(normalizedText.toString(), normalizedText.toString());
    }

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testNoExplicitLineMarkers(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a");
        Assert.assertEquals(Chars.wrap("a"), content.getNormalizedText());
        Assert.assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testEmptyFile(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("");
        Assert.assertEquals(Chars.wrap(""), content.getNormalizedText());
        Assert.assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @Test
    public void testCrlfSplitOnBuffer() throws IOException {
        StringReader reader = new StringReader("a\r\nb");
        // now the buffer is of size 2, so we read first [a\r] then [\nb]
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, System.lineSeparator());
        Assert.assertEquals(Chars.wrap("a\nb"), content.getNormalizedText());
        Assert.assertEquals("\r\n", content.getLineTerminator());
    }

    @Test
    public void testCrSplitOnBufferFp() throws IOException {
        StringReader reader = new StringReader("a\rb\n");
        // the buffer is of size 2, so we read first [a\r] then [b\n]
        // the \r is not a line terminator though
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, LINESEP_SENTINEL);
        Assert.assertEquals(Chars.wrap("a\rb\n"), content.getNormalizedText());
        Assert.assertEquals("\n", content.getLineTerminator());
    }

    @Test
    @Parameters(source = TextContentOrigin.class)
    public void testCrCr(TextContentOrigin origin) throws IOException {
        TextFileContent content = origin.normalize("a\r\rb");
        Assert.assertEquals(Chars.wrap("a\r\rb"), content.getNormalizedText());
        Assert.assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
    }

    @Test
    public void testCrCrSplitBuffer() throws IOException {
        StringReader reader = new StringReader("a\r\r");
        // the buffer is of size 2, so we read first [a\r] then [\ro]
        // the \r is not a line terminator though
        TextFileContent content = TextFileContent.normalizingRead(reader, 2, LINESEP_SENTINEL);
        Assert.assertEquals(Chars.wrap("a\r\r"), content.getNormalizedText());
        Assert.assertEquals(LINESEP_SENTINEL, content.getLineTerminator());
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

        // for junitParams
        public static Object[] provideParameters() {
            return values();
        }
    }
}
