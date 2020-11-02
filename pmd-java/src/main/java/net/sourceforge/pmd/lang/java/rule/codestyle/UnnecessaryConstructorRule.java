/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * This rule detects when a constructor is not necessary;
 * i.e., when there is only one constructor, it’s public, has an empty body,
 * and takes no arguments.
 */
public class UnnecessaryConstructorRule extends AbstractIgnoredAnnotationRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTEnumDeclaration.class, ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.singletonList("javax.inject.Inject");
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isRegularClass()) {
            checkClassOrEnum(node, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        checkClassOrEnum(node, data);
        return data;
    }

    private void checkClassOrEnum(ASTAnyTypeDeclaration node, Object data) {
        List<ASTConstructorDeclaration> ctors = node.getDeclarations().filterIs(ASTConstructorDeclaration.class).take(2).toList();
        if (ctors.size() == 1 && isExplicitDefaultConstructor(node, ctors.get(0))) {
            addViolation(data, ctors.get(0));
        }
    }


    private boolean isExplicitDefaultConstructor(ASTAnyTypeDeclaration declarator, ASTConstructorDeclaration ctor) {
        return ctor.getArity() == 0
            && !hasIgnoredAnnotation(ctor)
            && hasDefaultCtorVisibility(declarator, ctor)
            && isEmptyBlock(ctor.getBody())
            && ctor.getThrowsList() == null;
    }

    private boolean isEmptyBlock(ASTBlock body) {
        if (body.size() == 0) {
            return true;
        } else if (body.size() == 1) {
            ASTStatement stmt = body.get(0);
            if (stmt instanceof ASTExplicitConstructorInvocation) {
                ASTExplicitConstructorInvocation superCall = (ASTExplicitConstructorInvocation) stmt;
                return superCall.isSuper() && superCall.getArgumentCount() == 0;
            }
        }

        return false;
    }

    private boolean hasDefaultCtorVisibility(ASTAnyTypeDeclaration node, ASTConstructorDeclaration cons) {
        if (node instanceof ASTClassOrInterfaceDeclaration) {
            return node.getVisibility() == cons.getVisibility();
        } else if (node instanceof ASTEnumDeclaration) {
            return cons.getVisibility() == Visibility.V_PRIVATE;
        }
        return false;
    }
}
