/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:50:18 PM
 */
package net.sourceforge.pmd.symboltable;

public class OldSymbol {

    private String image;
    private int line;

    public OldSymbol(String image, int line) {
        this.image = image;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public String getImage() {
        return image;
    }

    public boolean equals(Object o) {
        OldSymbol s = (OldSymbol)o;
        return s.image.equals(this.image);
    }

    public int hashCode() {
        return image.hashCode();
    }

    public String toString() {
        return image + ":" + line;
    }
}
