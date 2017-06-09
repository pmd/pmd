/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.oom.Metrics;

/**
 * Wraps the visitor.
 *
 * @author Clément Fournier
 */
public class MetricsVisitorFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit rootNode) {
        MetricsVisitor visitor = new MetricsVisitor();
        rootNode.jjtAccept(visitor, Metrics.getTopLevelPackageStats());
    }
}
