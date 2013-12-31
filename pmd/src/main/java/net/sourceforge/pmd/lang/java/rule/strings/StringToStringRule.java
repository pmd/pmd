/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class StringToStringRule extends AbstractJavaRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node.getNameDeclaration(), String.class)) {
            return data;
        }
        boolean isArray = node.isArray();
        for (NameOccurrence occ: node.getUsages()) {
            JavaNameOccurrence jocc = (JavaNameOccurrence)occ;
            NameOccurrence qualifier = jocc.getNameForWhichThisIsAQualifier();
            if (qualifier != null) {
                if (!isArray && qualifier.getImage().indexOf("toString") != -1) {
                    addViolation(data, jocc.getLocation());
                } else if (isArray && qualifier.getLocation() != null && !(qualifier.getLocation() instanceof ASTName) && qualifier.getImage().equals("toString")) {
                    addViolation(data, jocc.getLocation());
                }
            }
        }
        return data;
    }
}
