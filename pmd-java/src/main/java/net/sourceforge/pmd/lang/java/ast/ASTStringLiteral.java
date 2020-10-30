/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    protected @Nullable Object buildConstValue() {
        if (isTextBlock()) {
            return determineTextBlockContent(getImage());
        } else {
            CharSequence image = getText();
            CharSequence woDelims = image.subSequence(1, image.length() - 1);
            return StringEscapeUtils.UNESCAPE_JAVA.translate(woDelims);
        }
    }

    static String determineTextBlockContent(String image) {
        // normalize line endings to LF
        String content = image.replaceAll("\r\n|\r", "\n");
        int start = determineContentStart(content);
        content = content.substring(start, content.length() - TEXTBLOCK_DELIMITER.length());

        int prefixLength = Integer.MAX_VALUE;
        List<String> lines = Arrays.asList(content.split("\\n"));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // compute common prefix
            if (!StringUtils.isAllBlank(line) || i == lines.size() - 1) {
                prefixLength = Math.min(prefixLength, countLeadingWhitespace(line));
            }
        }
        if (prefixLength == Integer.MAX_VALUE) {
            // common prefix not found
            prefixLength = 0;
        }
        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // remove common whitespace prefix
            if (!StringUtils.isAllBlank(line) && line.length() >= prefixLength) {
                line = line.substring(prefixLength);
            }
            line = removeTrailingWhitespace(line);
            sb.append(line);

            boolean isLastLine = i == lines.size() - 1;
            boolean isFirstLine = i == 0;
            if (!isLastLine || !isFirstLine && !StringUtils.isAllBlank(line)) {
                sb.append('\n');
            }
        }

        interpretEscapeSequences(sb);
        return sb.toString();
    }

    private static void interpretEscapeSequences(StringBuilder sb) {
        // interpret escape sequences "\<LF>" (line continuation), "n","t","b","r","f", "s", "\"", "\'", "\\"
        // we need to interpret everything in one pass, so regex replacement is inappropriate
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

    private static int determineContentStart(String s) {
        int start = TEXTBLOCK_DELIMITER.length(); // this is the opening delimiter
        // the content begins after at the first character after the line terminator
        // of the opening delimiter
        while (start < s.length() && Character.isWhitespace(s.charAt(start))) {
            if (s.charAt(start) == '\n') {
                return start + 1;
            }
            start++;
        }
        return start;
    }

    private static int countLeadingWhitespace(String s) {
        int count = 0;
        while (count < s.length() && Character.isWhitespace(s.charAt(count))) {
            count++;
        }
        return count;
    }

    private static String removeTrailingWhitespace(String s) {
        int endIndexIncluding = s.length() - 1;
        while (endIndexIncluding >= 0 && Character.isWhitespace(s.charAt(endIndexIncluding))) {
            endIndexIncluding--;
        }
        return s.substring(0, endIndexIncluding + 1);
    }
}
