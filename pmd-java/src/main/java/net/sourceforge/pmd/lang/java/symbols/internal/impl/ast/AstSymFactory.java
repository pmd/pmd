/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.types.TypeSystem;


final class AstSymFactory {


    private final TypeSystem ts;

    AstSymFactory(TypeSystem ts) {
        this.ts = ts;
    }

    public TypeSystem types() {
        return ts;
    }

    // keep in mind, creating a symbol sets it on the node (see constructor of AbstractAstBackedSymbol)

    /**
     * Builds, sets and returns the symbol for the given local variable.
     */
    void setLocalVarSymbol(ASTVariableDeclaratorId id) {
        assert !id.isField() && !id.isEnumConstant() : "Local var symbol is not appropriate for fields";
        assert !id.isFormalParameter()
            || id.isLambdaParameter()
            || id.isExceptionBlockParameter() : "Local var symbol is not appropriate for method parameters";

        new AstLocalVarSym(id, this);
    }

    /**
     * Builds, sets and returns the symbol for the given class.
     */
    JClassSymbol setClassSymbol(@Nullable JTypeParameterOwnerSymbol enclosing, ASTAnyTypeDeclaration klass) {
        if (enclosing instanceof JClassSymbol && klass.isNested()) {
            JClassSymbol inner = ((JClassSymbol) enclosing).getDeclaredClass(klass.getSimpleName());
            assert inner != null : "Inner class symbol was not created for " + klass;
            return inner;
        }
        return new AstClassSym(klass, this, enclosing);
    }




}
