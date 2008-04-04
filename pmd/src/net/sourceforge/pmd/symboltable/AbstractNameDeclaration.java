/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected Node node;

    public AbstractNameDeclaration(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public String getImage() {
        return node.getImage();
    }

    public Scope getScope() {
        return node.getScope();
    }
}
