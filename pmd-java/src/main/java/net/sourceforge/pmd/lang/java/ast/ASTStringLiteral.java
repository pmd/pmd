/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Represents a string literal. The image of this node is the literal as it appeared
 * in the source ({@link #getText()}). {@link #getConstValue()} allows to recover
 * the actual runtime value, by processing escapes.
 */
public final class ASTStringLiteral extends AbstractLiteral implements ASTLiteral {

    private static final String TEXTBLOCK_DELIMITER = "\"\"\"";

    private boolean isTextBlock;

    ASTStringLiteral(int id) {
        super(id);
    }


    // todo deprecate this
    // it's ambiguous whether it returns getOriginalText or getTranslatedText
    @Override
    public String getImage() {
        return getText().toString();
    }

    void setTextBlock() {
        this.isTextBlock = true;
    }

    /** Returns true if this is a text block (currently Java 13 preview feature). */
    public boolean isTextBlock() {
        return isTextBlock;
    }

    /** True if the constant value is empty. Does not necessarily compute the constant value. */
    public boolean isEmpty() {
        if (isTextBlock) {
            return getConstValue().isEmpty(); // could be a bunch of ignorable indents?
        } else {
            return getImage().length() == 2; // ""
        }
    }

    /** Length of the constant value in characters. */
    public int length() {
        return getConstValue().length();
    }

    /**
     * Returns a string where non-printable characters have been escaped
     * using Java-like escape codes (eg \n, \t, \u005cu00a0).
     */
    //                                          ^^^^^^
    // this is a backslash, it's printed as \u00a0
    @NoAttribute
    public @NonNull String toPrintableString() {
        return StringUtil.inDoubleQuotes(StringUtil.escapeJava(getConstValue()));
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /** Returns the value without delimiters and unescaped. */
    @Override
    public @NonNull String getConstValue() {
        return (String) super.getConstValue(); // value is cached
    }

    @Override
    protected @NonNull String buildConstValue() {
        if (isTextBlock()) {
            return determineTextBlockContent(getText());
        } else {
            return determineStringContent(getText());
        }
    }

    static @NonNull String determineStringContent(Chars image) {
        Chars woDelims = image.subSequence(1, image.length() - 1);
        StringBuilder sb = new StringBuilder(woDelims.length());
        interpretEscapeSequences(woDelims, sb, false);
        return sb.toString();
    }

    static String determineTextBlockContent(Chars image) {
        List<Chars> lines = getContentLines(image);
        // remove common prefix
        StringUtil.trimIndentInPlace(lines);
        StringBuilder sb = new StringBuilder(image.length());
        for (int i = 0; i < lines.size(); i++) {
            Chars line = lines.get(i);
            boolean isLastLine = i == lines.size() - 1;
            // this might return false if the line ends with a line continuation.
            boolean appendNl = interpretEscapeSequences(line, sb, !isLastLine);
            if (appendNl) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    static String determineTextBlockContent(String image) {
        return determineTextBlockContent(Chars.wrap(image));
    }

    /**
     * Returns the lines of the parameter minus the delimiters.
     */
    private static @NonNull List<Chars> getContentLines(Chars chars) {
        List<Chars> lines = chars.lineStream().collect(Collectors.toList());
        assert lines.size() >= 2 : "invalid text block syntax " + chars;
        // remove first line, it's just """ and some whitespace
        lines = lines.subList(1, lines.size());

        // trim the """ off the last line.
        int lastIndex = lines.size() - 1;
        Chars lastLine = lines.get(lastIndex);
        assert lastLine.endsWith(TEXTBLOCK_DELIMITER);
        lines.set(lastIndex, lastLine.removeSuffix(TEXTBLOCK_DELIMITER));

        return lines;
    }

    /**
     * Interpret escape sequences. This appends the interpreted contents
     * of 'line' into the StringBuilder. The line does not contain any
     * line terminators, instead, an implicit line terminator may be at
     * the end (parameter {@code isEndANewLine}), to interpret line
     * continuations.
     *
     * @param line          Source line
     * @param out           Output
     * @param isEndANewLine Whether the end of the line is a newline,
     *                      as in text blocks
     *
     * @return Whether a newline should be appended at the end. Returns
     *     false if {@code isEndANewLine} and the line ends with a backslash,
     *     as this is a line continuation.
     */
    // See https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-EscapeSequence
    private static boolean interpretEscapeSequences(Chars line, StringBuilder out, boolean isEndANewLine) {
        // we need to interpret everything in one pass, so regex replacement is inappropriate
        int appended = 0;
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c != '\\') {
                i++;
                continue;
            }
            if (i + 1 == line.length()) {
                // the last character of the line is a backslash
                if (isEndANewLine) {
                    // then this is a line continuation
                    line.appendChars(out, appended, i);
                    return false; // shouldn't append newline
                }
                // otherwise we'll append the backslash when exiting the loop
                break;
            }
            char cnext = line.charAt(i + 1);
            switch (cnext) {
            case '\\':
            case 'n':
            case 't':
            case 'b':
            case 'r':
            case 'f':
            case 's':
            case '"':
            case '\'':
                // append up to and not including backslash
                line.appendChars(out, appended, i);
                // append the translation
                out.append(translateBackslashEscape(cnext));
                // next time, start appending after the char
                i += 2;
                appended = i;
                continue;
            // octal digits
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                // append up to and not including backslash
                line.appendChars(out, appended, i);
                i = translateOctalEscape(line, i + 1, out);
                appended = i;
                continue;
            default:
                // unknown escape - do nothing - it stays
                i++;
                break;
            }
        }

        if (appended < line.length()) {
            // append until the end
            line.appendChars(out, appended, line.length());
        }
        return isEndANewLine;
    }

    private static char translateBackslashEscape(char c) {
        switch (c) {
        case '\\': return '\\';
        case 'n': return '\n';
        case 't': return '\t';
        case 'b': return '\b';
        case 'r': return '\r';
        case 'f': return '\f';
        case 's': return ' ';
        case '"': return '"';
        case '\'': return '\'';
        default:
            throw new IllegalArgumentException("Not a valid escape \\" + c);
        }
    }

    private static int translateOctalEscape(Chars src, final int firstDigitIndex, StringBuilder sb) {
        int i = firstDigitIndex;
        int result = src.charAt(i) - '0';
        i++;
        if (src.length() > i && isOctalDigit(src.charAt(i))) {
            result = 8 * result + src.charAt(i) - '0';
            i++;
            if (src.length() > i && isOctalDigit(src.charAt(i))) {
                result = 8 * result + src.charAt(i) - '0';
                i++;
            }
        }
        sb.append((char) result);
        return i;
    }

    private static boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }
}
