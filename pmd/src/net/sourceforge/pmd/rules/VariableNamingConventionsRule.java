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

public class VariableNamingConventionsRule extends AbstractRule {

  public Object visit(ASTLocalVariableDeclaration node, Object data) {
    return checkNames(node, data);
  }

  public Object visit(ASTFieldDeclaration node, Object data) {
    return checkNames(node, data);
  }

    public Object checkNames(AccessNode node, Object data) {
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

            // non static final class fields are OK
            if (node.isFinal() && !node.isStatic() && !(node.jjtGetParent() instanceof ASTInterfaceMemberDeclaration)) {
                return data;
            }

            // final, non static, class, fields are OK
            if (node.isFinal() && !node.isStatic() && !(node.jjtGetParent() instanceof ASTInterfaceMemberDeclaration)) {
                return data;
            }

            // static finals (and interface fields, which are implicitly static and final) are checked for uppercase
            if ((node.isStatic() && node.isFinal()) || node.jjtGetParent() instanceof ASTInterfaceMemberDeclaration) {
                if (!varName.equals(varName.toUpperCase())) {
                    RuleContext ctx = (RuleContext)data;
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), "Variables that are final and static should be in all caps."));
                }
                return data;
            }

            // if
            if (varName.indexOf("_") >= 0) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), "Variables that are not final should not contain underscores."));
            }
            if (Character.isUpperCase(varName.charAt(0))) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, childNodeName.getBeginLine(), "Variables should start with a lowercase character"));
            }
        }
        return data;
    }
}
