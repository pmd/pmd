/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class CommentUtil {

    private static final Pattern JAVADOC_TAG = Pattern.compile("@[A-Za-z0-9]+");
    // single regex, that captures: the start of a multi-line comment (/**|/*), the start of a single line comment (//)
    // or the start of line within a multine comment (*). It removes the end of the comment (*/) if existing.
    private static final Pattern COMMENT_LINE_COMBINED = Pattern.compile("^(?://|/\\*\\*?|\\*)?(.*?)(?:\\*/|/)?$");

    private CommentUtil() {
    }

    /**
     * Gets the next word (characters until next whitespace, punctuation,
     * or anything that is not a letter or digit) at the given position.
     *
     * @param text the complete text
     * @param position the position, at which the word starts
     * @return the word
     */
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
     */
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
     */
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
     */
    public static List<String> multiLinesIn(String comment) {
        String[] lines = comment.split("\\R");
        List<String> filteredLines = new ArrayList<>(lines.length);

        for (String rawLine : lines) {
            String line = rawLine.trim();

            Matcher allMatcher = COMMENT_LINE_COMBINED.matcher(line);
            if (allMatcher.matches()) {
                filteredLines.add(allMatcher.group(1).trim());
            }
        }

        return filteredLines;
    }

    /**
     * Similar to the String.trim() function, this one removes the leading and
     * trailing empty/blank lines from the line list.
     *
     * @param lines the list of lines, which might contain empty lines
     */
    public static List<String> trim(List<String> lines) {
        if (lines == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(lines.size());
        List<String> tempList = new ArrayList<>();
        boolean foundFirstNonEmptyLine = false;
        for (String line : lines) {
            if (StringUtils.isNoneBlank(line)) {
                // new non-empty line: add all previous empty lines occurred before
                result.addAll(tempList);
                tempList.clear();
                result.add(line);

                foundFirstNonEmptyLine = true;
            } else {
                if (foundFirstNonEmptyLine) {
                    // add the empty line to a temporary list first
                    tempList.add(line);
                }
            }
        }
        return result;
    }
}
