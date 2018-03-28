/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;

/**
 * This rule detects when a constructor is not necessary;
 * i.e., when there is only one constructor, it’s public, has an empty body,
 * and takes no arguments.
 */
public class UnnecessaryConstructorRule extends AbstractIgnoredAnnotationRule {

    private static final String XPATH_EXPRESSION_TO_CONSTRUCTOR
        = "ClassOrInterfaceBodyDeclaration/ConstructorDeclaration";

    private static final String XPATH_EXPRESSION_TO_ARGLISTEXPRESSION
        = "ExplicitConstructorInvocation/Arguments/ArgumentList/Expression";

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> defaultValues = new HashSet<>();
        defaultValues.add("javax.inject.Inject");
        return defaultValues;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {

        List<Node> nodes = getConstructorDeclarationNodes(node);

        //the node has more than one constructor
        if (nodes.size() != 1) {
            return super.visit(node, data);
        }

        ASTConstructorDeclaration cdnode = (ASTConstructorDeclaration) nodes.get(0);

        if (cdnode.isPublic() && !cdnode.hasDescendantMatchingXPath("FormalParameters/*")
            && !cdnode.hasDescendantOfType(ASTBlockStatement.class) && !cdnode.hasDescendantOfType(ASTNameList.class)
            && !cdnode.hasDescendantMatchingXPath(XPATH_EXPRESSION_TO_ARGLISTEXPRESSION)
            && !hasIgnoredAnnotation(cdnode)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    /**
     * Returns all the constructor declaration nodes.
     *
     * @param node
     *            the node to get constructor declaration nodes
     * @return List of all matching nodes. Returns an empty list if none found.
     */
    private List<Node> getConstructorDeclarationNodes(ASTClassOrInterfaceBody node) {
        try {
            return node.findChildNodesWithXPath(XPATH_EXPRESSION_TO_CONSTRUCTOR);
        } catch (JaxenException e) {
            throw new RuntimeException("XPath expression "
                + XPATH_EXPRESSION_TO_CONSTRUCTOR + " failed: " + e.getLocalizedMessage(), e);
        }
    }
}
