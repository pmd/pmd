/*
 * User: tom
 * Date: Oct 9, 2002
 * Time: 6:02:29 PM
 */
package net.sourceforge.pmd.symboltable;

public class Qualifier {

    public static final Qualifier THIS = new Qualifier("this");
    public static final Qualifier SUPER = new Qualifier("super");

    private String image;

    private Qualifier(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String toString() {
        return getImage();
    }
}
