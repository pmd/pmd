/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.cpd;

public class TokenEntry implements Comparable {

    public static final TokenEntry EOF = new TokenEntry();
    private char[] chars;
    private int hash;
    private String image;
    private int index;
    private String tokenSrcID;
    private int beginLine;

    private int sortCode;

    private TokenEntry() {
        this.image = "EOF";
        this.chars = image.toCharArray();
        this.tokenSrcID = "EOFMarker";
    }

    public TokenEntry(String image, int index, String tokenSrcID, int beginLine) {
        this.image = image;
        this.index = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
        this.chars = image.toCharArray();
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
            if (token.image.length() != image.length()) {
                return false;
            }
            for (int i = 0; i < image.length(); i++) {
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
                for (int i = 0 ; i < image.length(); i++) {
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
        if (image.length() == token.image.length()) {
            for (int i = 0; i < image.length() && i < token.image.length(); i++) {
                char c1 = this.chars[i];
                char c2 = token.chars[i];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            return 0;
        }
        for (int i = 0; i < image.length() && i < token.image.length(); i++) {
            char c1 = this.chars[i];
            char c2 = token.chars[i];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return image.length()  - token.image.length();
    }
}