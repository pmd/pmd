/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;


public interface JavaNode extends ScopedNode {

    /**
     * Accept the visitor. *
     */
    Object jjtAccept(JavaParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     */
    Object childrenAccept(JavaParserVisitor visitor, Object data);


    @Override
    Scope getScope();


    void setScope(Scope scope);


    default Node getLastChild() {
        return jjtGetChild(jjtGetNumChildren() - 1);
    }


    default Node getFirstChild() {
        return jjtGetChild(0);
    }

}
