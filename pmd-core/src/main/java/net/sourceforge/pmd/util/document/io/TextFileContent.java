/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Content of a text file.
 */
public final class TextFileContent {

    /**
     * The normalized line ending used to replace platform-specific
     * line endings in the {@linkplain #getNormalizedText() normalized text}.
     */
    public static final String NORMALIZED_LINE_TERM = "\n";

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\R");

    private final Chars cdata;
    private final String lineTerminator;

    public TextFileContent(Chars normalizedText, String lineTerminator) {
        this.cdata = normalizedText;
        this.lineTerminator = lineTerminator;
    }

    /**
     * The text of the file, with line endings normalized to
     * {@value NORMALIZED_LINE_TERM}.
     */
    public Chars getNormalizedText() {
        return cdata;
    }


    /**
     * Returns the original line terminator found in the file. This is
     * the terminator that should be used to write the file back to disk.
     */
    public String getLineTerminator() {
        return lineTerminator;
    }


    /**
     * Normalize the line endings of the text to {@value NORMALIZED_LINE_TERM},
     * returns a {@link TextFileContent} containing the original line ending.
     * If the text does not contain any line terminators, or if it contains a
     * mix of different terminators, falls back to the platform-specific line
     * separator.
     *
     * @param text Text content of a file
     *
     * @return A text file content
     */
    public static @NonNull TextFileContent fromCharSeq(CharSequence text) {
        return normalizeImpl(text, System.lineSeparator());
    }

    /**
     * Read the reader fully and produce a {@link TextFileContent}. This
     * does not close the reader.
     *
     * @param reader A reader
     *
     * @return A text file content
     *
     * @throws IOException If an IO exception occurs
     */
    public static TextFileContent fromReader(Reader reader) throws IOException {
        // TODO maybe there's a more efficient way to do that.
        String text = IOUtils.toString(reader);
        return fromCharSeq(text);
    }


    /**
     * Reads the contents of the data source to a string. Skips the byte-order
     * mark if present. Parsers expect input without a BOM.
     *
     * @param dataSource     Input stream
     * @param sourceEncoding Encoding to use to read from the data source
     */
    public static TextFileContent fromInputStream(InputStream dataSource, Charset sourceEncoding) throws IOException {
        try (InputStream stream = dataSource;
             // Skips the byte-order mark
             BOMInputStream bomIs = new BOMInputStream(stream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE);
             Reader reader = new InputStreamReader(bomIs, sourceEncoding)) {

            return fromReader(reader);
        }
    }

    // test only
    @NonNull
    static TextFileContent normalizeImpl(CharSequence text, String fallbackLineSep) {
        Matcher matcher = NEWLINE_PATTERN.matcher(text);
        boolean needsNormalization;
        String lineTerminator;
        if (matcher.find()) {
            lineTerminator = matcher.group();
            needsNormalization = !lineTerminator.equals(NORMALIZED_LINE_TERM);

            // check that all line terms are the same
            while (matcher.find()) {
                if (!lineTerminator.equals(matcher.group())) {
                    // mixed line endings, fallback to system default
                    needsNormalization = true;
                    lineTerminator = System.lineSeparator();
                    break;
                }
            }
        } else {
            lineTerminator = fallbackLineSep;
            needsNormalization = false; // no line sep, no need to copy
        }

        if (needsNormalization) {
            text = NEWLINE_PATTERN.matcher(text).replaceAll(NORMALIZED_LINE_TERM);
        }

        return new TextFileContent(Chars.wrap(text), lineTerminator);
    }

}
