/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

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
