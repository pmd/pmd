/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnusedLocalVariableRule extends AbstractRule {

    private Set visited = new HashSet();

    public Object visit(ASTCompilationUnit acu, Object data) {
        visited.clear();
        return super.visit(acu, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            Scope scope = node.getScope();
            if (visited.contains(scope)) {
                return data;
            } else {
                visited.add(scope);
            }
            Map locals = scope.getVariableDeclarations();
            for (Iterator i = locals.keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
                // TODO this misses some cases
                // need to add DFAish code to determine if an array
                // is initialized locally or gotten from somewhere else
                if (decl.isArray()) {
                    continue;
                }
                List usages = (List) locals.get(decl);
                if (!actuallyUsed(usages)) {
                    RuleContext ctx = ((RuleContext) data);
                    RuleViolation ruleViolation = createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()}));
                    ctx.getReport().addRuleViolation(ruleViolation);
                }
            }
        }
        return data;
    }

    private boolean actuallyUsed(List usages) {
        boolean used = false;
        for (Iterator j = usages.iterator(); j.hasNext() && !used;) {
            NameOccurrence occ = (NameOccurrence) j.next();
            used = !occ.isOnLeftHandSide();
        }
        return used;
    }
}
