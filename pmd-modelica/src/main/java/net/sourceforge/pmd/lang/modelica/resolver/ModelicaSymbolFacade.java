/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

public class ModelicaSymbolFacade {
    public void initializeWith(ASTStoredDefinition node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
        node.jjtAccept(sc, null);
    }
}
