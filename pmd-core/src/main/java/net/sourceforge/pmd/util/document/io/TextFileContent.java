/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Contents of a text file.
 */
public final class TextFileContent {

    /**
     * The normalized line ending used to replace platform-specific
     * line endings in the {@linkplain #getNormalizedText() normalized text}.
     */
    public static final String NORMALIZED_LINE_TERM = "\n";
    public static final char NORMALIZED_LINE_TERM_CHAR = '\n';

    private static final int DEFAULT_BUFSIZE = 8192;

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n|\n");
    private static final String FALLBACK_LINESEP = System.lineSeparator();

    private final Chars cdata;
    private final String lineTerminator;

    private final long checkSum;

    private TextFileContent(Chars normalizedText, String lineTerminator, long checkSum) {
        this.cdata = normalizedText;
        this.lineTerminator = lineTerminator;
        this.checkSum = checkSum;
    }

    /**
     * The text of the file, with the following normalizations:
     * <ul>
     * <li>Line endings are normalized to {@value NORMALIZED_LINE_TERM}.
     * For this purpose, a line ending is either {@code \r\n} or {@code \n}
     * (CRLF or LF), not the full range of unicode line endings. This is
     * consistent with {@link BufferedReader#readLine()} for example.
     * <li>An initial byte-order mark is removed, if any.
     * </ul>
     */
    public Chars getNormalizedText() {
        return cdata;
    }


    /**
     * Returns the original line terminator found in the file. This is
     * the terminator that should be used to write the file back to disk.
     * If the original file either has mixed line terminators, or has no
     * line terminator at all, the line terminator defaults to the
     * platform-specific one ({@link System#lineSeparator()}).
     */
    public String getLineTerminator() {
        return lineTerminator;
    }


    /**
     * Returns a checksum for the contents of the file. The checksum is
     * computed on the unnormalized bytes, so may be affected by a change
     * line terminators. This is why two {@link TextFileContent}s with the
     * same normalized content may have different checksums.
     */
    public long getCheckSum() {
        return checkSum;
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
        return normalizeCharSeq(text, FALLBACK_LINESEP);
    }

    /**
     * Read the reader fully and produce a {@link TextFileContent}. This
     * closes the reader.
     *
     * @param reader A reader
     *
     * @return A text file content
     *
     * @throws IOException If an IO exception occurs
     */
    public static TextFileContent fromReader(Reader reader) throws IOException {
        try (Reader r = reader) {
            return normalizingRead(r, DEFAULT_BUFSIZE, FALLBACK_LINESEP, newChecksum(), true);
        }
    }


    /**
     * Reads the contents of the data source to a string. Skips the byte-order
     * mark if present. Parsers expect input without a BOM. This closes the input
     * stream.
     *
     * @param inputStream    Input stream
     * @param sourceEncoding Encoding to use to read from the data source
     */
    public static TextFileContent fromInputStream(InputStream inputStream, Charset sourceEncoding) throws IOException {
        return fromInputStream(inputStream, sourceEncoding, FALLBACK_LINESEP);
    }

    // test only
    static TextFileContent fromInputStream(InputStream inputStream, Charset sourceEncoding, String fallbackLineSep) throws IOException {
        Checksum checksum = newChecksum();
        try (CheckedInputStream checkedIs = new CheckedInputStream(inputStream, checksum);
             Reader reader = new InputStreamReader(checkedIs, sourceEncoding)) {
            return normalizingRead(reader, DEFAULT_BUFSIZE, fallbackLineSep, checksum, false);
        }
    }

    // test only
    static @NonNull TextFileContent normalizeCharSeq(CharSequence text, String fallBackLineSep) {
        long checksum = getChecksum(text); // the checksum is computed on the original file

        if (text.length() > 0 && text.charAt(0) == ByteOrderMark.UTF_BOM) {
            text = text.subSequence(1, text.length()); // skip the BOM
        }
        Matcher matcher = NEWLINE_PATTERN.matcher(text);
        boolean needsNormalization = false;
        String lineTerminator = null;
        while (matcher.find()) {
            lineTerminator = detectLineTerm(lineTerminator, matcher.group(), fallBackLineSep);
            if (!lineTerminator.equals(NORMALIZED_LINE_TERM)) {
                needsNormalization = true;

                if (lineTerminator.equals(fallBackLineSep)) {
                    // otherwise a mixed delimiter may follow, and we must
                    // detect it to fallback on the system separator
                    break;
                }
            }
        }
        if (lineTerminator == null) {
            // no line sep, default to platform sep
            lineTerminator = fallBackLineSep;
            needsNormalization = false;
        }

        if (needsNormalization) {
            text = NEWLINE_PATTERN.matcher(text).replaceAll(NORMALIZED_LINE_TERM);
        }

        return new TextFileContent(Chars.wrap(text), lineTerminator, checksum);
    }

