/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>throw</code> statements containing <code>NullPointerException</code>
 * instances as thrown values
 *
 * @author <a href="mailto:michaeller.2012@gmail.com">Mykhailo Palahuta</a>
 */
public class AvoidThrowingNullPointerExceptionRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceBody body, Object data) {
        List<ASTThrowStatement> throwNPEs = getThrowNullPointerExceptionStatements(body);
        for (ASTThrowStatement throwNPE : throwNPEs) {
            addViolation(data, throwNPE);
        }
        return data;
    }

    private List<ASTThrowStatement> getThrowNullPointerExceptionStatements(ASTClassOrInterfaceBody body) {
        List<ASTThrowStatement> throwStatements = body.findDescendantsOfType(ASTThrowStatement.class);
        List<String> npeInstances = getNullPointerExceptionInstances(body);
        List<ASTThrowStatement> throwNPEStatements = new ArrayList<>();
        for (ASTThrowStatement throwStatement : throwStatements) {
            if (throwsNullPointerException(throwStatement, npeInstances)) {
                throwNPEStatements.add(throwStatement);
            }
        }
        return throwNPEStatements;
    }

    private List<String> getNullPointerExceptionInstances(ASTClassOrInterfaceBody body) {
        List<ASTAllocationExpression> allocations = body.findDescendantsOfType(ASTAllocationExpression.class);
        List<String> npeInstances = new ArrayList<>();
        for (ASTAllocationExpression allocation : allocations) {
            if (allocatesNullPointerException(allocation)) {
                String assignedVarName = getNameOfAssignedVariable(allocation);
                npeInstances.add(assignedVarName);
            }
        }
        return npeInstances;
    }

    private boolean allocatesNullPointerException(ASTAllocationExpression allocation) {
        Class<?> allocatedType = getAllocatedInstanceType(allocation);
        return allocatedType != null && NullPointerException.class.isAssignableFrom(allocatedType);
    }

    private Class<?> getAllocatedInstanceType(ASTAllocationExpression allocation) {
        List<ASTClassOrInterfaceType> allocatedTypes = allocation
                .findDescendantsOfType(ASTClassOrInterfaceType.class);
        return allocatedTypes.isEmpty() ? null : allocatedTypes.get(0).getType();
    }

    private String getNameOfAssignedVariable(ASTAllocationExpression allocation) {
        List<ASTVariableDeclarator> variableDeclarators = allocation.getParent()
                .findDescendantsOfType(ASTVariableDeclarator.class);
        return variableDeclarators.isEmpty() ? null : variableDeclarators.get(0).getName();
    }

    private boolean throwsNullPointerException(ASTThrowStatement throwStatement, List<String> npeInstances) {
        String thrownImage = throwStatement.getFirstClassOrInterfaceTypeImage();
        return "NullPointerException".equals(thrownImage) || npeInstances.contains(thrownImage);
    }
}
