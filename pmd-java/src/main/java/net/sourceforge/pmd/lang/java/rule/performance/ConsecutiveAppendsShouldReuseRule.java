/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ConsecutiveAppendsShouldReuseRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        String variable = getVariableAppended(node);
        if (variable != null) {
            ASTBlockStatement nextSibling = getNextBlockStatementSibling(node);
            if (nextSibling != null) {
                String nextVariable = getVariableAppended(nextSibling);
                if (nextVariable != null && nextVariable.equals(variable)) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

    private ASTBlockStatement getNextBlockStatementSibling(Node node) {
        Node parent = node.jjtGetParent();
        int childIndex = -1;
        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            if (parent.jjtGetChild(i) == node) {
                childIndex = i;
                break;
            }
        }
        if (childIndex + 1 < parent.jjtGetNumChildren()) {
            Node nextSibling = parent.jjtGetChild(childIndex + 1);
            if (nextSibling instanceof ASTBlockStatement) {
                return (ASTBlockStatement) nextSibling;
            }
        }
        return null;
    }

    private String getVariableAppended(ASTBlockStatement node) {
        if (isFirstChild(node, ASTStatement.class)) {
            ASTStatement statement = (ASTStatement) node.jjtGetChild(0);
            if (isFirstChild(statement, ASTStatementExpression.class)) {
                ASTStatementExpression stmtExp = (ASTStatementExpression) statement.jjtGetChild(0);
                if (stmtExp.jjtGetNumChildren() == 1) {
                    ASTPrimaryPrefix primaryPrefix = stmtExp.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                    if (primaryPrefix != null) {
                        ASTName name = primaryPrefix.getFirstChildOfType(ASTName.class);
                        if (name != null) {
                            String image = name.getImage();
                            if (image.endsWith(".append")) {
                                String variable = image.substring(0, image.indexOf('.'));
                                if (isAStringBuilderBuffer(primaryPrefix, variable)) {
                                    return variable;
                                }
                            }
                        }
                    }
                } else {
                    final ASTExpression exp = stmtExp.getFirstDescendantOfType(ASTExpression.class);
                    if (isFirstChild(exp, ASTPrimaryExpression.class)) {
                        final ASTPrimarySuffix primarySuffix = ((ASTPrimaryExpression) exp.jjtGetChild(0))
                                .getFirstDescendantOfType(ASTPrimarySuffix.class);
                        if (primarySuffix != null) {
                            final String name = primarySuffix.getImage();
                            if ("append".equals(name)) {
                                final ASTPrimaryExpression pExp = stmtExp
                                        .getFirstDescendantOfType(ASTPrimaryExpression.class);
                                if (pExp != null) {
                                    final ASTName astName = stmtExp.getFirstDescendantOfType(ASTName.class);
                                    if (astName != null) {
                                        final String variable = astName.getImage();
                                        if (isAStringBuilderBuffer(primarySuffix, variable)) {
                                            return variable;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (isFirstChild(node, ASTLocalVariableDeclaration.class)) {
            ASTLocalVariableDeclaration lvd = (ASTLocalVariableDeclaration) node.jjtGetChild(0);

            ASTVariableDeclaratorId vdId = lvd.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
            ASTExpression exp = lvd.getFirstDescendantOfType(ASTExpression.class);

            if (exp != null) {
                ASTPrimarySuffix primarySuffix = exp.getFirstDescendantOfType(ASTPrimarySuffix.class);
                if (primarySuffix != null) {
                    final String name = primarySuffix.getImage();
                    if ("append".equals(name)) {
                        String variable = vdId.getImage();
                        if (isAStringBuilderBuffer(primarySuffix, variable)) {
                            return variable;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isAStringBuilderBuffer(AbstractJavaNode node, String name) {
        Map<VariableNameDeclaration, List<NameOccurrence>> declarations = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (VariableNameDeclaration decl : declarations.keySet()) {
            if (decl.getName().equals(name) && TypeHelper.isEither(decl, StringBuilder.class, StringBuffer.class)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFirstChild(Node node, Class<?> clazz) {
        return node.jjtGetNumChildren() == 1 && clazz.isAssignableFrom(node.jjtGetChild(0).getClass());
    }
}
