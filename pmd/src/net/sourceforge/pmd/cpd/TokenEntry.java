package net.sourceforge.pmd.cpd;

/** tokens are opaque strings of chars. they are immutable, apart from their sort codes*/
public class TokenEntry implements Comparable {

    public static final TokenEntry EOF = new TokenEntry();
    private char[] chars;
    private int hash;
    private String image;
    private int index;
    private String tokenSrcID;
    private int beginLine;

    private int sortCode;

    private TokenEntry() {}

    public TokenEntry(String image, int index, String tokenSrcID, int beginLine) {
        this.image = image;
        this.index = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
        this.chars = image.toCharArray();
    }

    // TE
    public String getImage() {
        return image;
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

    // TE

    public int length() {
        return image.length();
    }

    public void setSortCode(int code) {
        this.sortCode = code;
    }

    public String toString() {
        return new String(chars);
    }

    public int getSortCode(int code) {
        return this.sortCode;
    }

    public boolean equals(Object o) {
        if (o instanceof TokenEntry) {
            TokenEntry token = (TokenEntry)o;
            if (this == EOF) {
                return token == EOF;
            }
            if (token.length() != length()) {
                return false;
            }
            for (int i = 0; i < length(); i++) {
                if (this.chars[i] != token.chars[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    // calculate a hash, as done in Effective Programming in Java.
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            if ( this == EOF ) {
                h = -1;
            } else {
                for (int i = 0 ; i < length(); i++) {
                    h = (37 * h + this.chars[i]);
                }
            }
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
        if (length() == token.length()) {
            for (int i = 0; i < length() && i < token.length(); i++) {
                char c1 = this.chars[i];
                char c2 = token.chars[i];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            return 0;
        }
        for (int i = 0; i < length() && i < token.length(); i++) {
            char c1 = this.chars[i];
            char c2 = token.chars[i];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return length()  - token.length();
    }
}