/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;
import net.sourceforge.pmd.lang.java.oom.visitor.Signature.Visibility;

/**
 * Access to Foreign Data. Quantifies the number of foreign fields accessed directly or via accessors.
 *
 * @author Clément Fournier
 */
public class AtfdMetric extends AbstractMetric implements ClassMetric, OperationMetric {

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder) {
        if (!isSupported(node)) {
            return Double.NaN;
        }

        OperationSigMask targetOps = new OperationSigMask();
        targetOps.setVisibilityMask(Visibility.PUBLIC);
        targetOps.setRoleMask(Role.GETTER_OR_SETTER);

        List<QualifiedName> callQNames = findAllCalls(node);
        int foreignCalls = 0;
        for (QualifiedName name : callQNames) {
            if (holder.hasMatchingSig(name, targetOps)) {
                foreignCalls++;
            }
        }

        return foreignCalls / callQNames.size();
    }

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        // TODO
        return 0;
    }
}
