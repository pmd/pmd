package net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.typeresolution.TypeHelper;

/**
 * Avoid instantiating Boolean objects; you can reference Boolean.TRUE,
 * Boolean.FALSE, or call Boolean.valueOf() instead.
 *
 * <pre>
 *  public class Foo {
 *       Boolean bar = new Boolean("true");    // just do a Boolean
 *       bar = Boolean.TRUE;                   //ok
 *       Boolean buz = Boolean.valueOf(false); // just do a Boolean buz = Boolean.FALSE;
 *  }
 * </pre>
 */
public class BooleanInstantiation extends AbstractRule {

	/*
	 *  see bug 1744065 : If somebody create it owns Boolean, the rule should not be triggered
	 *   Therefore, we use this boolean to flag if the source code contains such an import
	 *
	 */
	private boolean customBoolean;

    public Object visit(ASTCompilationUnit decl,Object data) {
        // customBoolean needs to be reset for each new file
        customBoolean = false;

        return super.visit(decl, data);
    }

	public Object visit(ASTImportDeclaration decl,Object data) {
		// If the import actually import a Boolean class that overrides java.lang.Boolean
		if ( decl.getImportedName().endsWith("Boolean") && ! decl.getImportedName().equals("java.lang"))
		{
			customBoolean = true;
		}
		return super.visit(decl, data);
	}

    public Object visit(ASTAllocationExpression node, Object data) {

    	if ( ! customBoolean ) {
	        if (node.findChildrenOfType(ASTArrayDimsAndInits.class).size() > 0) {
	            return super.visit(node, data);
	        }
	        if (TypeHelper.isA((ASTClassOrInterfaceType) node.jjtGetChild(0), Boolean.class)) {
                super.addViolation(data, node);
                return data;
            }
    	}
        return super.visit(node, data);
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {

    	if ( ! customBoolean )
    	{
	        if (node.jjtGetNumChildren() == 0 || !node.jjtGetChild(0).getClass().equals(ASTName.class)) {
	            return super.visit(node, data);
	        }

	        if ("Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())
	                || "java.lang.Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())) {
	            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.jjtGetParent();
	            ASTPrimarySuffix suffix = parent.getFirstChildOfType(ASTPrimarySuffix.class);
	            if (suffix == null) {
	                return super.visit(node, data);
	            }
	            ASTPrimaryPrefix prefix = suffix.getFirstChildOfType(ASTPrimaryPrefix.class);
	            if (prefix == null) {
	                return super.visit(node, data);
	            }

	            if (prefix.getFirstChildOfType(ASTBooleanLiteral.class) != null) {
	                super.addViolation(data, node);
	                return data;
	            }
	            ASTLiteral literal = prefix.getFirstChildOfType(ASTLiteral.class);
	            if (literal != null && ("\"true\"".equals(literal.getImage()) || "\"false\"".equals(literal.getImage()))) {
	                super.addViolation(data, node);
	                return data;
	            }
	        }
    	}
        return super.visit(node, data);
    }
}
