/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * @author Clément Fournier
 */
public class MultifileVisitorFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit rootNode) {
        PackageStats projectMirror = MultifileFacade.getTopLevelPackageStats();
        MultifileVisitor visitor = new MultifileVisitor(projectMirror);
        rootNode.jjtAccept(visitor, null);
    }


}
