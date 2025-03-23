/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;

public final class SymbolFacade {

    private SymbolFacade() {

    }

    public static void process(ASTInput node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
        node.acceptVisitor(sc, null);
        OccurrenceFinder of = new OccurrenceFinder();
        node.acceptVisitor(of, null);
    }
}
