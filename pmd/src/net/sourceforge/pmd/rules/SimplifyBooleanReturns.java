/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.ast.SimpleNode;

public class SimplifyBooleanReturns extends AbstractRule {

    public Object visit(ASTIfStatement node, Object data) {
        // only deal with if..then..else stmts
        if (node.jjtGetNumChildren() != 3) {
            return super.visit(node, data);
        }

        // don't bother if either the if or the else block is empty
        if (node.jjtGetChild(1).jjtGetNumChildren() == 0 || node.jjtGetChild(2).jjtGetNumChildren() == 0) {
            return super.visit(node, data);
        }
 
        // first case:
        // If
        //  Expr
        //  Statement
        //   ReturnStatement
        //  Statement
        //   ReturnStatement
        // i.e.,
        // if (foo)
        //  return true;
        // else
        //  return false;
        
        // second case:
        // If
        //  Expr
        //  Statement
        //   ReturnStatement
        //     UnaryExpressionNotPlusMinus '!'
        //       Expression E
        //  Statement
        //   ReturnStatement
        //       Expression E
        // i.e.,
        // if (foo)
        //  return !a;
        // else
        //  return a;

        // third case
        // If
        // Expr
        // Statement
        //  Block
        //   BlockStatement
        //    Statement
        //     ReturnStatement
        // Statement
        //  Block
        //   BlockStatement
        //    Statement
        //     ReturnStatement
        // i.e.,
        // if (foo) {
        //  return true;
        // } else {
        //  return false;
        // }

        // forth case
        // If
        // Expr
        // Statement
        //  Block
        //   BlockStatement
        //    Statement
        //     ReturnStatement
        //       UnaryExpressionNotPlusMinus '!'
        //         Expression E
        // Statement
        //  Block
        //   BlockStatement
        //    Statement
        //     ReturnStatement
        //      Expression E
        // i.e.,
        // if (foo) {
        //  return !a;
        // } else {
        //  return a;
        // }

        if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTReturnStatement && node.jjtGetChild(2).jjtGetChild(0) instanceof ASTReturnStatement) {
          SimpleNode returnStatement1 = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);
          SimpleNode returnStatement2 = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0);
          SimpleNode expression1 = (SimpleNode)returnStatement1.jjtGetChild(0).jjtGetChild(0);
          SimpleNode expression2 = (SimpleNode)returnStatement2.jjtGetChild(0).jjtGetChild(0);
          if(terminatesInBooleanLiteral(returnStatement1) && terminatesInBooleanLiteral(returnStatement2)) {
            addViolation(data, node);          
          } else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
            //We get the nodes under the '!' operator
            //If they are the same => error
            if(isNodesEqualWithUnaryExpression(expression1, expression2)) {
              addViolation(data, node);
            }
          }
        } else if (hasOneBlockStmt((SimpleNode) node.jjtGetChild(1)) && hasOneBlockStmt((SimpleNode) node.jjtGetChild(2))) {
          SimpleNode expression1 = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
          SimpleNode expression2 = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
          if(terminatesInBooleanLiteral((SimpleNode) node.jjtGetChild(1).jjtGetChild(0)) && terminatesInBooleanLiteral((SimpleNode) node.jjtGetChild(2).jjtGetChild(0))) {
            addViolation(data, node);
          } else if (expression1 instanceof ASTUnaryExpressionNotPlusMinus ^ expression2 instanceof ASTUnaryExpressionNotPlusMinus) {
            //We get the nodes under the '!' operator
            //If they are the same => error            
            if(isNodesEqualWithUnaryExpression(expression1, expression2)) {
              addViolation(data, node);
            }
          }
        }
        return super.visit(node, data);
    }

    private boolean hasOneBlockStmt(SimpleNode node) {
        return node.jjtGetChild(0) instanceof ASTBlock && node.jjtGetChild(0).jjtGetNumChildren() == 1 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTBlockStatement && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTStatement && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTReturnStatement;
    }

    private boolean terminatesInBooleanLiteral(SimpleNode node) {

        return eachNodeHasOneChild(node) && (getLastChild(node) instanceof ASTBooleanLiteral);
    }

    private boolean eachNodeHasOneChild(SimpleNode node) {
        if (node.jjtGetNumChildren() > 1) {
            return false;
        }
        if (node.jjtGetNumChildren() == 0) {
            return true;
        }
        return eachNodeHasOneChild((SimpleNode) node.jjtGetChild(0));
    }

    private SimpleNode getLastChild(SimpleNode node) {
        if (node.jjtGetNumChildren() == 0) {
            return node;
        }
        return getLastChild((SimpleNode) node.jjtGetChild(0));
    }

    private boolean isNodesEqualWithUnaryExpression(SimpleNode n1, SimpleNode n2) {
      SimpleNode node1, node2;
      if(n1 instanceof ASTUnaryExpressionNotPlusMinus) {
        node1 = (SimpleNode)n1.jjtGetChild(0);
      } else {
        node1 = n1;
      }
      if(n2 instanceof ASTUnaryExpressionNotPlusMinus) {
        node2 = (SimpleNode)n2.jjtGetChild(0);
      } else {
        node2 = n2;
      }
      return isNodesEquals(node1, node2);
    }

    private boolean isNodesEquals(SimpleNode n1, SimpleNode n2) {
        int numberChild1 = n1.jjtGetNumChildren();
        int numberChild2 = n2.jjtGetNumChildren();
        if(numberChild1 != numberChild2)
          return false;
        if(!n1.getClass().equals(n2.getClass()))
          return false;
        if(!n1.toString().equals(n2.toString())) 
          return false;
        for(int i = 0 ; i < numberChild1 ; i++) {
          if( !isNodesEquals( (SimpleNode)n1.jjtGetChild(i), (SimpleNode)n2.jjtGetChild(i) ) )
            return false;          
        }
        return true;
    }

}
