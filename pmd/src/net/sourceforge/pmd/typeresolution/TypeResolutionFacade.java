/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

/**
 * @author Allan Caplan
 */
public class TypeResolutionFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit node) {
        ClassTypeResolver ctr = new ClassTypeResolver();
        node.jjtAccept(ctr, null);
    }

}
