/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

public class ImportFromSamePackageRule extends AbstractJavaRule {

    public Object visit(ASTImportDeclaration importDecl, Object data) {
        String packageName = importDecl.getScope().getEnclosingSourceFileScope().getPackageName();

        if (packageName != null && packageName.equals(importDecl.getPackageName())) {
            addViolation(data, importDecl);
        }

        // special case
        if (packageName == null && importDecl.getPackageName().equals("")) {
            addViolation(data, importDecl);
        }
        return data;
    }
}
