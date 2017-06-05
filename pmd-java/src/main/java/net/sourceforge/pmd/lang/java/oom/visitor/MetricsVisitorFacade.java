/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * Wraps the visitor.
 *
 * @author Clément Fournier
 */
public class MetricsVisitorFacade extends JavaParserVisitorAdapter {

    public static final PackageStats TOP_LEVEL_PACKAGE = new PackageStats();

    public void initializeWith(ClassLoader classLoader, ASTCompilationUnit rootNode) {
        MetricsVisitor visitor = new MetricsVisitor();
        rootNode.jjtAccept(visitor, TOP_LEVEL_PACKAGE);
    }
}
