/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * A method/constructor should not explicitly declare java.lang.RuntimeException or 
 * it's subclasses in throws clause of it's signature, since it is advised to avoid 
 * declaring unchecked exceptions in method signature.
 *
 * @author <a href="prakashbp2020@gmail.com">Bhanu Prakash Pamidi</a>
 */
public class SignatureDeclareThrowsRuntimeExceptionRule extends AbstractJavaRule {
    
    public SignatureDeclareThrowsRuntimeExceptionRule() {
        super();
        addRuleChainVisit(ASTMethodDeclaration.class);
        addRuleChainVisit(ASTConstructorDeclaration.class);
    } 

    @Override
    public Object visit(ASTMethodDeclaration methodDeclaration, Object o) {
        checkExceptions(methodDeclaration, o);
        return o;
    }


    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        checkExceptions(constructorDeclaration, o);
        return o;
    }

    /**
     * Search the list of thrown exceptions for Exception
     */
    private void checkExceptions(Node method, Object o) {
        List<ASTName> exceptionList = Collections.emptyList();
        ASTNameList nameList = method.getFirstChildOfType(ASTNameList.class);
        if (nameList != null) {
            exceptionList = nameList.findDescendantsOfType(ASTName.class);
        }
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
    }

    /**
     * Checks all exceptions for possible violation on the exception
     * declaration.
     *
     * @param exceptionList
     *            containing all exception for declaration
     * @param context
     */
    private void evaluateExceptions(List<ASTName> exceptionList, Object context) {
        for (ASTName exception : exceptionList) {
            if (isRuntimeException(exception)) {
                addViolation(context, exception);
            }
        }
    }

    /**
     * Checks if the given value is defined as <code>RuntimeException</code> and the
     * parent is either a method or constructor declaration.
     *
     * @param exception
     *            to evaluate
     * @return true if <code>Exception</code> is declared and has proper parents
     */
    private boolean isRuntimeException(ASTName exception) {
        return exception.getType() != null && TypeHelper.isA(exception, RuntimeException.class);
    }
}
