/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJUnitRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (isJUnitMethod(method, data)) {
            if (!isExpectAnnotated(method.getParent())) {
                Map<String, VariableNameDeclaration> variables = getVariables(method);

                Scope classScope = method.getScope().getParent();
                Map<String, List<NameOccurrence>> expectables = getRuleAnnotatedExpectedExceptions(classScope);

                if (!containsExpectOrAssert(method.getBody(), expectables, variables)) {
                    addViolation(data, method);
                }
            }
        }
        return data;
    }

    private boolean containsExpectOrAssert(Node n,
                                           Map<String, List<NameOccurrence>> expectables,
                                           Map<String, VariableNameDeclaration> variables) {
        if (n instanceof ASTStatementExpression) {
            if (isExpectStatement((ASTStatementExpression) n, expectables)
                    || isAssertOrFailStatement((ASTStatementExpression) n)
                    || isVerifyStatement((ASTStatementExpression) n)
                    || isSoftAssertionStatement((ASTStatementExpression) n, variables)) {
                return true;
            }
        } else {
            for (int i = 0; i < n.getNumChildren(); i++) {
                Node c = n.getChild(i);
                if (containsExpectOrAssert(c, expectables, variables)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, VariableNameDeclaration> getVariables(ASTMethodDeclaration method) {
        Map<String, VariableNameDeclaration> variables = new HashMap<>();
        for (VariableNameDeclaration vnd : method.getScope().getDeclarations(VariableNameDeclaration.class).keySet()) {
            variables.put(vnd.getName(), vnd);
        }
        return variables;
    }

    /**
     * Gets a list of NameDeclarations for all the fields that have type
     * ExpectedException and have a Rule annotation.
     *
     * @param classScope
     *            The class scope to search for
     * @return See description
     */
    private Map<String, List<NameOccurrence>> getRuleAnnotatedExpectedExceptions(Scope classScope) {
        Map<String, List<NameOccurrence>> result = new HashMap<>();
        Map<NameDeclaration, List<NameOccurrence>> decls = classScope.getDeclarations();

        for (Map.Entry<NameDeclaration, List<NameOccurrence>> entry : decls.entrySet()) {
            Node parent = entry.getKey().getNode().getParent().getParent().getParent();
            if (parent.hasDescendantOfType(ASTMarkerAnnotation.class)
                    && parent.getFirstChildOfType(ASTFieldDeclaration.class) != null) {
                String annot = parent.getFirstDescendantOfType(ASTMarkerAnnotation.class).getChild(0).getImage();
                if (!"Rule".equals(annot) && !"org.junit.Rule".equals(annot)) {
                    continue;
                }

                Node type = parent.getFirstDescendantOfType(ASTReferenceType.class);
                if (!"ExpectedException".equals(type.getChild(0).getImage())) {
                    continue;
                }
                result.put(entry.getKey().getName(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Tells if the node contains a Test annotation with an expected exception.
     */
    private boolean isExpectAnnotated(Node methodParent) {
        List<ASTNormalAnnotation> annotations = methodParent.findDescendantsOfType(ASTNormalAnnotation.class);
        for (ASTNormalAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstChildOfType(ASTName.class);
            if (name != null && TypeHelper.isA(name, JUNIT4_CLASS_NAME)) {
                List<ASTMemberValuePair> memberValues = annotation.findDescendantsOfType(ASTMemberValuePair.class);
                for (ASTMemberValuePair pair : memberValues) {
                    if ("expected".equals(pair.getImage())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tells if the expression is an assert statement or not.
     */
    private boolean isAssertOrFailStatement(ASTStatementExpression expression) {
        if (expression != null) {
            ASTPrimaryExpression pe = expression.getFirstChildOfType(ASTPrimaryExpression.class);
            if (pe != null) {
                Node name = pe.getFirstDescendantOfType(ASTName.class);
                if (name != null) {
                    String img = name.getImage();
                    if (img != null && (img.startsWith("assert") || img.startsWith("fail")
                            || img.startsWith("Assert.assert") || img.startsWith("Assert.fail"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tells if the expression is verify statement or not
     */
    private boolean isVerifyStatement(ASTStatementExpression expression) {
        if (expression != null) {
            ASTPrimaryExpression pe = expression.getFirstChildOfType(ASTPrimaryExpression.class);
            if (pe != null) {
                Node name = pe.getFirstDescendantOfType(ASTName.class);
                if (name != null) {
                    String img = name.getImage();
                    if (img != null && (img.startsWith("verify") || img.startsWith("Mockito.verify"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isExpectStatement(ASTStatementExpression expression,
            Map<String, List<NameOccurrence>> expectables) {
        ASTPrimaryExpression pe = expression.getFirstChildOfType(ASTPrimaryExpression.class);
        if (pe != null) {
            ASTPrimaryPrefix primaryPrefix = pe.getFirstChildOfType(ASTPrimaryPrefix.class);
            Node name = pe.getFirstDescendantOfType(ASTName.class);
            if (!primaryPrefix.usesThisModifier() && name != null) {
                String[] parts = name.getImage().split("\\.");
                if (parts.length >= 2) {
                    String varname = parts[0];
                    String methodName = parts[1];
                    if (expectables.containsKey(varname) && "expect".equals(methodName)) {
                        return true;
                    }
                }
            } else if (primaryPrefix.usesThisModifier()) {
                List<ASTPrimarySuffix> primarySuffixes = pe.findChildrenOfType(ASTPrimarySuffix.class);
                if (primarySuffixes.size() >= 2) {
                    String varname = primarySuffixes.get(0).getImage();
                    String methodName = primarySuffixes.get(1).getImage();
                    if (expectables.containsKey(varname) && "expect".equals(methodName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSoftAssertionStatement(ASTStatementExpression expression,
                                             Map<String, VariableNameDeclaration> variables) {
        if (expression != null) {
            ASTPrimaryExpression pe = expression.getFirstChildOfType(ASTPrimaryExpression.class);
            if (pe != null) {
                Node name = pe.getFirstDescendantOfType(ASTName.class);
                if (name != null) {
                    String img = name.getImage();
                    if (img.indexOf(".") == -1) {
                        return false;
                    }
                    String[] tokens = img.split("\\.");
                    String methodName = tokens[1];
                    boolean methodIsAssertAll = "assertAll".equals(methodName);

                    String varName = tokens[0];
                    boolean variableTypeIsSoftAssertion = variables.containsKey(varName)
                            && TypeHelper.isA(variables.get(varName), "org.assertj.core.api.AbstractSoftAssertions");

                    return methodIsAssertAll && variableTypeIsSoftAssertion;
                }
            }
        }
        return false;
    }
}
