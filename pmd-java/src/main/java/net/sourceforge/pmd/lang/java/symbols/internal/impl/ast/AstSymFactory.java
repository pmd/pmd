/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;


public final class AstSymFactory implements SymbolFactory<ASTAnyTypeDeclaration> {

    /**
     * Returns the symbol for the given local variable.
     */
    public JLocalVariableSymbol getLocalVarSymbol(ASTVariableDeclaratorId id) {
        assert !id.isField() && !id.isEnumConstant() : "Local var symbol is not appropriate for fields";
        assert !id.isFormalParameter()
            || id.isLambdaParameter()
            || id.isExceptionBlockParameter() : "Local var symbol is not appropriate for method parameters";

        return new AstLocalVarSym(id, this);
    }

    @Override
    public JClassSymbol getClassSymbol(@Nullable ASTAnyTypeDeclaration klass) {
        if (klass == null) {
            return null;
        }

        JClassSymbol sym = InternalApiBridge.getSymbolInternal(klass);
        if (sym != null) {
            return sym;
        }

        @Nullable
        JClassSymbol encl = getClassSymbol(klass.getEnclosingType());
        return new AstClassSym(klass, this, encl);
    }

}
