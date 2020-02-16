/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DontImportJavaLangRule extends AbstractJavaRule {
    private static final String IMPORT_JAVA_LANG = "java.lang";

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {

        if (node.isStatic()) {
            return data;
        }

        String img = node.getChild(0).getImage();
        if (img.startsWith(IMPORT_JAVA_LANG)) {
            if (!IMPORT_JAVA_LANG.equals(img)) {
                if (img.indexOf('.', IMPORT_JAVA_LANG.length() + 1) != -1 || node.isImportOnDemand()) {
                    // Importing from a subpackage / inner class
                    return data;
                }
            }
            addViolation(data, node);
        }
        return data;
    }
}
