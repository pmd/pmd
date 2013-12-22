/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.util.StringUtil;

public class ImportFromSamePackageRule extends AbstractJavaRule {

    public Object visit(ASTImportDeclaration importDecl, Object data) {
        String packageName = importDecl.getScope().getEnclosingScope(SourceFileScope.class).getPackageName();

        if (packageName != null && packageName.equals(importDecl.getPackageName())) {
            addViolation(data, importDecl);
        }

        // special case
        if (packageName == null && StringUtil.isEmpty(importDecl.getPackageName())) {
            addViolation(data, importDecl);
        }
        return data;
    }
}
