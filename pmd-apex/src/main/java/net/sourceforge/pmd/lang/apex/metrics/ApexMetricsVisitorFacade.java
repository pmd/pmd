/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

/**
 * @author Clément Fournier
 */
public class ApexMetricsVisitorFacade extends ApexParserVisitorAdapter {


    public void initializeWith(ApexNode<?> rootNode) {
        ApexMetricsFacade facade = ApexMetrics.getFacade();
        ApexMetricsVisitor visitor = new ApexMetricsVisitor(facade.getLanguageSpecificProjectMemoizer(),
                                                            facade.getProjectMirror());
        rootNode.jjtAccept(visitor, null);
    }

}
