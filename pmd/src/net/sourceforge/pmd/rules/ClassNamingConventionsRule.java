/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;

public class ClassNamingConventionsRule extends AbstractRule {

  public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {

    if (Character.isLowerCase(node.getImage().charAt(0))) {
      RuleContext ctx = (RuleContext)data;
      ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), getMessage()));
    }

    if (node.getImage().indexOf("_") >= 0) {
        RuleContext ctx = (RuleContext)data;
      ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), "Class names should not contain underscores"));

    }

    return data;
  }
}
