/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 - 2014 All contributors
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.scala.language;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sonar.plugins.scala.util.StringUtils;

/**
 * This class implements a Scala comment and the computation of several base
 * metrics for a comment.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class Comment {

    private final CommentType type;
    private final List<String> lines;

    public Comment(String content, CommentType type) throws IOException {
        lines = StringUtils.convertStringToListOfLines(content);
        this.type = type;
    }

    public int getNumberOfLines() {
        return lines.size() - getNumberOfBlankLines() - getNumberOfCommentedOutLinesOfCode();
    }

    public int getNumberOfBlankLines() {
        int numberOfBlankLines = 0;
        for (String comment : lines) {
            boolean isBlank = true;

            for (int i = 0; isBlank && i < comment.length(); i++) {
                char character = comment.charAt(i);
                if (!Character.isWhitespace(character) && character != '*' && character != '/') {
                    isBlank = false;
                }
            }

            if (isBlank) {
                numberOfBlankLines++;
            }
        }
        return numberOfBlankLines;
    }

    public int getNumberOfCommentedOutLinesOfCode() {
        if (isDocComment()) {
            return 0;
        }

        int numberOfCommentedOutLinesOfCode = 0;
        for (String line : lines) {
            String strippedLine = org.apache.commons.lang3.StringUtils.strip(line, " /*");
            if (CodeDetector.hasDetectedCode(strippedLine)) {
                numberOfCommentedOutLinesOfCode++;
            }
        }
        return numberOfCommentedOutLinesOfCode;
    }

    public boolean isDocComment() {
        return type == CommentType.DOC;
    }

    public boolean isHeaderComment() {
        return type == CommentType.HEADER;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(lines).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Comment)) {
            return false;
        }

        Comment other = (Comment) obj;
        return new EqualsBuilder().append(type, other.type).append(lines, other.lines).isEquals();
    }

    @Override
    public String toString() {
        final String firstLine = lines.isEmpty() ? "" : lines.get(0);
        final String lastLine = lines.isEmpty() ? "" : lines.get(lines.size() - 1);
        return new ToStringBuilder(this).append("type", type).append("firstLine", firstLine)
                .append("lastLine", lastLine).append("numberOfLines", getNumberOfLines())
                .append("numberOfCommentedOutLinesOfCode", getNumberOfCommentedOutLinesOfCode()).toString();
    }
}