    // test only
    // the bufsize and fallbackLineSep parameters are here just for testability
    static TextFileContent normalizingRead(Reader input, int bufSize, String fallbackLineSep) throws IOException {
        return normalizingRead(input, bufSize, fallbackLineSep, newChecksum(), true);
    }

    static TextFileContent normalizingRead(Reader input, int bufSize, String fallbackLineSep, Checksum checksum, boolean updateChecksum) throws IOException {
        char[] cbuf = new char[bufSize];
        StringBuilder result = new StringBuilder(bufSize);
        String detectedLineTerm = null;
        int n;
        boolean afterCr = false;
        StringBuilder pendingLine = null;
        while (IOUtils.EOF != (n = input.read(cbuf))) {
            int copiedUpTo = 0;

            if (updateChecksum) { // if we use a checked input stream we dont need to update the checksum manually
                updateChecksum(checksum, CharBuffer.wrap(cbuf, 0, n));
            }

            for (int i = 0; i < n; i++) {
                char c = cbuf[i];

                if (i == 0 && result.length() == 0 && c == ByteOrderMark.UTF_BOM) {
                    copiedUpTo = 1;
                    // first char of the entire text: skip bom
                    continue;
                }

                if (afterCr && c != NORMALIZED_LINE_TERM_CHAR && copiedUpTo > 0) {
                    // we saw an \r, but it's not followed by \n
                    // append up to and including the first \r
                    result.append(cbuf, copiedUpTo, i - copiedUpTo);
                    copiedUpTo = i;
                } else if (pendingLine != null) {
                    assert afterCr && i == 0;
                    // we saw a \r at the end of the last buffer
                    result.append(pendingLine);
                    pendingLine = null; // reset it
                    if (c != NORMALIZED_LINE_TERM_CHAR) {
                        result.append('\r'); // because it won't be normalized
                    }
                    // don't reset afterCr, so that the branch below can see it
                }

                if (c == NORMALIZED_LINE_TERM_CHAR) {
                    if (afterCr) {
                        // \r\n
                        if (i > 0) {
                            cbuf[i - 1] = '\n'; // replace the \r with a \n
                            // copy up and including the \r, which was replaced
                            result.append(cbuf, copiedUpTo, i - copiedUpTo);
                        } else {
                            // i == 0
                            // we saw a \r\n split over two buffer iterations
                            // it's been appended previously
                            result.append(NORMALIZED_LINE_TERM_CHAR);
                        }
                        copiedUpTo = i + 1;
                        detectedLineTerm = detectLineTerm(detectedLineTerm, "\r\n", fallbackLineSep);
                    } else {
                        // \n
                        // no need to copy just yet, we can continue
                        detectedLineTerm = detectLineTerm(detectedLineTerm, NORMALIZED_LINE_TERM, fallbackLineSep);
                    }
                } else if (c == '\r' && i == n - 1) {
                    // then, we don't know whether the next char is going to be a \n or not
                    // note the pendingLine does not include the final \r
                    assert pendingLine == null;
                    pendingLine = new StringBuilder(i - copiedUpTo);
                    pendingLine.append(cbuf, copiedUpTo, i - copiedUpTo);
                }
                afterCr = c == '\r';
            }

            if (copiedUpTo != n && !afterCr) {
                result.append(cbuf, copiedUpTo, n - copiedUpTo);
            }
        }

        if (detectedLineTerm == null) {
            // no line terminator in text
            detectedLineTerm = fallbackLineSep;
        }
        if (pendingLine != null) {
            result.append(pendingLine).append('\r');
        }
        return new TextFileContent(Chars.wrap(result), detectedLineTerm, checksum.getValue());
    }

    private static String detectLineTerm(@Nullable String curLineTerm, String newLineTerm, String fallback) {
        if (curLineTerm == null) {
            return newLineTerm;
        }
        if (curLineTerm.equals(newLineTerm)) {
            return curLineTerm;
        } else {
            return fallback; // mixed line terminators, fallback to system default
        }
    }

    private static long getChecksum(CharSequence cs) {
        Checksum checksum = newChecksum();
        updateChecksum(checksum, CharBuffer.wrap(cs));
        return checksum.getValue();
    }

    private static void updateChecksum(Checksum checksum, CharBuffer chars) {
        ByteBuffer bytes = StandardCharsets.UTF_8.encode(chars);
        // note: this is only needed on Java 8. On Java 9, Checksum#update(ByteBuffer) has been added.
        assert bytes.hasArray() : "Encoder should produce a heap buffer";
        checksum.update(bytes.array(), bytes.arrayOffset(), bytes.remaining());
    }


    private static Checksum newChecksum() {
        return new Adler32();
    }
}
