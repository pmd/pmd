/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
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
        super.addRuleChainVisit(ASTUserClass.class);
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node)) {
            return data;
        }

        List<ASTVariableDeclaration> variableDecls = node.findDescendantsOfType(ASTVariableDeclaration.class);
        for (ASTVariableDeclaration varDecl : variableDecls) {
            findSafeLiterals(varDecl);
        }

        List<ASTField> fieldDecl = node.findDescendantsOfType(ASTField.class);
        for (ASTField fDecl : fieldDecl) {
            findSafeLiterals(fDecl);
        }

        List<ASTNewObjectExpression> newObjects = node.findDescendantsOfType(ASTNewObjectExpression.class);
        for (ASTNewObjectExpression newObj : newObjects) {
            checkNewObjects(newObj, data);
        }

        listOfStringLiteralVariables.clear();

        return data;
    }

    private void findSafeLiterals(AbstractApexNode<?> node) {
        ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {
            ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                listOfStringLiteralVariables.add(Helper.getFQVariableName(variable));
            }
        }
    }

    /**
     * Traverses all new declarations to find PageReferences
     * 
     * @param node
     * @param data
     */
    private void checkNewObjects(ASTNewObjectExpression node, Object data) {

        ASTMethod method = node.getFirstParentOfType(ASTMethod.class);
        if (method != null && Helper.isTestMethodOrClass(method)) {
            return;
        }

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
            if (variable.jjtGetChildIndex() == 0
                    && !listOfStringLiteralVariables.contains(Helper.getFQVariableName(variable))) {
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
