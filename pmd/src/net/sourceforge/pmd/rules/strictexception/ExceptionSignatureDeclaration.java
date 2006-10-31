package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.Node;

import java.util.Iterator;
import java.util.List;

/**
 * <p/>
 *
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 * @version 1.0
 * @since 1.2
 */
public class ExceptionSignatureDeclaration extends AbstractRule {

    private boolean junitImported;

    public Object visit(ASTCompilationUnit node, Object o) {
        junitImported = false;
        return super.visit(node, o);
    }

    public Object visit(ASTImportDeclaration node, Object o) {
        if (node.getImportedName().indexOf("junit") != -1) {
            junitImported = true;
        }
        return super.visit(node, o);
    }

    public Object visit(ASTMethodDeclaration methodDeclaration, Object o) {
        if ((methodDeclaration.getMethodName().equals("setUp") || methodDeclaration.getMethodName().equals("tearDown")) && junitImported) {
            return super.visit(methodDeclaration, o);
        }

        if (methodDeclaration.getMethodName().startsWith("test")) {
            return super.visit(methodDeclaration, o);
        }

        List exceptionList = methodDeclaration.findChildrenOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
        return super.visit(methodDeclaration, o);
    }


    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        List exceptionList = constructorDeclaration.findChildrenOfType(ASTName.class);
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
    private void evaluateExceptions(List exceptionList, Object context) {
        ASTName exception;
        for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
            exception = (ASTName) iter.next();
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
