/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.ModifierNode;

public class ASTModifierNode extends AbstractApexNode<ModifierNode> implements AccessNode {

    public ASTModifierNode(ModifierNode modifierNode) {
        super(modifierNode);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int getModifiers() {
        return node.getModifiers().getJavaModifiers();
    }

    @Override
    public boolean isPublic() {
        return (node.getModifiers().getJavaModifiers() & PUBLIC) == PUBLIC;
    }

    @Override
    public boolean isProtected() {
        return (node.getModifiers().getJavaModifiers() & PROTECTED) == PROTECTED;
    }

    @Override
    public boolean isPrivate() {
        return (node.getModifiers().getJavaModifiers() & PRIVATE) == PRIVATE;
    }

    @Override
    public boolean isAbstract() {
        return (node.getModifiers().getJavaModifiers() & ABSTRACT) == ABSTRACT;
    }

    @Override
    public boolean isStatic() {
        return (node.getModifiers().getJavaModifiers() & STATIC) == STATIC;
    }

    @Override
    public boolean isFinal() {
        return (node.getModifiers().getJavaModifiers() & FINAL) == FINAL;
    }

    @Override
    public boolean isTransient() {
        return (node.getModifiers().getJavaModifiers() & TRANSIENT) == TRANSIENT;
    }
}
