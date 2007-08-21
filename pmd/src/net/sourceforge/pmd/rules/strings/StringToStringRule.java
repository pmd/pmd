/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.typeresolution.TypeHelper;

public class StringToStringRule extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node.getNameDeclaration(), String.class)) {
            return data;
        }
        boolean isArray = node.isArray();
        for (NameOccurrence occ: node.getUsages()) {
            NameOccurrence qualifier = occ.getNameForWhichThisIsAQualifier();
            if (qualifier != null) {
                if (!isArray && qualifier.getImage().indexOf("toString") != -1) {
                    addViolation(data, occ.getLocation());
                } else if (isArray && qualifier.getLocation() != null && !ASTName.class.equals(qualifier.getLocation().getClass()) && qualifier.getImage().equals("toString")) {
                    addViolation(data, occ.getLocation());
                }
            }
        }
        return data;
    }
}
