/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.util.CollectionUtil;
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
    protected @NonNull Object buildConstValue() {
        if (isTextBlock()) {
            return determineTextBlockContent(getText());
        } else {
            Chars image = getText();
            Chars woDelims = image.subSequence(1, image.length() - 1);
            return StringEscapeUtils.UNESCAPE_JAVA.translate(woDelims);
        }
    }

    static String determineTextBlockContent(Chars image) {
        List<Chars> lines = getContentLines(image);
        // remove common prefix
        StringUtil.trimIndentInPlace(lines);
        // join with normalized end of line
        StringBuilder sb = CollectionUtil.joinCharsIntoStringBuilder(lines, "\n");
        // interpret escape sequences
        interpretEscapeSequences(sb);
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

    private static void interpretEscapeSequences(StringBuilder sb) {
        // interpret escape sequences "\<LF>" (line continuation), "n","t","b","r","f", "s", "\"", "\'", "\\"
        // we need to interpret everything in one pass, so regex replacement is inappropriate
        // todo octal escapes: https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-EscapeSequence
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c == '\\' && i < sb.length() - 1) {
                char cnext = sb.charAt(i + 1);
                switch (cnext) {
                case '\n':
                    // line continuation
                    sb.delete(i, i + 2);
                    break;
                case '\\':
                    sb.deleteCharAt(i);
                    break;
                case 'n':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\n');
                    break;
                case 't':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\t');
                    break;
                case 'b':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\b');
                    break;
                case 'r':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\r');
                    break;
                case 'f':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\f');
                    break;
                case 's':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, ' ');
                    break;
                case '"':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '"');
                    break;
                case '\'':
                    sb.deleteCharAt(i);
                    sb.setCharAt(i, '\'');
                    break;
                default:
                    // unknown escape - do nothing - it stays
                }
            }
        }
    }
}
