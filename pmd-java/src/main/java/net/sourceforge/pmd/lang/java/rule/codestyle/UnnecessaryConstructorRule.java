/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;

/**
 * This rule detects when a constructor is not necessary;
 * i.e., when there is only one constructor, it’s public, has an empty body,
 * and takes no arguments.
 */
public class UnnecessaryConstructorRule extends AbstractIgnoredAnnotationRule {

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.singletonList("javax.inject.Inject");
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        ASTConstructorDeclaration cons = node.getFirstDescendantOfType(ASTConstructorDeclaration.class);
        if (isExplicitDefaultConstructor(node)
            && haveSameAccessModifier(node, cons)) {
            addViolation(data, cons);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {

        ASTConstructorDeclaration cons = node.getFirstDescendantOfType(ASTConstructorDeclaration.class);
        if (isExplicitDefaultConstructor(node) && cons.isPrivate()) {
            addViolation(data, cons);
        }

        return super.visit(node, data);
    }

    /**
     * Returns {@code true} if the node has only one {@link ASTConstructorDeclaration}
     * child node and the constructor has empty body or simply invokes the superclass
     * constructor with no arguments.
     *
     * @param node the node to check
     */
    private boolean isExplicitDefaultConstructor(Node node) {

        List<ASTConstructorDeclaration> nodes
            = node.findDescendantsOfType(ASTConstructorDeclaration.class);

        if (nodes.size() != 1) {
            return false;
        }

        ASTConstructorDeclaration cdnode = nodes.get(0);

        return cdnode.getArity() == 0 && !hasIgnoredAnnotation(cdnode)
            && !cdnode.hasDescendantOfType(ASTBlockStatement.class) && !cdnode.hasDescendantOfType(ASTNameList.class)
            && hasDefaultConstructorInvocation(cdnode);
    }

    /**
     * Returns {@code true} if the constructor simply invokes superclass constructor
     * with no arguments or doesn't invoke any constructor, otherwise {@code false}.
     *
     * @param cons the node to check
     */
    private boolean hasDefaultConstructorInvocation(ASTConstructorDeclaration cons) {
        ASTExplicitConstructorInvocation inv = cons.getFirstChildOfType(ASTExplicitConstructorInvocation.class);
        return inv == null || inv.isSuper() && inv.getArgumentCount() == 0;
    }

    /**
     * Returns {@code true} if access modifier of construtor is same as class's,
     * otherwise {@code false}.
     *
     * @param node the class declaration node
     * @param cons the constructor declaration node
     */
    private boolean haveSameAccessModifier(ASTClassOrInterfaceDeclaration node, ASTConstructorDeclaration cons) {
        return node.isPrivate() && cons.isPrivate()
            || node.isProtected() && cons.isProtected()
            || node.isPublic() && cons.isPublic()
            || node.isPackagePrivate() && cons.isPackagePrivate();
    }
}
