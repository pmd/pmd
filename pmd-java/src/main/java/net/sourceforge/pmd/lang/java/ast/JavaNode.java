/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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


    /**
     * Accepts a generic visitor and calls back the visitor's visit method.
     */
    <T> void jjtAccept(JavaGenericSideEffectingVisitor<T> visitor, T data);


    <T> void childrenAccept(JavaGenericSideEffectingVisitor<T> visitor, T data);


    @Override
    Scope getScope();


    void setScope(Scope scope);
}
