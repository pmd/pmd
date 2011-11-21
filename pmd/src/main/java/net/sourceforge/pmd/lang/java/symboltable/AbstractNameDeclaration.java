/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;

public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected JavaNode node;

    public AbstractNameDeclaration(JavaNode node) {
        this.node = node;
    }

    public JavaNode getNode() {
        return node;
    }

    public String getImage() {
        return node.getImage();
    }

    public Scope getScope() {
        return node.getScope();
    }
}
