/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModuleProvidesDirective;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class LooseCouplingRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> ALLOWED_TYPES =
        PropertyFactory.stringListProperty("allowedTypes")
                       .desc("Exceptions to the rule")
                       .defaultValues("java.util.Properties")
                       .build();

    public LooseCouplingRule() {
        super(ASTClassType.class);
        definePropertyDescriptor(ALLOWED_TYPES);
    }

    @Override
    public Object visit(ASTClassType node, Object data) {
        if (isConcreteCollectionType(node)
            && !isInOverriddenMethodSignature(node)
            && !isInAllowedSyntacticCtx(node)
            && !isAllowedType(node)
            && !isTypeParameter(node)) {
            asCtx(data).addViolation(node, node.getSimpleName());
        }
        return null;
    }

    private boolean isInAllowedSyntacticCtx(ASTClassType node) {
        JavaNode parent = node.getParent();
        return parent instanceof ASTConstructorCall      // new ArrayList<>()
            || parent instanceof ASTTypeExpression       // instanceof, method reference
            || parent instanceof ASTCastExpression       // if we allow instanceof, we should allow cast
            || parent instanceof ASTClassLiteral         // ArrayList.class
            || parent instanceof ASTClassType // AbstractMap.SimpleEntry
            || parent instanceof ASTExtendsList          // extends AbstractMap<...>
            || parent instanceof ASTThisExpression       // Enclosing.this
            || parent instanceof ASTSuperExpression      // Enclosing.super
            || parent instanceof ASTModuleProvidesDirective  // provides <interface> with <implementation>
            || parent instanceof ASTArrayType && parent.getParent() instanceof ASTArrayAllocation;
    }

    private boolean isAllowedType(ASTClassType node) {
        for (String allowed : getProperty(ALLOWED_TYPES)) {
            if (TypeTestUtil.isA(allowed, node)) {
                return true;
            }
        }
        return false;
    }


    private boolean isConcreteCollectionType(ASTClassType node) {
        return (TypeTestUtil.isA(Collection.class, node) || TypeTestUtil.isA(Map.class, node))
            && !node.getTypeMirror().isInterface();
    }

    private static boolean isInOverriddenMethodSignature(JavaNode node) {
        JavaNode ancestor = node.ancestors().map(NodeStream.asInstanceOf(ASTMethodDeclaration.class, ASTBlock.class)).first();
        // when it's in a signature and not the body
        return ancestor instanceof ASTMethodDeclaration && ((ASTMethodDeclaration) ancestor).isOverridden();
    }

    private boolean isTypeParameter(ASTClassType node) {
        return node.getTypeMirror().isTypeVariable();
    }
}
