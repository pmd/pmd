/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>throw</code> statements containing <code>NullPointerException</code>
 * instances as thrown values
 *
 * @author <a href="mailto:michaeller.2012@gmail.com">Mykhailo Palahuta</a>
 */
public class AvoidThrowingNullPointerExceptionRule extends AbstractJavaRule {

    private final Set<String> npeInstances = new HashSet<>();

    @Override
    public Object visit(ASTVariableInitializer varInitializer, Object data) {
        String initialedVarName = getInitializedVariableName(varInitializer);
        processAssignmentToVariable(varInitializer, initialedVarName);
        return super.visit(varInitializer, data);
    }

    private String getInitializedVariableName(ASTVariableInitializer initializer) {
        ASTVariableDeclaratorId varDeclaratorId = initializer.getParent()
                .getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        return varDeclaratorId != null ? varDeclaratorId.getName() : null;
    }

    @Override
    public Object visit(ASTAssignmentOperator assignment, Object data) {
        String assignedVarName = getAssignedVariableName(assignment);
        processAssignmentToVariable(assignment, assignedVarName);
        return super.visit(assignment, data);
    }

    private String getAssignedVariableName(ASTAssignmentOperator assignment) {
        ASTName varName = assignment.getParent().getFirstDescendantOfType(ASTName.class);
        return varName != null ? varName.getImage() : null;
    }

    private void processAssignmentToVariable(JavaNode assignment, String varName) {
        Class<?> assignedValueType = getAssignedValueType(assignment);
        if (isNullPointerException(assignedValueType)) {
            npeInstances.add(varName);
        } else {
            npeInstances.remove(varName);
        }
    }

    private Class<?> getAssignedValueType(JavaNode assignment) {
        ASTClassOrInterfaceType assignedValueType = assignment.getParent()
                .getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return assignedValueType != null ? assignedValueType.getType() : null;
    }

    private boolean isNullPointerException(Class<?> clazz) {
        return NullPointerException.class.equals(clazz);
    }

    @Override
    public Object visit(ASTThrowStatement throwStatement, Object data) {
        if (throwsNullPointerException(throwStatement)) {
            addViolation(data, throwStatement);
        }
        return super.visit(throwStatement, data);
    }

    private boolean throwsNullPointerException(ASTThrowStatement throwStatement) {
        return throwsNullPointerExceptionType(throwStatement)
                || throwsNullPointerExceptionVariable(throwStatement);
    }

    private boolean throwsNullPointerExceptionType(ASTThrowStatement throwStatement) {
        ASTClassOrInterfaceType thrownType = throwStatement.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (thrownType != null) {
            Class<?> thrownException = thrownType.getType();
            return NullPointerException.class.equals(thrownException);
        }
        return false;
    }

    private boolean throwsNullPointerExceptionVariable(ASTThrowStatement throwStatement) {
        ASTName thrownVar = throwStatement.getFirstDescendantOfType(ASTName.class);
        if (thrownVar != null) {
            String thrownVarName = thrownVar.getImage();
            return npeInstances.contains(thrownVarName);
        }
        return false;
    }
}
