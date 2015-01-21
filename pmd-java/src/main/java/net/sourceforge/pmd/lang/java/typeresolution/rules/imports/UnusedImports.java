/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.rules.imports;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.imports.UnusedImportsRule;
import net.sourceforge.pmd.lang.rule.ImportWrapper;

public class UnusedImports extends UnusedImportsRule {

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
	if (node.isImportOnDemand()) {
	    ASTName importedType = (ASTName) node.jjtGetChild(0);
	    imports.add(new ImportWrapper(importedType.getImage(), null, node, node.getType(), node.isStatic()));
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
	Iterator<ImportWrapper> it = imports.iterator();
	while (it.hasNext()) {
	    ImportWrapper i = it.next();
	    if (i.matches(candidate)) {
	        it.remove();
	        return;
	    }
	}
	if (TypeNode.class.isAssignableFrom(node.getClass()) && ((TypeNode) node).getType() != null) {
	    Class<?> c = ((TypeNode) node).getType();
	    if (c.getPackage() != null) {
		candidate = new ImportWrapper(c.getPackage().getName(), null);
		if (imports.contains(candidate)) {
		    imports.remove(candidate);
		}
	    }
	}
    }
}
