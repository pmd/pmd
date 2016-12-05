/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.data.ast.Identifier;
import apex.jorje.data.ast.TypeRef.ClassTypeRef;

/**
 * Looking for potential Open redirect via PageReference variable input
 * 
 * @author sergey.gorbaty
 */
public class ApexOpenRedirectRule extends AbstractApexRule {
    private static final String PAGEREFERENCE = "PageReference";
    private final Set<String> listOfStringLiteralVariables = new HashSet<>();

    public ApexOpenRedirectRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTNewObjectExpression node, Object data) {
        checkNewObjects(node, data);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        findSafeLiterals(node);

        return data;
    }

    private void findSafeLiterals(AbstractApexNode<?> node) {
        ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {
            ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                StringBuilder sb = new StringBuilder().append(variable.getNode().getDefiningType()).append(":")
                        .append(variable.getNode().getIdentifier().value);
                listOfStringLiteralVariables.add(sb.toString());
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        findSafeLiterals(node);
        return data;
    }

    /**
     * Traverses all new declarations to find PageReferences
     * 
     * @param node
     * @param data
     */
    private void checkNewObjects(ASTNewObjectExpression node, Object data) {
        ClassTypeRef classRef = (ClassTypeRef) node.getNode().getTypeRef();
        Identifier identifier = classRef.className.get(0);

        if (identifier.value.equalsIgnoreCase(PAGEREFERENCE)) {
            getObjectValue(node, data);
        }
    }

    /**
     * Finds any variables being present in PageReference constructor
     * 
     * @param node
     *            - PageReference
     * @param data
     * 
     */
    private void getObjectValue(ApexNode<?> node, Object data) {
        // PageReference(foo);
        final List<ASTVariableExpression> variableExpressions = node.findChildrenOfType(ASTVariableExpression.class);
        for (ASTVariableExpression variable : variableExpressions) {
            StringBuilder sb = new StringBuilder().append(variable.getNode().getDefiningType()).append(":")
                    .append(variable.getNode().getIdentifier().value);
            if (variable.jjtGetChildIndex() == 0 && !listOfStringLiteralVariables.contains(sb.toString())) {
                addViolation(data, variable);
            }
        }

        // PageReference(foo + bar)
        final List<ASTBinaryExpression> binaryExpressions = node.findChildrenOfType(ASTBinaryExpression.class);
        for (ASTBinaryExpression z : binaryExpressions) {
            getObjectValue(z, data);
        }
    }

}
