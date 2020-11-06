/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsFacade;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;

/**
 * Backs the static façade.
 *
 * @author Clément Fournier
 * @deprecated Not useful anymore
 */
@Deprecated
public class ApexMetricsFacade extends AbstractMetricsFacade<ASTUserClassOrInterface<?>, ASTMethod> {

    private final ApexProjectMemoizer memoizer = new ApexProjectMemoizer();


    @Override
    protected MetricsComputer<ASTUserClassOrInterface<?>, ASTMethod> getLanguageSpecificComputer() {
        return ApexMetricsComputer.getInstance();
    }


    @Override
    protected ApexProjectMemoizer getLanguageSpecificProjectMemoizer() {
        return memoizer;
    }
}
