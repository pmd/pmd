/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsFacade;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;

/**
 * @author Clément Fournier
 */
public class ApexMetricsFacade extends AbstractMetricsFacade<ASTUserClassOrInterface<?>, ASTMethod> {

    private final ApexProjectMirror projectMirror = new ApexProjectMirror();


    /** Resets the entire project mirror. Used for tests. */
    void reset() {
        projectMirror.reset();
    }


    @Override
    protected MetricsComputer<ASTUserClassOrInterface<?>, ASTMethod> getLanguageSpecificComputer() {
        return ApexMetricsComputer.INSTANCE;
    }


    @Override
    protected ApexProjectMirror getLanguageSpecificProjectMirror() {
        return projectMirror;
    }
}
