/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class AtfdMetric implements ClassMetric, MethodMetric {
    
    @Override
    public double computeFor(ASTMethodDeclaration node, PackageStats holder) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
