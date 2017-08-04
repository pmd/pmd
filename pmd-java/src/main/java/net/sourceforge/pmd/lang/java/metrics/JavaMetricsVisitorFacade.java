/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * Wraps the visitor.
 *
 * @author Clément Fournier
 */
public class JavaMetricsVisitorFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit rootNode) {
        JavaMetricsFacade facade = JavaMetrics.getFacade();
        JavaMetricsVisitor visitor = new JavaMetricsVisitor(facade.getTopLevelPackageStats(),
                                                            facade.getLanguageSpecificProjectMemoizer());
        rootNode.jjtAccept(visitor, null);
    }

}
