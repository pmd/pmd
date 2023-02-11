/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public class TokenEntry implements Comparable<TokenEntry> {

    public static final TokenEntry EOF = new TokenEntry();

    private final String tokenSrcID;
    private final int beginLine;
    private final int beginColumn;
    private final int endColumn;
    private int index;
    private int identifier;
    private int hashCode;

    private TokenEntry() {
        this.identifier = 0;
        this.tokenSrcID = "EOFMarker";
        this.beginLine = -1;
        this.beginColumn = -1;
        this.endColumn = -1;
    }

    TokenEntry(int imageId, String tokenSrcID, int beginLine, int beginColumn, int endLine, int endColumn, int index) {
        assert isOk(beginLine) && isOk(beginColumn) && isOk(endLine) && isOk(endColumn) : "Coordinates are 1-based";
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.identifier = imageId;
        this.index = index;
    }


    private boolean isOk(int coord) {
        return coord >= 1 || coord == -1;
    }


    String getTokenSrcID() {
        return tokenSrcID;
    }

    public int getBeginLine() {
        return beginLine;
    }

    /**
     * The column number where this token begins.
     * returns -1 if not available
     * @return the begin column number
     */
    public int getBeginColumn() {
        return beginColumn; // TODO Java 1.8 make optional
    }

    /**
     * The column number where this token ends.
     * returns -1 if not available
     * @return the end column number
     */
    public int getEndColumn() {
        return endColumn; // TODO Java 1.8 make optional
    }

     int getIdentifier() {
        return this.identifier;
    }

     int getIndex() {
        return this.index;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

     void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public boolean equals(Object o) {
        // make sure to recognize EOF regardless of hashCode (hashCode is irrelevant for EOF)
        if (this == o) {
            return true;
        } else if (o == EOF || this == EOF) {
            return false;
        }
        // any token except EOF
        if (!(o instanceof TokenEntry)) {
            return false;
        }
        TokenEntry other = (TokenEntry) o;
        return other.hashCode == hashCode;
    }

    @Override
    public int compareTo(TokenEntry other) {
        return getIndex() - other.getIndex();
    }

    @Override
    public String toString() {
        if (EOF.equals(this)) {
            return "EOF";
        }
        return Integer.toString(identifier);
    }

    final void setImageIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
