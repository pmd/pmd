/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.SymbolDeclaratorNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.util.CollectionUtil;


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
        root.acceptVisitor(visitor, new AstSymFactory(processor));
        return visitor.makeKnownSymbolResolver();
    }

    /**
     * Converts between nodes to {@link SymAnnot}. Annotations that could not be converted,
     * eg because they are written with invalid code, are discarded.
     */
    public static PSet<SymAnnot> buildSymbolicAnnotations(NodeStream<ASTAnnotation> annotations) {
        return annotations.toStream()
                          .map(SymbolResolutionPass::toValidAnnotation)
                          .filter(Objects::nonNull)
                          .collect(CollectionUtil.toPersistentSet());
    }

    private static @Nullable SymAnnot toValidAnnotation(ASTAnnotation node) {
        JTypeDeclSymbol sym = InternalApiBridge.getReferencedSym(node.getTypeNode());
        if (sym instanceof JClassSymbol) {
            return new AstSymbolicAnnot(node, (JClassSymbol) sym);
        }
        return null;
    }
}
