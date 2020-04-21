/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public interface PLSQLNode extends ScopedNode, JjtreeNode<PLSQLNode> {

    /** Accept the visitor. **/
    Object jjtAccept(PLSQLParserVisitor visitor, Object data);

    @Override
    Scope getScope();

    void setScope(Scope scope);

}
