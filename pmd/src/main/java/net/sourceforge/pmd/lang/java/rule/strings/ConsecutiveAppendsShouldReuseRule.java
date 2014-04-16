/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Original rule was written with XPath, but didn't verify whether the two calls to append
 * would have been done on the same variable.
 * 
 * <pre>
//BlockStatement[./Statement/StatementExpression//PrimaryPrefix/Name[ends-with(@Image,'.append')]
                                      [substring-before(@Image, '.') =
                                         ancestor::Block//LocalVariableDeclaration[./Type//ClassOrInterfaceType[@Image='StringBuffer']]//VariableDeclaratorId/@Image
                                      ]
                ]/following-sibling::*[1][./Statement/StatementExpression//PrimaryPrefix/Name[ends-with(@Image,'.append')]
                                         [substring-before(@Image, '.') = 
                                             ancestor::Block//LocalVariableDeclaration[./Type//ClassOrInterfaceType[@Image='StringBuffer']]//VariableDeclaratorId/@Image
                                         ]
                                      ] 
|
//BlockStatement[./Statement/StatementExpression//PrimaryPrefix/Name[ends-with(@Image,'.append')]
                                      [substring-before(@Image, '.') = 
                                         ancestor::Block//LocalVariableDeclaration[./Type//ClassOrInterfaceType[@Image='StringBuilder']]//VariableDeclaratorId/@Image
                                      ]
                ]/following-sibling::*[1][./Statement/StatementExpression//PrimaryPrefix/Name[ends-with(@Image,'.append')]
                                         [substring-before(@Image, '.') = 
                                             ancestor::Block//LocalVariableDeclaration[./Type//ClassOrInterfaceType[@Image='StringBuilder']]//VariableDeclaratorId/@Image
                                         ]
                                      ]

 * </pre>
 *
 */
public class ConsecutiveAppendsShouldReuseRule  extends AbstractJavaRule {

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
                return (ASTBlockStatement)nextSibling;
            }
        }
        return null;
    }
    private String getVariableAppended(ASTBlockStatement node) {
        if (isFirstChild(node, ASTStatement.class)) {
            ASTStatement statement = (ASTStatement) node.jjtGetChild(0);
            if (isFirstChild(statement, ASTStatementExpression.class)) {
                ASTStatementExpression stmtExp = (ASTStatementExpression) statement.jjtGetChild(0);
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
            }
        }
        return null;
    }
    private boolean isAStringBuilderBuffer(ASTPrimaryPrefix prefix, String name) {
        Map<VariableNameDeclaration, List<NameOccurrence>> declarations = prefix.getScope().getDeclarations(VariableNameDeclaration.class);
        for (VariableNameDeclaration decl : declarations.keySet()) {
            if (decl.getName().equals(name) && TypeHelper.isEither(decl, StringBuilder.class, StringBuffer.class)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isFirstChild(Node node, Class<?> clazz) {
        if (node.jjtGetNumChildren() == 1 && clazz.isAssignableFrom(node.jjtGetChild(0).getClass())) {
            return true;
        }
        return false;
    }
}
