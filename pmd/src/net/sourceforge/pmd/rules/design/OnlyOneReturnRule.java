/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnlyOneReturnRule extends AbstractRule {

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isAbstract()) {
            return data;
        }

        List returnNodes = new ArrayList();
        node.findChildrenOfType(ASTReturnStatement.class, returnNodes, false);
        if (returnNodes.size() > 1) {
            RuleContext ctx = (RuleContext) data;
            for (Iterator i = returnNodes.iterator(); i.hasNext();) {
                SimpleNode problem = (SimpleNode) i.next();
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
