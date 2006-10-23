package net.sourceforge.pmd.rules;

import java.util.Stack;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;

public class MoreThanOneLogger extends AbstractRule {

	private Stack stack = new Stack();

	private Integer count;

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		return init (node, data);
	}	

	public Object visit(ASTEnumDeclaration node, Object data) {
		return init (node, data);
	}	

	private Object init(SimpleJavaNode node, Object data) {
		stack.push(count);
		count = new Integer(0);

		node.childrenAccept(this, data);

		if (count.intValue() > 1) {
			addViolation(data, node);
		}
		count = (Integer) stack.pop();

		return data;
	}

	public Object visit(ASTVariableDeclarator node, Object data) {
		if (count.intValue() > 1) {
			return super.visit(node, data);
		}
		SimpleNode type = (SimpleNode) ((SimpleNode) node.jjtGetParent()).getFirstChildOfType(ASTType.class);
		if (type != null) {
			SimpleNode reftypeNode = (SimpleNode) type.jjtGetChild(0);
			if (reftypeNode instanceof ASTReferenceType) {
                SimpleNode classOrIntType = (SimpleNode) reftypeNode.jjtGetChild(0);
                if (classOrIntType instanceof ASTClassOrInterfaceType && "Logger".equals(classOrIntType.getImage())) {
                	count = new Integer(count.intValue()+1);
                }
			}
		}

		return super.visit(node, data);
	}

}
