/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public abstract class AbstractNameDeclaration {

    protected SimpleNode node;

    public AbstractNameDeclaration(SimpleNode node) {
        this.node = node;
    }

    public Scope getScope() {
        return node.getScope();
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }
}
