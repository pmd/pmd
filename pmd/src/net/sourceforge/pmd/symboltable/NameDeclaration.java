/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:48:50 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public class NameDeclaration {

    private SimpleNode node;
    private Kind kind;

    public NameDeclaration(SimpleNode node, Kind kind) {
        this.node = node;
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

    public boolean equals(Object o) {
        NameDeclaration n = (NameDeclaration)o;
        return n.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return node.getImage() + ":" + node.getBeginLine();
    }
}
