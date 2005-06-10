/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AvoidReassigningParameters extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        Scope scope = node.getScope();
        Map params = scope.getVariableDeclarations();
        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            List usages = (List) params.get(decl);
            for (Iterator j = usages.iterator(); j.hasNext();) {
                NameOccurrence occ = (NameOccurrence) j.next();
                if ((occ.isOnLeftHandSide() || occ.isSelfAssignment()) && occ.getNameForWhichThisIsAQualifier() == null && !decl.isArray()) {
                    ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getNode(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
                }
            }
        }
        return super.visit(node, data);
    }
}
