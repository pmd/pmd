/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.SymbolDeclaratorNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;


/**
 * Populates symbols on declaration nodes.
 */
public final class SymbolResolutionPass {

    private SymbolResolutionPass() {
        // façade
    }

    /**
     * Traverse the given compilation unit, creating symbols on all
     * {@link SymbolDeclaratorNode}s.
     *
     * @param processor Processor
     * @param root      Root node
     *
     * @return A symbol resolver for all encountered type declarations.
     *     This is used to avoid hitting the classloader for local declarations.
     */
    public static SymbolResolver traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        AstSymbolMakerVisitor visitor = new AstSymbolMakerVisitor(root);
        root.acceptVisitor(visitor, new AstSymFactory(processor.getTypeSystem()));
        return visitor.makeKnownSymbolResolver();
    }
}
