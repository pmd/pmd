/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;

/**
 * @author Clément Fournier
 *
 */
public class WmcMetric extends AbstractMetric implements ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
