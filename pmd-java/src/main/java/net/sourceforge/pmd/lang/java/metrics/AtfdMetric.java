/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.metrics.OperationSignature.Visibility;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class AtfdMetric extends AbstractMetric implements ClassMetric, MethodMetric {

    static {
        operationMask = new OperationSigMask();
        operationMask.setAllVisibility();
        operationMask.setAllRoles();
        operationMask.remove(Role.CONSTRUCTOR);
    }

    @Override
    public double computeFor(ASTMethodDeclaration node, PackageStats holder) {
        if (!checkMaskIsMatching(node)) {
            return Double.NaN;
        }
        
        OperationSigMask targetOps = new OperationSigMask();
        targetOps.setVisibilityMask(Visibility.PUBLIC);
        targetOps.setRoleMask(Role.GETTERORSETTER);

        List<String> callQNames = findAllCalls(node);
        int atfd = 0;
        for (String name : callQNames) {
            if (holder.hasMatchingSig(name, targetOps)) {
                atfd++;
            }
        }
        
        return atfd;
    }

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        // TODO
        return 0;
    }
}
