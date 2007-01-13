/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;

public class StringToStringRule extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!node.getNameDeclaration().getTypeImage().equals("String")) {
            return data;
        }
        boolean isArray = node.isArray();
        for (Iterator i = node.getUsages().iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            if (occ.getNameForWhichThisIsAQualifier() != null) {
                if (!isArray && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("toString") != -1) {
                    addViolation(data, occ.getLocation());
                } else if (isArray && occ.getNameForWhichThisIsAQualifier().getLocation() != null && !ASTName.class.equals(occ.getNameForWhichThisIsAQualifier().getLocation().getClass())) {
                    addViolation(data, occ.getLocation());
                }
            }
        }
        return data;
    }
}
