/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

@InternalApi
public final class ModelicaSymbolFacade {

    private ModelicaSymbolFacade() {
        // util class
    }

    public static void process(ASTStoredDefinition node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
        node.acceptVisitor(sc, null);
    }
}
