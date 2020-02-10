/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;

/**
 * @author Clément Fournier
 */
final class AstCtorSym extends AbstractAstExecSymbol<ASTConstructorDeclaration> implements JConstructorSymbol {

    AstCtorSym(ASTConstructorDeclaration node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory, owner);
    }


    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.CONSTRUCTOR.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.CONSTRUCTOR.hash(this);
    }


    @Override
    public String toString() {
        return SymbolToStrings.AST.ctorToString(this);
    }

}
