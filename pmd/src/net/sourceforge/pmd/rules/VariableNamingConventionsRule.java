/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInterfaceMemberDeclaration;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.Node;

public class VariableNamingConventionsRule extends AbstractRule {

  public Object visit(ASTLocalVariableDeclaration node, Object data) {
    return checkNames(node, data);
  }

  public Object visit(ASTFieldDeclaration node, Object data) {
    return checkNames(node, data);
  }

  public Object checkNames(Node node, Object data) {

    boolean isFinal = false;
    if (node instanceof AccessNode) {
      isFinal = ((AccessNode)node).isFinal();
    }

    if (node.jjtGetParent() instanceof ASTInterfaceMemberDeclaration) {
        isFinal = true;
    }

    ASTType childNodeType = (ASTType)node.jjtGetChild(0);
    String varType = "";
    if (childNodeType.jjtGetChild(0)instanceof ASTName ) {
     varType = ((ASTName)childNodeType.jjtGetChild(0)).getImage();
    } else if (childNodeType.jjtGetChild(0) instanceof ASTPrimitiveType) {
    varType = ((ASTPrimitiveType)childNodeType.jjtGetChild(0)).getImage();
    }
      if (varType != null && varType.length() > 0) {
        //Get the variable name
        ASTVariableDeclarator childNodeName = (ASTVariableDeclarator)node.jjtGetChild(1);
        ASTVariableDeclaratorId childNodeId = (ASTVariableDeclaratorId)childNodeName.jjtGetChild(0);
        String varName = childNodeId.getImage();

          if (varName.equals("serialVersionUID")) {
              return data;
          }

        if (isFinal) {
          if (!varName.equals(varName.toUpperCase())) {
            String msg = "Variables that are final should be in all caps.";
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), msg));

          }
        } else {
          if (varName.indexOf("_") >= 0) {
            String msg = "Variables that are not final should not contain underscores.";
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), msg));
          }
          if (Character.isUpperCase(varName.charAt(0))) {
            String msg = "Variables should start with a lowercase character";
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), msg));
          }

        }
      }
    return data;
  }
}
