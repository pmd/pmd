/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class UnusedLocalVariableRule extends AbstractRule {
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            Map unused = node.getScope().getVariableDeclarations();
            for (Iterator i = unused.keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
                if (!actuallyUsed((List)unused.get(decl))) {
                    ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
                }
            }
        }
        return data;
    }

    private boolean actuallyUsed(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            if (!((NameOccurrence)j.next()).isOnLeftHandSide()) {
                return true;
            }
        }
        return false;
    }
}
