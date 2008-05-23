/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * @author Allan Caplan
 */
public class TypeResolutionFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ClassLoader classLoader, ASTCompilationUnit node) {
        ClassTypeResolver classTypeResolver = new ClassTypeResolver(classLoader);
        node.setClassTypeResolver(classTypeResolver);
        node.jjtAccept(classTypeResolver, null);
    }

}
