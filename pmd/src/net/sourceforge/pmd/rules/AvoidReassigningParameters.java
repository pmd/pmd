/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AvoidReassigningParameters extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        Map params = node.getScope().getVariableDeclarations();
        for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
 
            VariableNameDeclaration decl = (VariableNameDeclaration) entry.getKey();
            List usages = (List) entry.getValue();
            for (Iterator j = usages.iterator(); j.hasNext();) {
                NameOccurrence occ = (NameOccurrence) j.next();
                if ((occ.isOnLeftHandSide() || occ.isSelfAssignment()) && occ.getNameForWhichThisIsAQualifier() == null && !decl.isArray()) {
                    addViolation(data, decl.getNode(), decl.getImage());
                }
            }
        }
        return super.visit(node, data);
    }
}
