package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ScopedNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

public interface JavaNode extends ScopedNode {

    /**
     * Accept the visitor. *
     */
    Object jjtAccept(JavaParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    Object childrenAccept(JavaParserVisitor visitor, Object data);

    Scope getScope();

    void setScope(Scope scope);
}
