/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

public class TokenEntry implements Comparable {

    public static final TokenEntry EOF = new TokenEntry();
    private int hash;
    private String image;
    private int index;
    private String tokenSrcID;
    private int beginLine;

    private int sortCode;

    private TokenEntry() {
        this.image = "EOF";
        this.tokenSrcID = "EOFMarker";
    }

    public TokenEntry(String image, int index, String tokenSrcID, int beginLine) {
        this.image = image;
        this.index = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
    }

    public int getIndex() {
        return index;
    }

    public String getTokenSrcID() {
        return tokenSrcID;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setSortCode(int code) {
        this.sortCode = code;
    }

    public boolean equals(Object o) {
        if (o instanceof TokenEntry) {
            TokenEntry token = (TokenEntry)o;
            if (this == EOF) {
                return token == EOF;
            }
            return image.equals(token.image);
        }
        return false;
    }
    // calculate a hash, as done in Effective Programming in Java.
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = image.hashCode();
            hash = h; // single assignment = thread safe hashcode.
        }
        return h;
    }
    
    public int compareTo(Object o) {
        TokenEntry token = (TokenEntry)o;
        // try to use sort codes if available.
        if (this == EOF) {
            if (token == EOF) {
                return 0;
            }
            return -1;
        }
        if (this.sortCode > 0 && token.sortCode > 0) {
            return this.sortCode - token.sortCode;
        }
        // otherwise sort lexicographically
        return image.compareTo(token.image);
    }
}