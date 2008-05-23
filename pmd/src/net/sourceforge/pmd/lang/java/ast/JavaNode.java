package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symboltable.Scope;

public interface JavaNode extends Node {

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(JavaParserVisitor visitor, Object data);

    Scope getScope();

    void setScope(Scope scope);
}
