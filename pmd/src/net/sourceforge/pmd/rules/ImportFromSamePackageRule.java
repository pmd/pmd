/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;

public class ImportFromSamePackageRule extends AbstractRule {

    private String packageName;

    public Object visit(ASTCompilationUnit node, Object data) {
        packageName = null;
        return super.visit(node, data);
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = ((ASTName) node.jjtGetChild(0)).getImage();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName nameNode = node.getImportedNameNode();
        RuleContext ctx = (RuleContext) data;
        if (packageName != null && !node.isImportOnDemand() && packageName.equals(getPackageName(nameNode.getImage()))) {
            addViolation(ctx, node);
        }

        // special case
        if (packageName == null && getPackageName(nameNode.getImage()).equals("")) {
            addViolation(ctx, node);
        }
        return data;
    }

    private void addViolation(RuleContext ctx, ASTImportDeclaration node) {
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
    }

    private String getPackageName(String importName) {
        if (importName.indexOf('.') == -1) {
            return "";
        }
        int lastDot = importName.lastIndexOf('.');
        return importName.substring(0, lastDot);
    }

}
