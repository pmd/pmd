package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.ast.*;

public class MethodNamingConventionsRule extends AbstractRule {

  public Object visit(ASTMethodDeclarator node, Object data) {
    if (Character.isUpperCase(node.getImage().charAt(0))) {
      RuleContext ctx = (RuleContext)data;
      ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), getMessage()));
    }

    if (node.getImage().indexOf("_") >= 0) {
      String msg = "Method names should not contain underscores";
      RuleContext ctx = (RuleContext)data;
      ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), msg));

    }
    return data;
  }

}
