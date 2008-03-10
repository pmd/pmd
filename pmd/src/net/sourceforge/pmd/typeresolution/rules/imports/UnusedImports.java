/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution.rules.imports;

import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.TypeNode;
import net.sourceforge.pmd.rules.ImportWrapper;
import net.sourceforge.pmd.rules.imports.UnusedImportsRule;

public class UnusedImports extends UnusedImportsRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.isImportOnDemand()) {
            ASTName importedType = (ASTName) node.jjtGetChild(0);
            imports.add(new ImportWrapper(importedType.getImage(), null, node));
        } else {
            super.visit(node, data);
        }
        return data;
    }

    protected void check(SimpleNode node) {
        if (imports.isEmpty()) {
            return;
        }
        ImportWrapper candidate = getImportWrapper(node);
        if (imports.contains(candidate)) {
            imports.remove(candidate);
            return;
        }
        if (TypeNode.class.isAssignableFrom(node.getClass()) && ((TypeNode) node).getType() != null) {
            Class c = ((TypeNode) node).getType();
            if (c.getPackage() != null) {
	            candidate = new ImportWrapper(c.getPackage().getName(), null, new SimpleJavaNode(-1));
	            if (imports.contains(candidate)) {
	                imports.remove(candidate);
	            }
            }
        }
    }
}
