/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;

/**
 * @author Clément Fournier
 */
final class AstCtorSymbol extends AbstractAstExecSymbol<ASTConstructorDeclaration> implements JConstructorSymbol {

    public AstCtorSymbol(ASTConstructorDeclaration node, AstSymFactory factory, JClassSymbol owner) {
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

}
