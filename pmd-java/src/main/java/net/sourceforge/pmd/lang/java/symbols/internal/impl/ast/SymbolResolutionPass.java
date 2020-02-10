/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;


/**
 * Populates symbols on declaration nodes.
 */
public final class SymbolResolutionPass {

    private SymbolResolutionPass() {
        // façade
    }

    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        processor.getAstSymFactory().createSymbolsOn(root);
    }
}
