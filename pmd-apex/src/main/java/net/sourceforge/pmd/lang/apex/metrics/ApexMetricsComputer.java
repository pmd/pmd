/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

/**
 * @author Clément Fournier
 */
public class ApexMetricsComputer extends AbstractMetricsComputer<ASTUserClass, ASTMethod> {

    public static final ApexMetricsComputer INSTANCE = new ApexMetricsComputer();

    @Override
    protected List<ASTMethod> findOperations(ASTUserClass node) {
        return node.findChildrenOfType(ASTMethod.class);
    }
}
