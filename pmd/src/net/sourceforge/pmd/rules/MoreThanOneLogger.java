package net.sourceforge.pmd.rules;

import java.util.Stack;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.NumericConstants;

public class MoreThanOneLogger extends AbstractRule {
    
    private static Class log4jLogger = null;

    private static Class javaLogger = null;

    static {
        try {
            log4jLogger = Class.forName("org.apache.log4j.Logger");
        } catch (Throwable t) { //NOPMD somewhat legitmate catch all
            log4jLogger = null;
        }
        try {
            javaLogger = Class.forName("java.util.logging.Logger");
        } catch (Throwable t) { //NOPMD somewhat legitmate catch all
            javaLogger = null;
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

	public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
		return init (node, data);
	}	

	private Object init(SimpleJavaNode node, Object data) {
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
		SimpleNode type = ((SimpleNode) node.jjtGetParent()).getFirstChildOfType(ASTType.class);
		if (type != null) {
			SimpleNode reftypeNode = (SimpleNode) type.jjtGetChild(0);
			if (reftypeNode instanceof ASTReferenceType) {
                SimpleNode classOrIntType = (SimpleNode) reftypeNode.jjtGetChild(0);
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
