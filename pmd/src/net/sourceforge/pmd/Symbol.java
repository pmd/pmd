/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:48:50 AM
 */
package net.sourceforge.pmd;

public class Symbol {

    private String image;
    private int line;

    public Symbol(String image, int line) {
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
        Symbol s = (Symbol)o;
        return s.image.equals(this.image);
    }

    public int hashCode() {
        return image.hashCode();
    }

    public String toString() {
        return image + ":" + line;
    }
}
