/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;


public final class AstSymFactory {

    /**
     * Builds, sets and returns the symbol for the given local variable.
     */
    public JLocalVariableSymbol setLocalVarSymbol(ASTVariableDeclaratorId id) {
        assert !id.isField() && !id.isEnumConstant() : "Local var symbol is not appropriate for fields";
        assert !id.isFormalParameter()
            || id.isLambdaParameter()
            || id.isExceptionBlockParameter() : "Local var symbol is not appropriate for method parameters";

        return new AstLocalVarSym(id, this);
    }

    /**
     * Builds, sets and returns the symbol for the given class.
     */
    public JClassSymbol setClassSymbol(@Nullable JTypeParameterOwnerSymbol enclosing, ASTAnyTypeDeclaration klass) {
        return new AstClassSym(klass, this, enclosing);
    }

}
