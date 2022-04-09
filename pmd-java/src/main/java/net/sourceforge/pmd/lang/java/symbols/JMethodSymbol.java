/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;


/**
 * Reference to a method.
 *
 * @since 7.0.0
 */
public interface JMethodSymbol extends JExecutableSymbol, BoundToNode<ASTMethodDeclaration> {


    // note that for now, bridge methods are filtered out from the ASM
    // symbols, and bridge methods are not reflected by the AST symbols
    boolean isBridge();


    /** Returns the return type under the given substitution. */
    JTypeMirror getReturnType(Substitution subst);


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitMethod(this, param);
    }

}
