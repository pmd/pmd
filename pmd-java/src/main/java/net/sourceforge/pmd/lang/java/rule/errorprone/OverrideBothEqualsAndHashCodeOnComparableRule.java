/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Rule that checks {@link Comparable} classes for proper equals/hashCode implementations.
 * <p>
 * <a href="https://www.jetbrains.com/help/inspectopedia/ComparableImplementedButEqualsNotOverridden.html">ComparableImplementedButEqualsNotOverridden</a>
 *
 * @author <a href="mailto:vpotucek@me.com">Vincent Rudolf Potuček</a>
 * @since 7.17.0
 */
public class OverrideBothEqualsAndHashCodeOnComparableRule extends AbstractJavaRulechainRule {

    private static final String MESSAGE_PREFIX = "When implementing Comparable ";
    private static final String MISSING_HASH_CODE = MESSAGE_PREFIX + "hashCode() should be overridden";
    private static final String MISSING_EQUALS = MESSAGE_PREFIX + "equals() should be overridden";
    private static final String MISSING_EQUALS_AND_HASH_CODE = MESSAGE_PREFIX + "both equals() and hashCode() should be overridden";

    public OverrideBothEqualsAndHashCodeOnComparableRule() {
        super(ASTClassDeclaration.class,
                ASTRecordDeclaration.class,
                ASTAnonymousClassDeclaration.class);
    }

    @Override
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        visitTypeDecl(node, data);
        return null;
    }

    private void visitTypeDecl(ASTTypeDeclaration node, Object data) {
        if (TypeTestUtil.isA(Comparable.class, node) && !node.isInterface() && !node.isAbstract()) {
            ASTMethodDeclaration equalsMethod = null;
            ASTMethodDeclaration hashCodeMethod = null;

            for (ASTMethodDeclaration m : node.getDeclarations(ASTMethodDeclaration.class)) {
                if (JavaAstUtils.isEqualsMethod(m)) {
                    equalsMethod = m;
                } else if (JavaAstUtils.isHashCodeMethod(m)) {
                    hashCodeMethod = m;
                }
            }

            if (equalsMethod == null && hashCodeMethod == null) {
                asCtx(data).addViolationWithMessage(node, MISSING_EQUALS_AND_HASH_CODE);
            } else if (equalsMethod == null) {
                asCtx(data).addViolationWithMessage(hashCodeMethod, MISSING_EQUALS);
            } else if (hashCodeMethod == null) {
                asCtx(data).addViolationWithMessage(equalsMethod, MISSING_HASH_CODE);
            }
        }
    }
}
