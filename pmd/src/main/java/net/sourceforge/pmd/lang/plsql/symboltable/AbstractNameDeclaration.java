/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;

public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected PLSQLNode node;

    public AbstractNameDeclaration(PLSQLNode node) {
        this.node = node;
    }

    public PLSQLNode getNode() {
        return node;
    }

    public String getImage() {
        return node.getImage();
    }

    public Scope getScope() {
        return node.getScope();
    }
}
