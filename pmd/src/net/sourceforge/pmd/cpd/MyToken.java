package net.sourceforge.pmd.cpd;

/** tokens are opaque strings of chars. they are immutable, apart from their sort codes*/
public class MyToken implements Comparable {

    public static final MyToken EOF = new MyToken();
    private char[] chars;
    private int start;
    private int length;
    private boolean markToken;
    private int hash;

    private MyToken() {}

    public MyToken(char[] chars, int start, int end, boolean markToken) {
        this.chars = chars;
        this.start = start;
        this.length = end - start;
        this.markToken = markToken;
    }

    public boolean isMarkToken() {
        return markToken;
    }

    private int sortCode;

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
        if (o instanceof MyToken) {
            MyToken token = (MyToken)o;
            if (this == EOF) {
                return token == EOF;
            }
            if (token.length != this.length) {
                return false;
            }
            for (int i = 0; i < this.length; i++) {
                if (this.chars[i + this.start] != token.chars[i + token.start]) {
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
                for (int i = 0 ; i < this.length; i++) {
                    h = (37 * h + this.chars[i + this.start]);
                }
            }
            hash = h; // single assignment = thread safe hashcode.
        }
        return h;
    }
    public int compareTo(Object o) {
        MyToken token = (MyToken)o;
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
        if (this.length == token.length) {
            for (int i = 0; i < this.length && i < token.length; i++) {
                char c1 = this.chars[i + this.start];
                char c2 = token.chars[i + token.start];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            return 0;
        }
        //int len = Math.min(this.length, token.length);
        for (int i = 0; i < this.length && i < token.length; i++) {
            char c1 = this.chars[i + this.start];
            char c2 = token.chars[i + token.start];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return this.length - token.length;
    }
}