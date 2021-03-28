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
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
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
        super(ASTClassOrInterfaceType.class);
        definePropertyDescriptor(ALLOWED_TYPES);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (isConcreteCollectionType(node)
            && !isInOverriddenMethodSignature(node)
            && !isInAllowedSyntacticCtx(node)
            && !isAllowedType(node)) {
            addViolation(data, node, node.getSimpleName());
        }
        return null;
    }

    private boolean isInAllowedSyntacticCtx(ASTClassOrInterfaceType node) {
        return node.getParent() instanceof ASTConstructorCall
            || node.getParent() instanceof ASTTypeExpression
            || node.getParent() instanceof ASTArrayType && node.getParent().getParent() instanceof ASTArrayAllocation;
    }

    private boolean isAllowedType(ASTClassOrInterfaceType node) {
        for (String allowed : getProperty(ALLOWED_TYPES)) {
            if (TypeTestUtil.isA(allowed, node)) {
                return true;
            }
        }
        return false;
    }


    private boolean isConcreteCollectionType(ASTClassOrInterfaceType node) {
        return (TypeTestUtil.isA(Collection.class, node) || TypeTestUtil.isA(Map.class, node))
            && !node.getTypeMirror().isInterface();
    }

    private static boolean isInOverriddenMethodSignature(JavaNode node) {
        JavaNode ancestor = node.ancestors().map(NodeStream.asInstanceOf(ASTMethodDeclaration.class, ASTBlock.class)).first();
        if (ancestor instanceof ASTMethodDeclaration) {
            // then it's in a signature and not the body
            return ((ASTMethodDeclaration) ancestor).isOverridden();
        }
        return false;
    }
}
