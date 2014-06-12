/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
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

    private static final Class<?> LOG4J_LOGGER;

    private static final Class<?> JAVA_LOGGER;
    
    private static final Class<?> SLF4J_LOGGER;

    static {
	Class<?> c;
	try {
	    c = Class.forName("org.apache.log4j.Logger");
	} catch (Throwable t) {
	    c = null;
	}
	LOG4J_LOGGER = c;
	try {
	    c = Class.forName("java.util.logging.Logger");
	} catch (Throwable t) {
	    c = null;
	}
	JAVA_LOGGER = c;
	try {
	    c = Class.forName("org.slf4j.Logger");
	} catch (Throwable t) {
	    c = null;
	}
	SLF4J_LOGGER = c;
    }

    private Stack<Integer> stack = new Stack<Integer>();

    private Integer count;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
	return init(node, data);
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
	return init(node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
	return init(node, data);
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

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
	if (count > 1) {
	    return super.visit(node, data);
	}
	Node type = node.jjtGetParent().getFirstChildOfType(ASTType.class);
	if (type != null) {
	    Node reftypeNode = type.jjtGetChild(0);
	    if (reftypeNode instanceof ASTReferenceType) {
		Node classOrIntType = reftypeNode.jjtGetChild(0);
		if (classOrIntType instanceof ASTClassOrInterfaceType) {
		    Class<?> clazzType = ((ASTClassOrInterfaceType) classOrIntType).getType();
		    if (clazzType != null && (clazzType.equals(LOG4J_LOGGER) 
		                           || clazzType.equals(JAVA_LOGGER) 
		                           || clazzType.equals(SLF4J_LOGGER))
			    || clazzType == null && "Logger".equals(classOrIntType.getImage())) {
			++count;
		    }
		}
	    }
	}

	return super.visit(node, data);
    }

}
