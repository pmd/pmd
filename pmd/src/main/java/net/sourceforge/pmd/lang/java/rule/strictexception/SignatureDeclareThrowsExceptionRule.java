/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strictexception;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * <p/>
 *
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 * @version 1.0
 * @since 1.2
 */

public class SignatureDeclareThrowsExceptionRule extends AbstractJavaRule {

    private boolean junitImported;

    @Override
    public Object visit(ASTCompilationUnit node, Object o) {
        junitImported = false;
        return super.visit(node, o);
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
        if ((methodDeclaration.getMethodName().equals("setUp") || methodDeclaration.getMethodName().equals("tearDown")) && junitImported) {
            return super.visit(methodDeclaration, o);
        }

        if (methodDeclaration.getMethodName().startsWith("test")) {
            return super.visit(methodDeclaration, o);
        }

        List<ASTName> exceptionList = Collections.emptyList();
        ASTNameList nameList = methodDeclaration.getFirstChildOfType(ASTNameList.class);
        if (nameList != null) {
            exceptionList = nameList.findDescendantsOfType(ASTName.class);
        }
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
        return super.visit(methodDeclaration, o);
    }


    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        List<ASTName> exceptionList = constructorDeclaration.findDescendantsOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
        return super.visit(constructorDeclaration, o);
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
     * Checks if the given exception is declared in the method or constructor
     * signature.
     * @param exception to evaluate
     * @return true if parent node is either a method or constructor declaration
     */
    private boolean isParentSignatureDeclaration(ASTName exception) {
        Node parent = exception.jjtGetParent().jjtGetParent();
        return parent instanceof ASTMethodDeclaration || parent instanceof ASTConstructorDeclaration;
    }

}
