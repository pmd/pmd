/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.BufferedInputStream;
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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * Contents of a text file.
 */
public final class TextFileContent {

    // the three line terminators we handle.
    private static final String CRLF = "\r\n";
    private static final String LF = "\n";
    private static final String CR = "\r";

    /**
     * The normalized line ending used to replace platform-specific
     * line endings in the {@linkplain #getNormalizedText() normalized text}.
     */
    public static final String NORMALIZED_LINE_TERM = LF;

    /** The normalized line ending as a char. */
    public static final char NORMALIZED_LINE_TERM_CHAR = '\n';

    private static final int DEFAULT_BUFSIZE = 8192;

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n?|\n");
    private static final String FALLBACK_LINESEP = System.lineSeparator();

    private final Chars cdata;
    private final String lineTerminator;

    private final long checkSum;
    private final SourceCodePositioner positioner;

    private TextFileContent(Chars normalizedText, String lineTerminator, long checkSum, SourceCodePositioner positioner) {
        this.cdata = normalizedText;
        this.lineTerminator = lineTerminator;
        this.checkSum = checkSum;
        this.positioner = positioner;
    }

    /**
     * The text of the file, with the following normalizations:
     * <ul>
     * <li>Line endings are normalized to {@value NORMALIZED_LINE_TERM}.
     * For this purpose, a line ending is either {@code \r}, {@code \r\n}
     * or {@code \n} (CR, CRLF or LF), not the full range of unicode line
     * endings. This is consistent with {@link BufferedReader#readLine()},
     * and the JLS, for example.
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

    SourceCodePositioner getPositioner() {
        return positioner;
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
     * closes the reader. This takes care of buffering.
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
     * Reads the contents of the input stream into a TextFileContent.
     * This closes the input stream. This takes care of buffering.
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
        try (CheckedInputStream checkedIs = new CheckedInputStream(new BufferedInputStream(inputStream), checksum);
             // no need to buffer this reader as we already use our own char buffer
             Reader reader = new InputStreamReader(checkedIs, sourceEncoding)) {
            return normalizingRead(reader, DEFAULT_BUFSIZE, fallbackLineSep, checksum, false);
        }
    }

    // test only
    static @NonNull TextFileContent normalizeCharSeq(CharSequence text, String fallBackLineSep) {
        long checksum = getCheckSum(text); // the checksum is computed on the original file

        if (text.length() > 0 && text.charAt(0) == IOUtil.UTF_BOM) {
            text = text.subSequence(1, text.length()); // skip the BOM
        }
        Matcher matcher = NEWLINE_PATTERN.matcher(text);
        boolean needsNormalization = false;
        String lineTerminator = null;
        while (matcher.find()) {
            lineTerminator = detectLineTerm(lineTerminator, matcher.group(), fallBackLineSep);
            if (!NORMALIZED_LINE_TERM.equals(lineTerminator)) {
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

        return new TextFileContent(Chars.wrap(text), lineTerminator, checksum, SourceCodePositioner.create(text));
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
        boolean afterCr = false;
        SourceCodePositioner.Builder positionerBuilder = new SourceCodePositioner.Builder();

        int bufOffset = 0;
        int nextCharToCopy = 0;
        int n = input.read(cbuf);
        if (n > 0 && cbuf[0] == IOUtil.UTF_BOM) {
            nextCharToCopy = 1;
        }

        while (n != IOUtil.EOF) {
            if (updateChecksum) {
                // if we use a checked input stream we dont need to update the checksum manually
                // note that this checksum operates on non-normalized characters
                updateChecksum(checksum, CharBuffer.wrap(cbuf, nextCharToCopy, n));
            }

            int offsetDiff = 0;

            for (int i = nextCharToCopy; i < n; i++) {
                char c = cbuf[i];

                if (afterCr || c == NORMALIZED_LINE_TERM_CHAR) {
                    final String newLineTerm;
                    final int newLineOffset;
                    if (afterCr && c != NORMALIZED_LINE_TERM_CHAR) {
                        // we saw a \r last iteration, but didn't copy it
                        // it's not followed by an \n
                        newLineTerm = CR;
                        newLineOffset = bufOffset + i + offsetDiff;
                        if (i > 0) {
                            cbuf[i - 1] = NORMALIZED_LINE_TERM_CHAR; // replace the \r with a \n
                        } else {
                            // The CR was trailing a buffer, so it's not in the current buffer and wasn't copied.
                            // Append a newline.
                            result.append(NORMALIZED_LINE_TERM);
                        }
                    } else {
                        if (afterCr) {
                            newLineTerm = CRLF;

                            if (i > 0) {
                                cbuf[i - 1] = NORMALIZED_LINE_TERM_CHAR; // replace the \r with a \n
                                // copy up to and including the \r, which was replaced
                                result.append(cbuf, nextCharToCopy, i - nextCharToCopy);
                                nextCharToCopy = i + 1; // set the next char to copy to after the \n
                            }
                            // Since we're replacing a 2-char delimiter with a single char,
                            // the offset of the line needs to be adjusted.
                            offsetDiff--;
                        } else {
                            // just \n
                            newLineTerm = LF;
                        }
                        newLineOffset = bufOffset + i + offsetDiff + 1;
                    }
                    positionerBuilder.addLineEndAtOffset(newLineOffset);
                    detectedLineTerm = detectLineTerm(detectedLineTerm, newLineTerm, fallbackLineSep);
                }
                afterCr = c == '\r';
            } // end for

            if (nextCharToCopy != n) {
                int numRemaining = n - nextCharToCopy;
                if (afterCr) {
                    numRemaining--; // don't copy the \r, it could still be followed by \n on the next round
                }
                result.append(cbuf, nextCharToCopy, numRemaining);
            }

            nextCharToCopy = 0;
            bufOffset += n + offsetDiff;
            n = input.read(cbuf);
        } // end while

        if (afterCr) { // we're at EOF, so it's not followed by \n
            result.append(NORMALIZED_LINE_TERM);
            positionerBuilder.addLineEndAtOffset(bufOffset);
            detectedLineTerm = detectLineTerm(detectedLineTerm, CR, fallbackLineSep);
        }

        if (detectedLineTerm == null) {
            // no line terminator in text
            detectedLineTerm = fallbackLineSep;
        }

        return new TextFileContent(Chars.wrap(result), detectedLineTerm, checksum.getValue(), positionerBuilder.build(bufOffset));
    }

    private static String detectLineTerm(@Nullable String curLineTerm, String newLineTerm, String fallback) {
        if (curLineTerm == null) {
            return newLineTerm;
        }
        if (curLineTerm.equals(newLineTerm)) {
            return curLineTerm;
        } else {
            // todo maybe we should report a warning
            return fallback; // mixed line terminators, fallback to system default
        }
    }

    private static long getCheckSum(CharSequence cs) {
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
