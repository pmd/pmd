package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

import java.text.MessageFormat;

public class LongVariableRule extends AbstractRule
{
    public Object visit(ASTVariableDeclaratorId decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        String image = decl.getImage();

        if (image.length() > getIntProperty("minimumLength")) {
            String msg = MessageFormat.format(getMessage(), new Object[] {image});
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getBeginLine(), msg));
        }

        return data;
    }
}
