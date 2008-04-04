package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.NumericConstants;

public class MoreThanOneLoggerRule extends AbstractJavaRule {
    
    private static Class log4jLogger = null;

    private static Class javaLogger = null;

    static {
        try {
            log4jLogger = Class.forName("org.apache.log4j.Logger");
        } catch (Throwable t) {
            log4jLogger = null;
        }
        try {
            javaLogger = Class.forName("java.util.logging.Logger");
        } catch (Throwable t) {
            log4jLogger = null;
        }
    }

	private Stack<Integer> stack = new Stack<Integer>();

	private Integer count;

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		return init (node, data);
	}	

	public Object visit(ASTEnumDeclaration node, Object data) {
		return init (node, data);
	}	

	private Object init(JavaNode node, Object data) {
		stack.push(count);
		count = NumericConstants.ZERO;

		node.childrenAccept(this, data);

		if (count > 1) {
			addViolation(data, node);
		}
		count = stack.pop();

		return data;
	}

	public Object visit(ASTVariableDeclarator node, Object data) {
		if (count > 1) {
			return super.visit(node, data);
		}
		Node type = node.jjtGetParent().getFirstChildOfType(ASTType.class);
		if (type != null) {
		    Node reftypeNode = type.jjtGetChild(0);
			if (reftypeNode instanceof ASTReferenceType) {
			    Node classOrIntType = reftypeNode.jjtGetChild(0);
                if (classOrIntType instanceof ASTClassOrInterfaceType){
                    Class clazzType = ((ASTClassOrInterfaceType)classOrIntType).getType();
                    if((clazzType != null && (clazzType.equals(log4jLogger) || clazzType.equals(javaLogger))|| (clazzType == null&& "Logger".equals(classOrIntType.getImage())))) {
                        ++count;
                    }
                }
			}
		}

		return super.visit(node, data);
	}

}
