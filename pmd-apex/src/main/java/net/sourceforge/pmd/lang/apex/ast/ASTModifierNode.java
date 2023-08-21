/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.TEST_METHOD;

import apex.jorje.semantic.ast.modifier.ModifierNode;
import apex.jorje.semantic.symbol.type.ModifierTypeInfos;

public final class ASTModifierNode extends AbstractApexNode<ModifierNode> implements AccessNode {

    ASTModifierNode(ModifierNode modifierNode) {
        super(modifierNode);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
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

    /**
     * Returns true if function has `@isTest` annotation or `testmethod` modifier
     */
    public boolean isTest() {
        return node.getModifiers().isTest();
    }

    /**
     * Returns true if function has `testmethod` modifier
     */
    public boolean hasDeprecatedTestMethod() {
        return node.getModifiers().has(TEST_METHOD);
    }

    public boolean isTestOrTestSetup() {
        return node.getModifiers().isTestOrTestSetup();
    }

    public boolean isWithSharing() {
        return node.getModifiers().has(ModifierTypeInfos.WITH_SHARING);
    }

    public boolean isWithoutSharing() {
        return node.getModifiers().has(ModifierTypeInfos.WITHOUT_SHARING);
    }

    public boolean isInheritedSharing() {
        return node.getModifiers().has(ModifierTypeInfos.INHERITED_SHARING);
    }

    public boolean isWebService() {
        return node.getModifiers().has(ModifierTypeInfos.WEB_SERVICE);
    }

    public boolean isGlobal() {
        return node.getModifiers().has(ModifierTypeInfos.GLOBAL);
    }

    public boolean isOverride() {
        return node.getModifiers().has(ModifierTypeInfos.OVERRIDE);
    }

    public boolean isVirtual() {
        return node.getModifiers().has(ModifierTypeInfos.VIRTUAL);
    }
}
