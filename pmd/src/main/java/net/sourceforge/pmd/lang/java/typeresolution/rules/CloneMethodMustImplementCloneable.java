/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.rules;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * The method clone() should only be implemented if the class implements the
 * Cloneable interface with the exception of a final method that only throws
 * CloneNotSupportedException. This version uses PMD's type resolution
 * facilities, and can detect if the class implements or extends a Cloneable
 * class
 *
 * @author acaplan
 */
public class CloneMethodMustImplementCloneable extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
	ASTImplementsList impl = node.getFirstChildOfType(ASTImplementsList.class);
	if (impl != null && impl.jjtGetParent().equals(node)) {
	    for (int ix = 0; ix < impl.jjtGetNumChildren(); ix++) {
		ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) impl.jjtGetChild(ix);
		if (type.getType() == null) {
		    if ("Cloneable".equals(type.getImage())) {
			return data;
		    }
		} else if (type.getType().equals(Cloneable.class)) {
		    return data;
		} else {
		    List<Class<?>> implementors = Arrays.asList(type.getType().getInterfaces());
		    if (implementors.contains(Cloneable.class)) {
			return data;
		    }
		}
	    }
	}
	if (node.jjtGetNumChildren() != 0 && node.jjtGetChild(0) instanceof ASTExtendsList) {
	    ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.jjtGetChild(0).jjtGetChild(0);
	    Class<?> clazz = type.getType();
	    if (clazz != null && clazz.equals(Cloneable.class)) {
		return data;
	    }
	    while (clazz != null && !Object.class.equals(clazz)) {
		if (Arrays.asList(clazz.getInterfaces()).contains(Cloneable.class)) {
		    return data;
		}
		clazz = clazz.getSuperclass();
	    }
	}

	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
	ASTClassOrInterfaceDeclaration classOrInterface = node
		.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
	if (classOrInterface != null && //Don't analyze enums, which cannot subclass clone()
		(node.isFinal() || classOrInterface.isFinal())) {
	    if (node.findDescendantsOfType(ASTBlock.class).size() == 1) {
		List<ASTBlockStatement> blocks = node.findDescendantsOfType(ASTBlockStatement.class);
		if (blocks.size() == 1) {
		    ASTBlockStatement block = blocks.get(0);
		    ASTClassOrInterfaceType type = block.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
		    if (type != null && type.getType() != null && type.getNthParent(9).equals(node)
			    && type.getType().equals(CloneNotSupportedException.class)) {
			return data;
		    } else if (type != null && type.getType() == null
			    && "CloneNotSupportedException".equals(type.getImage())) {
			return data;
		    }
		}
	    }
	}
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
	if (!"clone".equals(node.getImage())) {
	    return data;
	}
	int countParams = ((ASTFormalParameters) node.jjtGetChild(0)).jjtGetNumChildren();
	if (countParams != 0) {
	    return data;
	}
	addViolation(data, node);
	return data;
    }
}