/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:00:28 AM
 */
package net.sourceforge.pmd.cpd;

public class Token {

    private char c;
    private int index;
    private String tokenSrcID;

    public Token(char c, int index, String tokenSrcID) {
        this.c = c;
        this.index = index;
        this.tokenSrcID = tokenSrcID;
    }

    public String getImage() {
        return String.valueOf(c);
    }

    public int getIndex() {
        return index;
    }

    public String getTokenSrcID() {
        return tokenSrcID;
    }

    public String toString() {
        return "[" + tokenSrcID + "," + index +"," + c+"]";
    }

}
