/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public interface PLSQLNode extends Node, ScopedNode {

    /** Accept the visitor. **/
    Object jjtAccept(PLSQLParserVisitor visitor, Object data);


    /**
     * Accept the visitor.
     *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(PLSQLParserVisitor visitor, Object data);

    @Override
    Scope getScope();

    void setScope(Scope scope);

    @Override
    PLSQLNode getChild(int index);

    @Override
    PLSQLNode getParent();


    @Override
    Iterable<? extends PLSQLNode> children();
}
