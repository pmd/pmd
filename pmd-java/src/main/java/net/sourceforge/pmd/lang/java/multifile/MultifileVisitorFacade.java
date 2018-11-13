/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class MultifileVisitorFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit rootNode) {
        PackageStats projectMirror = PackageStats.INSTANCE;
        MultifileVisitor visitor = new MultifileVisitor(projectMirror);
        rootNode.jjtAccept(visitor, null);
    }


}
