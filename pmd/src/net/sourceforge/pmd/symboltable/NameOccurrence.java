/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 11:05:43 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;


public class NameOccurrence {

    private String image;
    private int beginLine;

    public NameOccurrence(String image, int beginLine) {
        this.image = image;
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public String getImage() {
        return this.image;
    }

    public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence)o;
        return n.getImage().equals(image);
    }

    public int hashCode() {
        return  image.hashCode();
    }

    public String toString() {
        return image + ":" + beginLine;
    }

    public NameDeclaration copyIntoNameDeclaration() {
        SimpleNode node = new SimpleNode(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(beginLine);
        return new NameDeclaration(node, Kind.UNKNOWN);
    }

}
