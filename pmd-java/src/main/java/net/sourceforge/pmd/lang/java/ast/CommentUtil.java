/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

/**
 *
 * @deprecated This utility class is deprecated and will be removed with PMD 7.0.0.
 *      Its methods have been intended to parse javadoc tags.
 *      A more useful solution will be added around the AST node {@link FormalComment},
 *      which contains as children {@link JavadocElement} nodes, which in
 *      turn provide access to the {@link JavadocTag}.
 */
@Deprecated // will be remove with PMD 7.0.0
public final class CommentUtil {

    private static final Pattern JAVADOC_TAG = Pattern.compile("@[A-Za-z0-9]+");

    private CommentUtil() {
    }

    /**
     * Gets the next word (characters until next whitespace, punctuation,
     * or anything that is not a letter or digit) at the given position.
     *
     * @param text the complete text
     * @param position the position, at which the word starts
     * @return the word
     *
     * @deprecated This method is deprecated and will be removed with PMD 7.0.0.
     *      This method has been intended to parse javadoc tags.
     *      A more useful solution will be added around the AST node {@link FormalComment},
     *      which contains as children {@link JavadocElement} nodes, which in
     *      turn provide access to the {@link JavadocTag}.
     */
    @Deprecated // will be removed with PMD 7.0.0
    public static String wordAfter(String text, int position) {
        if (text == null || position >= text.length()) {
            return null;
        }
        int newposition = position + 1;
        int end = newposition;
        char ch = text.charAt(end);

        while (Character.isLetterOrDigit(ch) && end < text.length()) {
            ch = text.charAt(++end);
        }

        return text.substring(newposition, end);
    }

    /**
     * Gets the remaining line after a specific position.
     *
     * @param text the complete text
     * @param position the position from which the comment should be returned
     * @return the part of the text
     *
     * @deprecated This method is deprecated and will be removed with PMD 7.0.0.
     *      This method has been intended to parse javadoc tags.
     *      A more useful solution will be added around the AST node {@link FormalComment},
     *      which contains as children {@link JavadocElement} nodes, which in
     *      turn provide access to the {@link JavadocTag}.
     */
    @Deprecated // will be removed with PMD 7.0.0
    public static String javadocContentAfter(String text, int position) {
        if (text == null || position > text.length()) {
            return null;
        }

        int endPos = text.indexOf('\n', position);
        if (endPos < 0) {
            endPos = text.length();
        }

        if (StringUtils.isNotBlank(text.substring(position, endPos))) {
            return text.substring(position, endPos).trim();
        }

        if (text.indexOf('@', endPos) >= 0) {
            return null; // nope, this is another entry
        }

        // try next line
        int nextEndPos = text.indexOf('\n', endPos + 1);

        if (StringUtils.isNotBlank(text.substring(endPos, nextEndPos))) {
            return text.substring(endPos, nextEndPos).trim();
        }

        return null;
    }

    /**
     * Finds all the javadoc tags in the (formal) comment.
     * Returns a map from javadoc tag to index position.
     *
     * <p>Note: If a tag is used multiple times, the last occurrence is returned.
     *
     * @param comment the raw comment
     * @return mapping of javadoc tag to index position
     *
     * @deprecated This method is deprecated and will be removed with PMD 7.0.0.
     *      This method has been intended to parse javadoc tags.
     *      A more useful solution will be added around the AST node {@link FormalComment},
     *      which contains as children {@link JavadocElement} nodes, which in
     *      turn provide access to the {@link JavadocTag}.
     */
    @Deprecated // will be removed with PMD 7.0.0
    public static Map<String, Integer> javadocTagsIn(String comment) {
        Map<String, Integer> tags = new HashMap<>();

        if (comment != null) {
            Matcher m = JAVADOC_TAG.matcher(comment);
            while (m.find()) {
                String match = comment.substring(m.start() + 1, m.end());
                tags.put(match, m.start());
            }
        }

        return tags;
    }

    /**
     * Removes the leading comment marker (like {@code *}) of each line
     * of the comment as well as the start marker ({@code //}, {@code /*} or {@code /**}
     * and the end markers (<code>&#x2a;/</code>).
     *
     * @param comment the raw comment
     * @return List of lines of the comments
     *
     * @deprecated This method will be removed with PMD 7.0.0.
     *      It has been replaced by {@link Comment#getFilteredComment()}.
     */
    @Deprecated // will be removed with PMD 7.0.0
    public static List<String> multiLinesIn(String comment) {
        // temporary createa a Multiline Comment Node
        Token t = new Token();
        t.image = comment;
        MultiLineComment node = new MultiLineComment(t);
        return Arrays.asList(Comment.NEWLINES_PATTERN.split(node.getFilteredComment()));
    }

    /**
     * Similar to the String.trim() function, this one removes the leading and
     * trailing empty/blank lines from the line list.
     *
     * @param lines the list of lines, which might contain empty lines
     * @return the lines without leading or trailing blank lines.
     *
     * @deprecated This method will be removed with PMD 7.0.0.
     *      It is not needed anymore, since {@link Comment#getFilteredComment()}
     *      returns already the filtered and trimmed comment text.
     */
    @Deprecated // will be removed with PMD 7.0.0
    public static List<String> trim(List<String> lines) {
        return Comment.trim(lines);
    }
}
