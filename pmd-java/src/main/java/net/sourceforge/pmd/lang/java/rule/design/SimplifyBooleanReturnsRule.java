/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SimplifyBooleanReturnsRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        // only boolean methods should be inspected
        ASTResultType r = node.getResultType();

        if (!r.isVoid()) {
            Node t = r.getChild(0);
            if (t.getNumChildren() == 1) {
                t = t.getChild(0);
                if (t instanceof ASTPrimitiveType && ((ASTPrimitiveType) t).isBoolean()) {
                    return super.visit(node, data);
                }
            }
        }
        // skip method
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // that's the case: if..then..return; return;
        if (!node.hasElse() && isIfJustReturnsBoolean(node) && isJustReturnsBooleanAfter(node)) {
            addViolation(data, node);
            return super.visit(node, data);
        }

        // only deal with if..then..else stmts
        if (node.getNumChildren() != 3) {
            return super.visit(node, data);
        }

        // don't bother if either the if or the else block is empty
        if (node.getChild(1).getNumChildren() == 0 || node.getChild(2).getNumChildren() == 0) {
            return super.visit(node, data);
        }

        Node returnStatement1 = node.getChild(1).getChild(0);
        Node returnStatement2 = node.getChild(2).getChild(0);

        if (returnStatement1 instanceof ASTReturnStatement && returnStatement2 instanceof ASTReturnStatement) {
            Node expression1 = returnStatement1.getChild(0).getChild(0);
            Node expression2 = returnStatement2.getChild(0).getChild(0);
            if (terminatesInBooleanLiteral(returnStatement1) && terminatesInBooleanLiteral(returnStatement2)) {
                addViolation(data, node);
            } else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus
                    ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
                // We get the nodes under the '!' operator
                // If they are the same => error
                if (isNodesEqualWithUnaryExpression(expression1, expression2)) {
                    // second case:
                    // If
                    // Expr
                    // Statement
                    // ReturnStatement
                    // UnaryExpressionNotPlusMinus '!'
                    // Expression E
                    // Statement
                    // ReturnStatement
                    // Expression E
                    // i.e.,
                    // if (foo)
                    // return !a;
                    // else
                    // return a;
                    addViolation(data, node);
                }
            }
        } else if (hasOneBlockStmt(node.getChild(1)) && hasOneBlockStmt(node.getChild(2))) {
            // We have blocks so we must go down three levels (BlockStatement,
            // Statement, ReturnStatement)
            returnStatement1 = returnStatement1.getChild(0).getChild(0).getChild(0);
            returnStatement2 = returnStatement2.getChild(0).getChild(0).getChild(0);

            // if we have 2 return;
            if (isSimpleReturn(returnStatement1) && isSimpleReturn(returnStatement2)) {
                // third case
                // If
                // Expr
                // Statement
                // Block
                // BlockStatement
                // Statement
                // ReturnStatement
                // Statement
                // Block
                // BlockStatement
                // Statement
                // ReturnStatement
                // i.e.,
                // if (foo) {
                // return true;
                // } else {
                // return false;
                // }
                addViolation(data, node);
            } else {
                Node expression1 = getDescendant(returnStatement1, 4);
                Node expression2 = getDescendant(returnStatement2, 4);
                if (terminatesInBooleanLiteral(node.getChild(1).getChild(0))
                        && terminatesInBooleanLiteral(node.getChild(2).getChild(0))) {
                    addViolation(data, node);
                } else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus
                        ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
                    // We get the nodes under the '!' operator
                    // If they are the same => error
                    if (isNodesEqualWithUnaryExpression(expression1, expression2)) {
                        // forth case
                        // If
                        // Expr
                        // Statement
                        // Block
                        // BlockStatement
                        // Statement
                        // ReturnStatement
                        // UnaryExpressionNotPlusMinus '!'
                        // Expression E
                        // Statement
                        // Block
                        // BlockStatement
                        // Statement
                        // ReturnStatement
                        // Expression E
                        // i.e.,
                        // if (foo) {
                        // return !a;
                        // } else {
                        // return a;
                        // }
                        addViolation(data, node);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    /**
     * Checks, whether there is a statement after the given if statement, and if
     * so, whether this is just a return boolean statement.
     *
     * @param node
     *            the if statement
     * @return
     */
    private boolean isJustReturnsBooleanAfter(ASTIfStatement ifNode) {
        Node blockStatement = ifNode.getParent().getParent();
        Node block = blockStatement.getParent();
        if (block.getNumChildren() != blockStatement.getIndexInParent() + 1 + 1) {
            return false;
        }

        Node nextBlockStatement = block.getChild(blockStatement.getIndexInParent() + 1);
        return terminatesInBooleanLiteral(nextBlockStatement);
    }

    /**
     * Checks whether the given ifstatement just returns a boolean in the if
     * clause.
     *
     * @param node
     *            the if statement
     * @return
     */
    private boolean isIfJustReturnsBoolean(ASTIfStatement ifNode) {
        Node node = ifNode.getChild(1);
        return node.getNumChildren() == 1
                && (hasOneBlockStmt(node) || terminatesInBooleanLiteral(node.getChild(0)));
    }

    private boolean hasOneBlockStmt(Node node) {
        return node.getChild(0) instanceof ASTBlock && node.getChild(0).getNumChildren() == 1
                && terminatesInBooleanLiteral(node.getChild(0).getChild(0));
    }

    /**
     * Returns the first child node going down 'level' levels or null if level
     * is invalid
     */
    private Node getDescendant(Node node, int level) {
        Node n = node;
        for (int i = 0; i < level; i++) {
            if (n.getNumChildren() == 0) {
                return null;
            }
            n = n.getChild(0);
        }
        return n;
    }

    private boolean terminatesInBooleanLiteral(Node node) {
        return eachNodeHasOneChild(node) && getLastChild(node) instanceof ASTBooleanLiteral;
    }

    private boolean eachNodeHasOneChild(Node node) {
        if (node.getNumChildren() > 1) {
            return false;
        }
        if (node.getNumChildren() == 0) {
            return true;
        }
        return eachNodeHasOneChild(node.getChild(0));
    }

    private Node getLastChild(Node node) {
        if (node.getNumChildren() == 0) {
            return node;
        }
        return getLastChild(node.getChild(0));
    }

    private boolean isNodesEqualWithUnaryExpression(Node n1, Node n2) {
        Node node1;
        Node node2;
        if (n1 instanceof ASTUnaryExpressionNotPlusMinus) {
            node1 = n1.getChild(0);
        } else {
            node1 = n1;
        }
        if (n2 instanceof ASTUnaryExpressionNotPlusMinus) {
            node2 = n2.getChild(0);
        } else {
            node2 = n2;
        }
        return isNodesEquals(node1, node2);
    }

    private boolean isNodesEquals(Node n1, Node n2) {
        int numberChild1 = n1.getNumChildren();
        int numberChild2 = n2.getNumChildren();
        if (numberChild1 != numberChild2) {
            return false;
        }
        if (!n1.getClass().equals(n2.getClass())) {
            return false;
        }
        if (!n1.toString().equals(n2.toString())) {
            return false;
        }
        for (int i = 0; i < numberChild1; i++) {
            if (!isNodesEquals(n1.getChild(i), n2.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isSimpleReturn(Node node) {
        return node instanceof ASTReturnStatement && node.getNumChildren() == 0;
    }

}
