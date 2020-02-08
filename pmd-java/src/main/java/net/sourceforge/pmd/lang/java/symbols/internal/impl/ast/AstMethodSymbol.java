/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;

/**
 * @author Clément Fournier
 */
final class AstMethodSymbol
    extends AbstractAstExecSymbol<ASTMethodDeclaration>
    implements JMethodSymbol {


    public AstMethodSymbol(ASTMethodDeclaration node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory, owner);
    }

    @Override
    public String getSimpleName() {
        return node.getName();
    }


    @Override
    public boolean equals(Object o) {
        return SymbolEquality.METHOD.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.METHOD.hash(this);
    }

    @Override
    public String toString() {
        return SymbolToStrings.AST.methodToString(this);
    }
}
