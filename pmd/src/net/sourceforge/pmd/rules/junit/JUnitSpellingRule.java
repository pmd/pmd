/*
 * User: tom
 * Date: Sep 10, 2002
 * Time: 1:06:34 PM
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

import java.text.MessageFormat;

public class JUnitSpellingRule extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (node.getParameterCount() == 0) {
            checkSpelling(node.getImage(), data, node, "setUp");
            checkSpelling(node.getImage(), data, node, "tearDown");
        }
        return data;
    }

    private void checkSpelling(String name, Object data, ASTMethodDeclarator node, String correctSpelling) {
        if (name.toLowerCase().equals(correctSpelling.toLowerCase())) {
            if (!name.equals(correctSpelling)) {
                RuleContext ctx = (RuleContext)data;
                String msg = MessageFormat.format(getMessage(), new Object[] {correctSpelling, name});
                ctx.getReport().addRuleViolation(super.createRuleViolation(ctx, node.getBeginLine(), msg));
            }
        }
    }
}
