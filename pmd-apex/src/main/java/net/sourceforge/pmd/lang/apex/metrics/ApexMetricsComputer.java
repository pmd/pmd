/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

/**
 * Computes metrics for the Apex framework.
 *
 * @author Clément Fournier
 */
public class ApexMetricsComputer extends AbstractMetricsComputer<ASTUserClassOrInterface<?>, ASTMethod> {

    private static final ApexMetricsComputer INSTANCE = new ApexMetricsComputer();


    @InternalApi
    public static ApexMetricsComputer getInstance() {
        return INSTANCE;
    }


    @Override
    protected List<ASTMethod> findOperations(ASTUserClassOrInterface<?> node) {
        List<ASTMethod> candidates = node.findChildrenOfType(ASTMethod.class);
        List<ASTMethod> result = new ArrayList<>(candidates);
        for (ASTMethod method : candidates) {
            if (method.getImage().matches("(<clinit>|<init>|clone)")) {
                result.remove(method);
            }
        }
        return result;
    }
}
