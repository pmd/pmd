/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

public class TokenEntry implements Comparable {

    public static final TokenEntry EOF = new TokenEntry();
    private int hash;
    private String image;
    private String tokenSrcID;
    private int beginLine;
    private int index;
    private int identifier;

    private TokenEntry() {
        this.image = "EOF";
        this.tokenSrcID = "EOFMarker";
    }

    public TokenEntry(String image, String tokenSrcID, int index, int beginLine) {
        this.image = image;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
        this.index = index;
    }

    public String getTokenSrcID() {
        return tokenSrcID;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setIdentifier(int code) {
        this.identifier = code;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getIndex() {
        return this.index;
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
        if (this == EOF) {
            if (token == EOF) {
                return 0;
            }
            return -1;
        }
        return image.compareTo(token.image);
    }
}