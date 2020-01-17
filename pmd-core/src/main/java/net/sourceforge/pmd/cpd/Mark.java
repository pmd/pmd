/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public class Mark implements Comparable<Mark> {
    private TokenEntry token;
    private TokenEntry endToken;
    private int lineCount;
    private SourceCode code;

    public Mark(TokenEntry token) {
        this.token = token;
    }

    public TokenEntry getToken() {
        return this.token;
    }

    public String getFilename() {
        return this.token.getTokenSrcID();
    }

    public int getBeginLine() {
        return this.token.getBeginLine();
    }

    /**
     * The column number where this duplication begins.
     * returns -1 if not available
     * @return the begin column number
     */
    public int getBeginColumn() {
        return this.token.getBeginColumn(); // TODO Java 1.8 make optional
    }

    public int getEndLine() {
        return getBeginLine() + getLineCount() - 1;
    }

    /**
     * The column number where this duplication ends.
     * returns -1 if not available
     * @return the end column number
     */
    public int getEndColumn() {
        return this.endToken == null ? -1 : this.endToken.getEndColumn(); // TODO Java 1.8 make optional
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public void setEndToken(TokenEntry endToken) {
        this.endToken = endToken;
    }

    public String getSourceCodeSlice() {
        return this.code.getSlice(getBeginLine(), getEndLine());
    }

    public void setSourceCode(SourceCode code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Mark other = (Mark) obj;
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Mark other) {
        return getToken().compareTo(other.getToken());
    }
}
