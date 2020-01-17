/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

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
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.util.NumericConstants;

public class MoreThanOneLoggerRule extends AbstractJavaRule {

    private static final String LOG4J_LOGGER_NAME = "org.apache.log4j.Logger";
    private static final String LOG4J2_LOGGER_NAME = "org.apache.logging.log4j.Logger";
    private static final String JAVA_LOGGER_NAME = "java.util.logging.Logger";
    private static final String SLF4J_LOGGER_NAME = "org.slf4j.Logger";

    private Stack<Integer> stack = new Stack<>();

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
        ASTType type = node.getParent().getFirstChildOfType(ASTType.class);
        if (type != null) {
            Node reftypeNode = type.getChild(0);
            if (reftypeNode instanceof ASTReferenceType) {
                Node classOrIntType = reftypeNode.getChild(0);
                if (classOrIntType instanceof ASTClassOrInterfaceType) {
                    Class<?> clazzType = ((ASTClassOrInterfaceType) classOrIntType).getType();
                    if (clazzType != null
                            && (TypeHelper.isA((ASTClassOrInterfaceType) classOrIntType, LOG4J_LOGGER_NAME)
                            || TypeHelper.isA((ASTClassOrInterfaceType) classOrIntType, LOG4J2_LOGGER_NAME)
                            || TypeHelper.isA((ASTClassOrInterfaceType) classOrIntType, JAVA_LOGGER_NAME)
                                || TypeHelper.isA((ASTClassOrInterfaceType) classOrIntType, SLF4J_LOGGER_NAME))
                            || clazzType == null && "Logger".equals(classOrIntType.getImage())) {
                        ++count;
                    }
                }
            }
        }

        return super.visit(node, data);
    }

}
