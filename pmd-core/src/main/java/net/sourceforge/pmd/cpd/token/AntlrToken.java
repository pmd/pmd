/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.GenericToken;

/**
 * Generic Antlr representation of a token.
 */
public class AntlrToken implements GenericToken {

    private static final Pattern NEWLINE_PATTERN =
        // \R on java 8+
        Pattern.compile("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");

    private final Token token;
    private final AntlrToken previousComment;

    private String text;

    private int endline;
    private int endcolumn;

    /**
     * Constructor
     *
     * @param token           The antlr token implementation
     * @param previousComment The previous comment
     */
    public AntlrToken(final Token token, final AntlrToken previousComment) {
        this.token = token;
        this.previousComment = previousComment;
    }

    @Override
    public GenericToken getNext() {
        // Antlr implementation does not require this
        return null;
    }

    @Override
    public GenericToken getPreviousComment() {
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
    public int getBeginLine() {
        return token.getLine();
    }

    @Override
    public int getBeginColumn() {
        int charPos = token.getCharPositionInLine() + 1;
        assert charPos > 0;
        return charPos;
    }


    @Override
    public int getEndLine() {
        if (endline == 0) {
            computeEndCoords();
            assert endline > 0;
        }
        return endline;
    }

    @Override
    public int getEndColumn() {
        if (endcolumn == 0) {
            computeEndCoords();
            assert endcolumn > 0;
        }
        return endcolumn;
    }

    private void computeEndCoords() {
        String image = getImage();
        if (image.length() == 1) {
            // fast path for single char tokens
            if (image.charAt(0) != '\n') {
                this.endline = getBeginLine();
                this.endcolumn = getBeginColumn();
                return;
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

        if (numNls == 0) {
            // single line token
            this.endline = this.getBeginLine();
            int length = 1 + token.getStopIndex() - token.getStartIndex();
            this.endcolumn = token.getCharPositionInLine() + length;
        } else if (lastOffset < image.length()) {
            this.endline = this.getBeginLine() + numNls;
            this.endcolumn = image.length() - lastOffset;
        } else {
            // ends with a newline, the newline is considered part of the previous line
            this.endline = this.getBeginLine() + numNls - 1;
            this.endcolumn = lastLineLen;
        }
    }

    @Override
    @Experimental
    public int getKind() {
        return token.getType();
    }

    /**
     * @deprecated use {@link #getKind()} instead.
     */
    @Deprecated
    public int getType() {
        return getKind();
    }

    public boolean isHidden() {
        return !isDefault();
    }

    public boolean isDefault() {
        return token.getChannel() == Lexer.DEFAULT_TOKEN_CHANNEL;
    }
}
