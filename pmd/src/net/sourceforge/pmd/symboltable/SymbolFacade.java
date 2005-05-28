/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Iterator;

public class SymbolFacade {

    public void initializeWith(ASTCompilationUnit node) {
        // TODO - can the scope and declaration traversals be combined?

        // first create all the scopes
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor();
        node.jjtAccept(sc, null);

        // pick up all the declarations
        DeclarationFinder df = new DeclarationFinder();
        node.jjtAccept(df, null);

        // finally, pick up all the name occurrences
        OccurrenceFinder of = new OccurrenceFinder();
        node.jjtAccept(of, null);
    }

}
