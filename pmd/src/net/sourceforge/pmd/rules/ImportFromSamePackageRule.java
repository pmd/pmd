/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;

public class ImportFromSamePackageRule extends AbstractRule {

    public Object visit(ASTImportDeclaration importDecl, Object data) {
        String packageName = importDecl.getScope().getEnclosingSourceFileScope().getPackageName();
        String importPkgName = importDecl.getPackageName();

        if (packageName != null && packageName.equals(importPkgName)) {
            addViolation(data, importDecl);
        }

        // special case
        if (packageName == null && importPkgName.equals("")) {
            addViolation(data, importDecl);
        }
        return data;
    }
}
