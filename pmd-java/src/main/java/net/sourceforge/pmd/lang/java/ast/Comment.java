/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.AbstractNode;

public abstract class Comment extends AbstractNode {
    // single regex, that captures: the start of a multi-line comment (/**|/*), the start of a single line comment (//)
    // or the start of line within a multine comment (*). It removes the end of the comment (*/) if existing.
    private static final Pattern COMMENT_LINE_COMBINED = Pattern.compile("^(?://|/\\*\\*?|\\*)?(.*?)(?:\\*/|/)?$");

    // Same as "\\R" - but \\R is only available with java8+
    static final Pattern NEWLINES_PATTERN = Pattern.compile("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");

    protected Comment(Token t) {
        super(-1, t.beginLine, t.endLine, t.beginColumn, t.endColumn);

        setImage(t.image);
    }

    @Override
    public String toString() {
        return getImage();
    }

    /**
     * Filters the comment by removing the leading comment marker (like {@code *}) of each line
     * as well as the start markers ({@code //}, {@code /*} or {@code /**}
     * and the end markers (<code>&#x2a;/</code>).
     * Also leading and trailing empty lines are removed.
     *
     * @return the filtered comment
     */
    public String getFilteredComment() {
        List<String> lines = multiLinesIn();
        lines = trim(lines);
        return StringUtils.join(lines, PMD.EOL);
    }

    /**
     * Removes the leading comment marker (like {@code *}) of each line
     * of the comment as well as the start marker ({@code //}, {@code /*} or {@code /**}
     * and the end markers (<code>&#x2a;/</code>).
     *
     * @param comment the raw comment
     * @return List of lines of the comments
     */
    private List<String> multiLinesIn() {
        String[] lines = NEWLINES_PATTERN.split(getImage());
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
     * @return the lines without leading or trailing blank lines.
     */
    // note: this is only package private, since it is used by CommentUtil. Once CommentUtil is gone, this
    // can be private
    static List<String> trim(List<String> lines) {
        if (lines == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(lines.size());
        List<String> tempList = new ArrayList<>();
        boolean foundFirstNonEmptyLine = false;
        for (String line : lines) {
            if (StringUtils.isNotBlank(line)) {
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
