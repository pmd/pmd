/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 11:05:43 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public class NameOccurrence {

    private SimpleNode node;

    public NameOccurrence(SimpleNode node) {
        this.node = node;
    }

    public int getBeginLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

    public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence)o;
        return n.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return node.getImage() + ":" + node.getBeginLine();
    }

    public NameDeclaration copyIntoNameDeclaration() {
        return new NameDeclaration(node, Kind.UNKNOWN);
    }

}
