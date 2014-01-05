/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DontImportJavaLangRule extends AbstractJavaRule {

    private static final Package JAVA_LANG_PACKAGE = Package.getPackage("java.lang");

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {

	if (node.isStatic()) {
	    return data;
	}

	if (node.getPackage() != null) {
	    if (node.getPackage().equals(JAVA_LANG_PACKAGE)) {
		addViolation(data, node);
	    }
	} else {
	    String img = node.jjtGetChild(0).getImage();
	    if (img.startsWith("java.lang")) {
		if (img.startsWith("java.lang.ref") || img.startsWith("java.lang.reflect")
			|| img.startsWith("java.lang.annotation") || img.startsWith("java.lang.instrument")
			|| img.startsWith("java.lang.management") || img.startsWith("java.lang.Thread.")
			|| img.startsWith("java.lang.ProcessBuilder.")) {
		    return data;
		}
		addViolation(data, node);
	    }
	}
	return data;
    }
}
