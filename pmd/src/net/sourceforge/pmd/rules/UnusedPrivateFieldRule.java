/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnusedPrivateFieldRule extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map vars = node.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (!decl.getAccessNodeParent().isPrivate() || isOK(decl.getImage())) {
                continue;
            }
            if (!actuallyUsed((List) vars.get(decl))) {
                ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getNode(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
            }
        }
        return super.visit(node, data);
    }

    private boolean actuallyUsed(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence nameOccurrence = (NameOccurrence) j.next();
            if (!nameOccurrence.isOnLeftHandSide()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOK(String image) {
        return image.equals("serialVersionUID") || image.equals("serialPersistentFields") || image.equals("IDENT");
    }
}
