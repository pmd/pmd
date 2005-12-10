/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.Iterator;
import java.util.List;

public class StringToStringRule extends AbstractRule {

    public Object visit(ASTName node, Object data) {
        if (!(node.getNameDeclaration() instanceof VariableNameDeclaration)) {
            return super.visit(node, data);
        }

        VariableNameDeclaration vnd = (VariableNameDeclaration)node.getNameDeclaration();
        if (!vnd.getTypeImage().equals("String")) {
            return super.visit(node, data);
        }

        List usages = (List)vnd.getDeclaratorId().getScope().getVariableDeclarations().get(vnd);
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence) j.next();
            if (occ.getNameForWhichThisIsAQualifier() != null && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("toString") != -1) {
                addViolation(data, occ.getLocation());
            }
        }
        return super.visit(node, data);
    }
}
