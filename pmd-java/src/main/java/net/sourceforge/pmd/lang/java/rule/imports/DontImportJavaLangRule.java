/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DontImportJavaLangRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {

        if (node.isStatic()) {
            return data;
        }

        String img = node.jjtGetChild(0).getImage();
        if (img.startsWith("java.lang")) {
            if (img.startsWith("java.lang.ref") || img.startsWith("java.lang.reflect")
                    || img.startsWith("java.lang.annotation") || img.startsWith("java.lang.instrument")
                    || img.startsWith("java.lang.management") || img.startsWith("java.lang.Thread.")
                    || img.startsWith("java.lang.ProcessBuilder.") || img.startsWith("java.lang.invoke.")) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
