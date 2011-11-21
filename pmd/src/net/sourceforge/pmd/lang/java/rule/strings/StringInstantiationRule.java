package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

public class StringInstantiationRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
	if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
	    return data;
	}

	if (!TypeHelper.isA((ASTClassOrInterfaceType) node.jjtGetChild(0), String.class)) {
	    return data;
	}

	List<ASTExpression> exp = node.findDescendantsOfType(ASTExpression.class);
	if (exp.size() >= 2) {
	    return data;
	}

	if (node.hasDecendantOfAnyType(ASTArrayDimsAndInits.class, ASTAdditiveExpression.class)) {
	    return data;
	}

	ASTName name = node.getFirstDescendantOfType(ASTName.class);
	// Literal, i.e., new String("foo")
	if (name == null) {
	    addViolation(data, node);
	    return data;
	}

	NameDeclaration nd = name.getNameDeclaration();
	if (!(nd instanceof VariableNameDeclaration)) {
	    return data;
	}

	VariableNameDeclaration vnd = (VariableNameDeclaration) nd;
	if (TypeHelper.isA(vnd, String.class)) {
	    addViolation(data, node);
	}
	return data;
    }
}
