/*
 * User: tom
 * Date: Sep 26, 2002
 * Time: 10:24:35 AM
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class OnlyOneReturnRule extends AbstractRule {

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isAbstract()) {
            return data;
        }

        List returnNodes = new ArrayList();
        node.findChildrenOfType(ASTReturnStatement.class, returnNodes);
        if (returnNodes.size() > 1) {
            RuleContext ctx = (RuleContext)data;
            for (Iterator i = returnNodes.iterator(); i.hasNext();) {
                SimpleNode problem = (SimpleNode)i.next();
                // skip the last one, it's OK
                if (!i.hasNext()) {
                    continue;
                }
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, problem.getBeginLine()));
            }
        }
        return data;
    }

}
