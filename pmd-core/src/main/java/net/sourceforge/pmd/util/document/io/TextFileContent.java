package net.sourceforge.pmd.util.document.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Content of a text file.
 */
public class TextFileContent {

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
    @NonNull
    public static TextFileContent normalizeToFileContent(CharSequence text) {
        return normalizeImpl(text, System.lineSeparator());
    }


    // test only
    @NonNull static TextFileContent normalizeImpl(CharSequence text, String fallbackLineSep) {
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
