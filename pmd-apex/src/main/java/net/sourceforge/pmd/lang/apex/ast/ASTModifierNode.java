/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.summit.ast.modifier.AnnotationModifier;
import com.google.summit.ast.modifier.KeywordModifier;
import com.google.summit.ast.modifier.KeywordModifier.Keyword;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.modifier.Modifier;

public class ASTModifierNode extends AbstractApexNode.Many<Modifier> implements AccessNode {

    @Deprecated
    @InternalApi
    public ASTModifierNode(List<Modifier> modifierNode) {
        super(modifierNode);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private static final Map<Keyword, Integer> opcodes = new HashMap<>();

    static {
        opcodes.put(Keyword.PUBLIC, AccessNode.PUBLIC);
        opcodes.put(Keyword.PRIVATE, AccessNode.PRIVATE);
        opcodes.put(Keyword.PROTECTED, AccessNode.PROTECTED);
        opcodes.put(Keyword.ABSTRACT, AccessNode.ABSTRACT);
        opcodes.put(Keyword.STATIC, AccessNode.STATIC);
        opcodes.put(Keyword.FINAL, AccessNode.FINAL);
        opcodes.put(Keyword.TRANSIENT, AccessNode.TRANSIENT);
    }

    @Override
    public int getModifiers() {
        int modifiers = nodes
                .stream()
                .filter(mod -> mod instanceof KeywordModifier)
                .map(mod -> (KeywordModifier) mod)
                .filter(mod -> opcodes.containsKey(mod.getKeyword()))
                .mapToInt(mod -> opcodes.get(mod.getKeyword()))
                .reduce(0, (current, bit) -> current | bit);

        if ((modifiers & PUBLIC) > 0) {
            // Remove PROTECTED and PRIVATE if PUBLIC
            modifiers &= ~PROTECTED;
            modifiers &= ~PRIVATE;
        } else if ((modifiers & PROTECTED) > 0) {
            // Remove PRIVATE if PROTECTED
            modifiers &= ~PRIVATE;
        }
        return modifiers;
    }

    @Override
    public boolean isPublic() {
        return (getModifiers() & PUBLIC) == PUBLIC;
    }

    @Override
    public boolean isProtected() {
        return (getModifiers() & PROTECTED) == PROTECTED;
    }

    @Override
    public boolean isPrivate() {
        return (getModifiers() & PRIVATE) == PRIVATE;
    }

    @Override
    public boolean isAbstract() {
        return (getModifiers() & ABSTRACT) == ABSTRACT;
    }

    @Override
    public boolean isStatic() {
        return (getModifiers() & STATIC) == STATIC;
    }

    @Override
    public boolean isFinal() {
        return (getModifiers() & FINAL) == FINAL;
    }

    @Override
    public boolean isTransient() {
        return (getModifiers() & TRANSIENT) == TRANSIENT;
    }

    private boolean hasKeyword(Keyword keyword) {
        return nodes
                .stream()
                .filter(mod -> mod instanceof KeywordModifier)
                .map(mod -> (KeywordModifier) mod)
                .anyMatch(mod -> mod.getKeyword() == keyword);
    }

    private boolean hasAnnotation(String name) {
        return nodes
                .stream()
                .filter(mod -> mod instanceof AnnotationModifier)
                .map(mod -> (AnnotationModifier) mod)
                .anyMatch(mod -> mod.getName().getString().equalsIgnoreCase(name.toLowerCase()));
    }

    /**
     * Returns true if function has `@isTest` annotation or `testmethod` modifier
     */
    public boolean isTest() {
        return hasAnnotation("isTest") || hasKeyword(Keyword.TESTMETHOD);
    }

    /**
     * Returns true if function has `testmethod` modifier
     */
    public boolean hasDeprecatedTestMethod() {
        return hasKeyword(Keyword.TESTMETHOD);
    }

    public boolean isTestOrTestSetup() {
        return isTest() || hasAnnotation("testSetup");
    }

    public boolean isWithSharing() {
        return hasKeyword(Keyword.WITHSHARING);
    }

    public boolean isWithoutSharing() {
        return hasKeyword(Keyword.WITHOUTSHARING);
    }

    public boolean isInheritedSharing() {
        return hasKeyword(Keyword.INHERITEDSHARING);
    }

    public boolean isWebService() {
        return hasKeyword(Keyword.WEBSERVICE);
    }

    public boolean isGlobal() {
        return hasKeyword(Keyword.GLOBAL);
    }

    public boolean isOverride() {
        return hasKeyword(Keyword.OVERRIDE);
    }

    public boolean isVirtual() {
        return hasKeyword(Keyword.VIRTUAL);
    }
}
