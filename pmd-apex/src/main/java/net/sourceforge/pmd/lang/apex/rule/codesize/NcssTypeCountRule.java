/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;

import net.sourceforge.pmd.lang.apex.ast.ASTConstructorPreambleStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for type declarations.
 * 
 * @author Jason Bennett
 */
public class NcssTypeCountRule extends AbstractNcssCountRule {

	/**
	 * Count type declarations. This includes classes as well as enums and
	 * annotations.
	 */
	public NcssTypeCountRule() {
		super(ASTVariableDeclaration.class);
		setProperty(MINIMUM_DESCRIPTOR, 1500d);
	}

	@Override
	public Object visit(ASTUserClass node, Object data) {
		/*
		 * if (!node.isNested()) { return super.visit(node, data); }
		 */
		return countNodeChildren(node, data);
	}

	@Override
	public Object visit(ASTUserInterface node, Object data) {
		/*
		 * if (!node.getNode.isNested()) { return super.visit(node, data); }
		 */
		return countNodeChildren(node, data);
	}

	@Override
	public Object visit(ASTConstructorPreambleStatement node, Object data) {
		return countNodeChildren(node, data);
	}

	@Override
	public Object visit(ASTUserEnum node, Object data) {
		/*
		 * If the enum is a type in and of itself, don't count its declaration
		 * twice.
		 */
		if (node.jjtGetParent() instanceof ASTVariableDeclaration) {
			Integer nodeCount = countNodeChildren(node, data);
			int count = nodeCount.intValue() - 1;
			return Integer.valueOf(count);
		}
		return countNodeChildren(node, data);
	}

	@Override
	public Object visit(ASTMethod node, Object data) {
		return countNodeChildren(node, data);
	}

	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		return NumericConstants.ONE;
	}

	@Override
	public Object[] getViolationParameters(DataPoint point) {
		return new String[] { String.valueOf((int) point.getScore()) };
	}
}
