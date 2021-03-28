/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class LooseCouplingRule extends AbstractJavaRulechainRule {

    public LooseCouplingRule() {
        super(ASTClassOrInterfaceType.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (isConcreteCollectionType(node)
            && !isInOverriddenMethodSignature(node)
            && !(node.getParent() instanceof ASTConstructorCall
            || node.getParent() instanceof ASTMethodReference)) {
            addViolation(data, node, node.getSimpleName());
        }
        return null;
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
