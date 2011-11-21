/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.rules.imports;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.ImportWrapper;
import net.sourceforge.pmd.lang.java.rule.imports.UnusedImportsRule;

public class UnusedImports extends UnusedImportsRule {

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
	if (node.isImportOnDemand()) {
	    ASTName importedType = (ASTName) node.jjtGetChild(0);
	    imports.add(new ImportWrapper(importedType.getImage(), null, node));
	} else {
	    super.visit(node, data);
	}
	return data;
    }

    @Override
    protected void check(Node node) {
	if (imports.isEmpty()) {
	    return;
	}
	ImportWrapper candidate = getImportWrapper(node);
	if (imports.contains(candidate)) {
	    imports.remove(candidate);
	    return;
	}
	if (TypeNode.class.isAssignableFrom(node.getClass()) && ((TypeNode) node).getType() != null) {
	    Class<?> c = ((TypeNode) node).getType();
	    if (c.getPackage() != null) {
		candidate = new ImportWrapper(c.getPackage().getName(), null, new DummyJavaNode(-1));
		if (imports.contains(candidate)) {
		    imports.remove(candidate);
		}
	    }
	}
    }
}
