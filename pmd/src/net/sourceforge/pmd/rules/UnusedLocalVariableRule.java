/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnusedLocalVariableRule extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            Map locals = node.getScope().getVariableDeclarations();
            for (Iterator i = locals.keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
                if (!actuallyUsed((List)locals.get(decl))) {
                    ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
                }
            }
        }
        return data;
    }

    private boolean actuallyUsed(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence nameOccurrence = (NameOccurrence)j.next();
            if (!nameOccurrence.isOnLeftHandSide() || nameOccurrence.isPartOfQualifiedName()) {
                return true;
            }
        }
        return false;
    }
}
