/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:00:28 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;

public class TokenEntry implements Serializable {

    private String image;
    private int index;
    private String tokenSrcID;
    private int beginLine;

    // don't use this, just for Serialization
    protected TokenEntry() {
    }

    public TokenEntry(String image, int index, String tokenSrcID, int beginLine) {
        this.image = image;
        this.index = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
    }

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

    public String toString() {
        return "[" + tokenSrcID + "," + index + "," + image + "," + beginLine + "]";
    }

}
