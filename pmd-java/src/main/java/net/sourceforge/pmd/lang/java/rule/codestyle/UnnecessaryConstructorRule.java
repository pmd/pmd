/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
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
        Collection<String> defaultValues = new HashSet<>();
        defaultValues.add("javax.inject.Inject");
        return defaultValues;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {

        if (isExplicitDefaultConstructor(node)
            && hasDefaultAccessModifier((ASTClassOrInterfaceDeclaration) node.jjtGetParent(),
            node.getFirstDescendantOfType(ASTConstructorDeclaration.class))) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTEnumBody node, Object data) {

        if (isExplicitDefaultConstructor(node)
            && node.getFirstDescendantOfType(ASTConstructorDeclaration.class).isPrivate()) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    /**
     * @param node
     *            the node to check
     * @return {@code true} if the node has only one {@link ASTConstructorDeclaration} child node
     *         and the constructor has empty body or simply invokes the superclass constructor with no arguments
     */
    private boolean isExplicitDefaultConstructor(Node node) {

        List<ASTConstructorDeclaration> nodes = getClassConstructor(node);

        if (nodes.size() != 1) {
            return false;
        }

        ASTConstructorDeclaration cdnode = nodes.get(0);

        return cdnode.getParameterCount() == 0 && !hasIgnoredAnnotation(cdnode)
            && !cdnode.hasDescendantOfType(ASTBlockStatement.class) && !cdnode.hasDescendantOfType(ASTNameList.class)
            && hasDefaultConstructorInvocation(cdnode);
    }

    /**
     * @param node
     *            the class node to get constructors
     * @return List of class's constuctors. Return an empty list if the class has no explicit constructor
     */
    private List<ASTConstructorDeclaration> getClassConstructor(Node node) {
        List<ASTConstructorDeclaration> nodes
            = node.findDescendantsOfType(ASTConstructorDeclaration.class);
        Iterator<ASTConstructorDeclaration> iterator = nodes.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getNthParent(2) != node) {
                iterator.remove();
            }
        }

        return nodes;
    }


    /**
     * @param cons
     *            the node to check
     * @return {@code true} if the constructor simply invokes superclass constructor
     *         with no arguments or doesn't invoke any constructor, otherwise {@code false}
     */
    private boolean hasDefaultConstructorInvocation(ASTConstructorDeclaration cons) {
        ASTExplicitConstructorInvocation inv = cons.getFirstChildOfType(ASTExplicitConstructorInvocation.class);
        return inv == null || inv.isSuper() && inv.getArgumentCount() == 0;
    }

    /**
     *
     * @param node
     *           the class declaration node
     * @param cons
     *            the constructor declaration node
     * @return {@code true} if access modifier of construtor is same as class's, otherwise {@code false}
     */
    private boolean hasDefaultAccessModifier(ASTClassOrInterfaceDeclaration node, ASTConstructorDeclaration cons) {
        return node.isPrivate() && cons.isPrivate()
            || node.isProtected() && cons.isProtected()
            || node.isPublic() && cons.isPublic()
            || node.isPackagePrivate() && cons.isPackagePrivate();
    }
}
