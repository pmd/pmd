/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Looking for potential Open redirect via PageReference variable input
 *
 * @author sergey.gorbaty
 */
public class ApexOpenRedirectRule extends AbstractApexRule {
    private static final String PAGEREFERENCE = "PageReference";
    private final Set<String> listOfStringLiteralVariables = new HashSet<>();


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        for (ASTAssignmentExpression assignment : node.descendants(ASTAssignmentExpression.class)) {
            findSafeLiterals(assignment);
        }

        for (ASTVariableDeclaration varDecl : node.descendants(ASTVariableDeclaration.class)) {
            findSafeLiterals(varDecl);
        }

        for (ASTField fDecl : node.descendants(ASTField.class)) {
            findSafeLiterals(fDecl);
        }

        for (ASTNewObjectExpression newObj : node.descendants(ASTNewObjectExpression.class)) {
            checkNewObjects(newObj, data);
        }

        listOfStringLiteralVariables.clear();

        return data;
    }

    private void findSafeLiterals(ApexNode<?> node) {
        ASTBinaryExpression binaryExp = node.firstChild(ASTBinaryExpression.class);
        if (binaryExp != null) {
            findSafeLiterals(binaryExp);
        }

        ASTLiteralExpression literal = node.firstChild(ASTLiteralExpression.class);
        if (literal != null) {
            int index = literal.getIndexInParent();
            if (index == 0) {
                if (node instanceof ASTVariableDeclaration) {
                    addVariable((ASTVariableDeclaration) node);
                } else if (node instanceof ASTBinaryExpression) {
                    ASTVariableDeclaration parent = node.ancestors(ASTVariableDeclaration.class).first();
                    if (parent != null) {
                        addVariable(parent);
                    }
                    ASTAssignmentExpression assignment = node.ancestors(ASTAssignmentExpression.class).first();
                    if (assignment != null) {
                        ASTVariableExpression var = assignment.firstChild(ASTVariableExpression.class);
                        if (var != null) {
                            addVariable(var);
                        }
                    }

                }
            }
        } else {
            if (node instanceof ASTField) {
                ASTField field = (ASTField) node;
                if ("String".equalsIgnoreCase(field.getType())) {
                    if (field.getValue() != null) {
                        listOfStringLiteralVariables.add(Helper.getFQVariableName(field));
                    }
                }
            }
        }

    }

    private void addVariable(ASTVariableDeclaration node) {
        ASTVariableExpression variable = node.firstChild(ASTVariableExpression.class);
        addVariable(variable);
    }

    private void addVariable(ASTVariableExpression node) {
        if (node != null) {
            listOfStringLiteralVariables.add(Helper.getFQVariableName(node));
        }
    }

    /**
     * Traverses all new declarations to find PageReferences
     *
     * @param node
     * @param data
     */
    private void checkNewObjects(ASTNewObjectExpression node, Object data) {

        ASTMethod method = node.ancestors(ASTMethod.class).first();
        if (method != null && Helper.isTestMethodOrClass(method)) {
            return;
        }

        if (PAGEREFERENCE.equalsIgnoreCase(node.getType())) {
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
        for (ASTVariableExpression variable : node.children(ASTVariableExpression.class)) {
            if (variable.getIndexInParent() == 0
                    && !listOfStringLiteralVariables.contains(Helper.getFQVariableName(variable))) {
                asCtx(data).addViolation(variable);
            }
        }

        // PageReference(foo + bar)
        for (ASTBinaryExpression z : node.children(ASTBinaryExpression.class)) {
            getObjectValue(z, data);
        }
    }

}
