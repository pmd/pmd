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
        ApexMetricsVisitor visitor = new ApexMetricsVisitor();
        rootNode.jjtAccept(visitor, ApexMetrics.getApexProjectMirror());
    }

}
