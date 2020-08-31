/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.util.document.io.TextFileContent;

public class TextFileContentTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testMixedDelimiters() {
        TextFileContent content = TextFileContent.fromCharSeq("a\r\nb\n\rc");
        Assert.assertEquals(Chars.wrap("a\nb\n\nc"), content.getNormalizedText());
        Assert.assertEquals(System.lineSeparator(), content.getLineTerminator());
    }

    @Test
    public void testFormFeedIsNotNewline() {
        TextFileContent content = TextFileContent.fromCharSeq("a\f\nb\nc");
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
    public void testBomElimination() throws IOException {
        byte[] input = "\ufeffabc".getBytes();
        TextFileContent content;
        try (ByteArrayInputStream bar = new ByteArrayInputStream(input)) {
            content = TextFileContent.fromInputStream(bar, StandardCharsets.UTF_8);
        }
        Chars normalizedText = content.getNormalizedText();
        Assert.assertEquals(Chars.wrap("abc"), normalizedText);
        // this means the underlying string does not start with the bom marker
        // it's useful for performance to have `textDocument.getText().toString()` be O(1).
        Assert.assertSame(normalizedText.toString(), normalizedText.toString());
    }

    @Test
    public void testNoExplicitLineMarkers() {
        TextFileContent content = TextFileContent.fromCharSeq("a");
        Assert.assertEquals(Chars.wrap("a"), content.getNormalizedText());
        Assert.assertEquals(System.lineSeparator(), content.getLineTerminator());
    }

    @Test
    public void testEmptyFile() {
        TextFileContent content = TextFileContent.fromCharSeq("");
        Assert.assertEquals(Chars.wrap(""), content.getNormalizedText());
        Assert.assertEquals(System.lineSeparator(), content.getLineTerminator());
    }

}
