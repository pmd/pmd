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

    public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence)o;
        return n.getObjectName().equals(node.getImage());
    }

    public int hashCode() {
        return getObjectName().hashCode();
    }

    public String toString() {
        return node.getImage() + ":" + node.getBeginLine();
    }

    public String getObjectName() {
        return (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
    }

}
