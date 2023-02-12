/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Objects;

public class Mark implements Comparable<Mark> {
    private final TokenEntry token;
    private TokenEntry endToken;

    public Mark(TokenEntry token) {
        this.token = token;
    }

    public TokenEntry getToken() {
        return this.token;
    }

    public String getFilename() {
        return this.token.getFileName();
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

    public int getBeginTokenIndex() {
        return this.token.getIndex();
    }

    public int getEndLine() {
        return endToken == null ? getBeginLine() : endToken.getEndLine();
    }

    /**
     * The column number where this duplication ends.
     * returns -1 if not available
     * @return the end column number
     */
    public int getEndColumn() {
        return this.endToken == null ? -1 : this.endToken.getEndColumn(); // TODO Java 1.8 make optional
    }

    public int getEndTokenIndex() {
        return this.endToken == null ? -1 : this.endToken.getIndex();
    }

    public int getLineCount() {
        return this.getEndLine() - this.getBeginLine() + 1;
    }

    void setEndToken(TokenEntry endToken) {
        this.endToken = endToken;
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
        return Objects.equals(token, other.token);
    }

    @Override
    public int compareTo(Mark other) {
        return getToken().compareTo(other.getToken());
    }
}
