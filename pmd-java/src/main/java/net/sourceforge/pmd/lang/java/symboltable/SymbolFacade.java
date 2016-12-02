/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public class SymbolFacade {
    public void initializeWith(ASTCompilationUnit node) {
        initializeWith(SymbolFacade.class.getClassLoader(), node);
    }

    public void initializeWith(ClassLoader classLoader, ASTCompilationUnit node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder(classLoader);
        node.jjtAccept(sc, null);
        OccurrenceFinder of = new OccurrenceFinder();
        node.jjtAccept(of, null);
    }
}
