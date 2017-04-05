/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
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
import apex.jorje.semantic.symbol.member.variable.StandardFieldInfo;

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
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        List<ASTAssignmentExpression> assignmentExprs = node.findDescendantsOfType(ASTAssignmentExpression.class);
        for (ASTAssignmentExpression assignment : assignmentExprs) {
            findSafeLiterals(assignment);
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
        ASTBinaryExpression binaryExp = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryExp != null) {
            findSafeLiterals(binaryExp);
        }

        ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {
            int index = literal.jjtGetChildIndex();
            if (index == 0) {
                if (node instanceof ASTVariableDeclaration) {
                    addVariable((ASTVariableDeclaration) node);
                } else if (node instanceof ASTBinaryExpression) {
                    ASTVariableDeclaration parent = node.getFirstParentOfType(ASTVariableDeclaration.class);
                    if (parent != null) {
                        addVariable(parent);
                    }
                    ASTAssignmentExpression assignment = node.getFirstParentOfType(ASTAssignmentExpression.class);
                    if (assignment != null) {
                        ASTVariableExpression var = assignment.getFirstChildOfType(ASTVariableExpression.class);
                        if (var != null) {
                            addVariable(var);
                        }
                    }

                }
            }
        } else {
            if (node instanceof ASTField) {
                /*
                 * sergey.gorbaty: Apex Jorje parser is returning a null from
                 * Field.getFieldInfo(), but the info is available from an inner
                 * field. DO NOT attempt to optimize this block without checking
                 * that Jorje parser actually fixed its bug.
                 * 
                 */
                try {
                    final Field f = node.getNode().getClass().getDeclaredField("fieldInfo");
                    f.setAccessible(true);
                    final StandardFieldInfo fieldInfo = (StandardFieldInfo) f.get(node.getNode());
                    if (fieldInfo.getType().getApexName().equalsIgnoreCase("String")) {
                        if (fieldInfo.getValue() != null) {
                            addVariable(fieldInfo);
                        }
                    }

                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                        | IllegalAccessException e) {
                    // preventing exceptions from this code
                }
            }
        }

    }

    private void addVariable(StandardFieldInfo fieldInfo) {
        StringBuilder sb = new StringBuilder().append(fieldInfo.getDefiningType().getApexName()).append(":")
                .append(fieldInfo.getName());
        listOfStringLiteralVariables.add(sb.toString());

    }

    private void addVariable(ASTVariableDeclaration node) {
        ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
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
