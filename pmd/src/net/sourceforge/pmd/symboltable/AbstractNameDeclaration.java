/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.IPositionProvider;
import net.sourceforge.pmd.ast.SimpleNode;

public abstract class AbstractNameDeclaration implements IPositionProvider {

    protected SimpleNode node;

    public AbstractNameDeclaration(SimpleNode node) {
        this.node = node;
    }
    public SimpleNode getNode() {
        return node;
    }
    public Scope getScope() {
        return node.getScope();
    }
    public String getImage() {
        return node.getImage();
    }
    public int getBeginColumn() {
        return node.getBeginColumn();
    }
    public int getBeginLine() {
        return node.getBeginLine();
    }
    public int getEndColumn() {
        return node.getEndColumn();
    }
    public int getEndLine() {
        return node.getEndLine();
    }
}
