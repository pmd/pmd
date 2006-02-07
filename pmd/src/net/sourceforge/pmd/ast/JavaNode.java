package net.sourceforge.pmd.ast;

public interface JavaNode extends Node {

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data);
}
