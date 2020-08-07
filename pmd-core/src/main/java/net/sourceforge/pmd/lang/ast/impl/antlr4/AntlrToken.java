/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.util.document.FileLocation;

/**
 * Generic Antlr representation of a token.
 */
public class AntlrToken implements GenericToken<AntlrToken> {

    private static final Pattern NEWLINE_PATTERN =
        // \R on java 8+
        Pattern.compile("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");

    private final Token token;
    private final AntlrToken previousComment;
    private final String fileName;
    AntlrToken next;

    private String text;

    private int endline;
    private int endcolumn;

    /**
     * Constructor
     *
     * @param token           The antlr token implementation
     * @param previousComment The previous comment
     * @param fileName        The filename
     */
    public AntlrToken(final Token token, final AntlrToken previousComment, String fileName) {
        this.token = token;
        this.previousComment = previousComment;
        this.fileName = fileName;
    }

    @Override
    public AntlrToken getNext() {
        return next;
    }

    @Override
    public AntlrToken getPreviousComment() {
        return previousComment;
    }

    @Override
    public String getImage() {
        if (text == null) {
            text = token.getText();
        }
        return text;
    }

    @Override
    public boolean isEof() {
        return getKind() == Token.EOF;
    }

    private int getLength() {
        return token.getStopIndex() - token.getStartIndex();
    }

    @Override
    public int getBeginColumn() {
        int charPos = token.getCharPositionInLine() + 1;
        assert charPos > 0;
        return charPos;
    }


    @Override
    public int compareTo(AntlrToken o) {
        int start = Integer.compare(token.getStartIndex(), o.token.getStartIndex());
        return start == 0 ? Integer.compare(getLength(), o.getLength())
                          : start;
    }

    @Override
    public FileLocation getReportLocation() {
        final int bline = token.getLine();
        final int bcol = token.getCharPositionInLine() + 1;

        String image = getImage();
        if (image.length() == 1) {
            // fast path for single char tokens
            if (image.charAt(0) != '\n') {
                return FileLocation.location(fileName, bline, bcol, bline, bcol + 1);
            }
        }

        Matcher matcher = NEWLINE_PATTERN.matcher(image);
        int numNls = 0;
        int lastOffset = 0;
        int lastLineLen = -1;
        while (matcher.find()) {
            // continue
            numNls++;
            if (lastLineLen < 0) {
                // first iteration, line may not be completely in the image
                lastLineLen = token.getCharPositionInLine() + matcher.end();
            } else {
                lastLineLen = matcher.end() - lastOffset;
            }
            lastOffset = matcher.end();
        }

        int endline;
        int endcolumn;
        if (numNls == 0) {
            // single line token
            endline = bline;
            int length = 1 + token.getStopIndex() - token.getStartIndex();
            endcolumn = token.getCharPositionInLine() + length + 1;
        } else if (lastOffset < image.length()) {
            endline = bline + numNls;
            endcolumn = image.length() - lastOffset + 1;
        } else {
            // ends with a newline, the newline is considered part of the previous line
            endline = bline + numNls - 1;
            endcolumn = lastLineLen + 1;
        }

        return FileLocation.location(fileName, bline, bcol, endline, endcolumn);
    }

    public int getKind() {
        return token.getType();
    }

    public boolean isHidden() {
        return !isDefault();
    }

    public boolean isDefault() {
        return token.getChannel() == Lexer.DEFAULT_TOKEN_CHANNEL;
    }
}
