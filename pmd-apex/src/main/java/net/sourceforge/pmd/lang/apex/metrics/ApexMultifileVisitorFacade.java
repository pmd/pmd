/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

/**
 * TODO: move that out!
 * @author Clément Fournier
 */
public class ApexMultifileVisitorFacade extends ApexParserVisitorAdapter {

    public void initializeWith(ApexNode<?> rootNode) {
        ApexMetricsFacade facade = ApexMetrics.getFacade();
        ApexMultifileVisitor visitor = new ApexMultifileVisitor(facade.getProjectMirror());
        rootNode.jjtAccept(visitor, null);
    }

}
