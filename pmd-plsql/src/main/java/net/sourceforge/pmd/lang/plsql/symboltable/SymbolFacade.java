/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;

public class SymbolFacade {
    public void initializeWith(ASTInput node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
        node.jjtAccept(sc, null);
        OccurrenceFinder of = new OccurrenceFinder();
        node.jjtAccept(of, null);
    }
}
