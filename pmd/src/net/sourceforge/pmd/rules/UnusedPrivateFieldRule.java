/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class UnusedPrivateFieldRule extends AbstractRule {

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        check(node, (RuleContext) data, true);
        check(node, (RuleContext) data, false);
        return super.visit(node, data);
    }

    private void check(ASTUnmodifiedClassDeclaration node, RuleContext ctx, boolean flag) {
        Map unused = node.getScope().getVariableDeclarations(flag);
        for (Iterator i = unused.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (!decl.getAccessNodeParent().isPrivate() || isOK(decl.getImage())) {
                continue;
            }
            if (!actuallyUsed((List)unused.get(decl))) {
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
            }
        }
    }

    private boolean actuallyUsed(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            if (!((NameOccurrence)j.next()).isOnLeftHandSide()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOK(String image) {
        return image.equals("serialVersionUID") || image.equals("serialPersistentFields") || image.equals("IDENT");
    }
}
