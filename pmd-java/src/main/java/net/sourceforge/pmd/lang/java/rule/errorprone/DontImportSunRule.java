/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DontImportSunRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        String img = node.getChild(0).getImage();
        if (img.startsWith("sun.") && !img.startsWith("sun.misc.Signal")) {
            addViolation(data, node);
        }
        return data;
    }

}
