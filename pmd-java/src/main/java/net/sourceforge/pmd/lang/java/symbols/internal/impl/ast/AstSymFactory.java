/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;


public final class AstSymFactory {


    public void createSymbolsOn(ASTCompilationUnit acu) {
        acu.jjtAccept(new AstSymbolMakerVisitor(acu), this);
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
        return new AstClassSym(klass, this, enclosing);
    }

}
