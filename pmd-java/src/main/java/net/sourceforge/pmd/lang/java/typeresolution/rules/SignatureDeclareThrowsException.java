/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.rules;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

/**
 * A method/constructor shouldn't explicitly throw java.lang.Exception, since it
 * is unclear which exceptions that can be thrown from the methods. It might be
 * difficult to document and understand the vague interfaces. Use either a class
 * derived from RuntimeException or a checked exception. This version uses PMD's
 * type resolution facilities, and can detect if the class implements or extends
 * TestCase class
 *
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 * @author acaplan
 * @author Wouter Zelle
 */
public class SignatureDeclareThrowsException extends AbstractJavaRule {
	
    private static final BooleanProperty IGNORE_JUNIT_COMPLETELY_DESCRIPTOR = new BooleanProperty("IgnoreJUnitCompletely",
        "Allow all methods in a JUnit testcase to throw Exceptions", false, 1.0f);

    //Set to true when the class is determined to be a JUnit testcase
    private boolean junitImported = false;
    
    public SignatureDeclareThrowsException() {
	definePropertyDescriptor(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR);
    }
    
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (junitImported) {
	    return super.visit(node, data);
	}

        ASTImplementsList impl = node.getFirstChildOfType(ASTImplementsList.class);
        if (impl != null && impl.jjtGetParent().equals(node)) {
            for (int ix = 0; ix < impl.jjtGetNumChildren(); ix++) {
                ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) impl.jjtGetChild(ix);
                if (isJUnitTest(type)) {
                    junitImported = true;
                    return super.visit(node, data);
                }
            }
        }
        if (node.jjtGetNumChildren() != 0 && node.jjtGetChild(0) instanceof ASTExtendsList) {
            ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.jjtGetChild(0).jjtGetChild(0);
            if (isJUnitTest(type)) {
                junitImported = true;
                return super.visit(node, data);
            }
        }

        return super.visit(node, data);
    }

    private boolean isJUnitTest(ASTClassOrInterfaceType type) {
    	Class<?> clazz = type.getType();
        if (clazz == null) {
            if ("junit.framework.Test".equals(type.getImage())) {
            	return true;
            }
        } else if (isJUnitTest(clazz)) {
        	return true;
        } else {
        	while (clazz != null && !Object.class.equals(clazz)) {
	        	for(Class<?> intf : clazz.getInterfaces()) {
	        		if (isJUnitTest(intf)) {
	        			return true;
	        		}
	        	}
                clazz = clazz.getSuperclass();
        	}
        }
        return false;
    }

    private boolean isJUnitTest(Class<?> clazz) {
    	return clazz.getName().equals("junit.framework.Test");
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object o) {
        if (node.getImportedName().indexOf("junit") != -1) {
            junitImported = true;
        }
        return super.visit(node, o);
    }


    @Override
    public Object visit(ASTMethodDeclaration methodDeclaration, Object o) {
        if (junitImported && isAllowedMethod(methodDeclaration)) {
            return super.visit(methodDeclaration, o);
        }

        checkExceptions(methodDeclaration, o);

        return super.visit(methodDeclaration, o);
    }

    private boolean isAllowedMethod(ASTMethodDeclaration methodDeclaration) {
        if (getProperty(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR)) {
	    return true;
	} else {
	    return methodDeclaration.getMethodName().equals("setUp") || methodDeclaration
                .getMethodName().equals("tearDown");
	}
    }

    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        checkExceptions(constructorDeclaration, o);

        return super.visit(constructorDeclaration, o);
    }

    /**
     * Search the list of thrown exceptions for Exception
     */
    private void checkExceptions(Node method, Object o) {
        List<ASTName> exceptionList = method.findDescendantsOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
    }

    /**
     * Checks all exceptions for possible violation on the exception declaration.
     *
     * @param exceptionList containing all exception for declaration
     * @param context
     */
    private void evaluateExceptions(List<ASTName> exceptionList, Object context) {
        for (ASTName exception: exceptionList) {
            if (hasDeclaredExceptionInSignature(exception)) {
                addViolation(context, exception);
            }
        }
    }

    /**
     * Checks if the given value is defined as <code>Exception</code> and the parent is either
     * a method or constructor declaration.
     *
     * @param exception to evaluate
     * @return true if <code>Exception</code> is declared and has proper parents
     */
    private boolean hasDeclaredExceptionInSignature(ASTName exception) {
        return exception.hasImageEqualTo("Exception") && isParentSignatureDeclaration(exception);
    }

    /**
     * @param exception to evaluate
     * @return true if parent node is either a method or constructor declaration
     */
    private boolean isParentSignatureDeclaration(ASTName exception) {
        Node parent = exception.jjtGetParent().jjtGetParent();
        return parent instanceof ASTMethodDeclaration || parent instanceof ASTConstructorDeclaration;
    }
}