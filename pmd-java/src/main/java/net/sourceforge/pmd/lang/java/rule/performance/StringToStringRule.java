/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public class StringToStringRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.getNameDeclaration() == null
            || !TypeHelper.isExactlyAny(node.getNameDeclaration(), String.class)
                && !TypeHelper.isExactlyAny(node.getNameDeclaration(), String[].class)) {
            return data;
        }
        boolean isArray = node.isArray();
        for (NameOccurrence occ : node.getUsages()) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            NameOccurrence qualifier = jocc.getNameForWhichThisIsAQualifier();
            if (qualifier != null) {
                if (!isArray && isNotAMethodReference(qualifier) && qualifier.getImage().indexOf("toString") != -1) {
                    addViolation(data, jocc.getLocation());
                } else if (isArray && isNotAName(qualifier) && qualifier.getImage().equals("toString")) {
                    addViolation(data, jocc.getLocation());
                }
            }
        }
        return data;
    }

    private boolean isNotAMethodReference(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTMethodReference.class);
    }

    private boolean isNotAName(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTName.class);
    }

    private boolean isNotA(NameOccurrence qualifier, Class<? extends AbstractJavaNode> type) {
        ScopedNode location = qualifier.getLocation();
        return location == null || !(type.isAssignableFrom(location.getClass()));
    }
}
