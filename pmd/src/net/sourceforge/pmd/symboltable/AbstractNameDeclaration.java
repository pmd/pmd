/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 5:01:37 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected SimpleNode node;

    public AbstractNameDeclaration(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

}
