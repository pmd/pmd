/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

/**
 * Visitor over symbols.
 */
public interface SymbolVisitor<R, P> {


    R visitSymbol(JElementSymbol sym, P p);

    default R visitTypeDecl(JTypeDeclSymbol sym, P param) {
        return visitSymbol(sym, param);
    }

    /** Delegates to {@link #visitTypeDecl(JTypeDeclSymbol, Object) visitTypeDecl}. */
    default R visitClass(JClassSymbol sym, P param) {
        return visitTypeDecl(sym, param);
    }

    /** Delegates to {@link #visitClass(JClassSymbol, Object) visitClass}. */
    default R visitArray(JClassSymbol sym, JTypeDeclSymbol component, P param) {
        return visitClass(sym, param);
    }

    /** Delegates to {@link #visitTypeDecl(JTypeDeclSymbol, Object) visitTypeDecl}. */
    default R visitTypeParam(JTypeParameterSymbol sym, P param) {
        return visitTypeDecl(sym, param);
    }


    default R visitExecutable(JExecutableSymbol sym, P param) {
        return visitSymbol(sym, param);
    }

    /** Delegates to {@link #visitExecutable(JExecutableSymbol, Object) visitExecutable}. */
    default R visitCtor(JConstructorSymbol sym, P param) {
        return visitExecutable(sym, param);
    }

    /** Delegates to {@link #visitExecutable(JExecutableSymbol, Object) visitExecutable}. */
    default R visitMethod(JMethodSymbol sym, P param) {
        return visitExecutable(sym, param);
    }


    default R visitVariable(JVariableSymbol sym, P param) {
        return visitSymbol(sym, param);
    }

    /** Delegates to {@link #visitVariable(JVariableSymbol, Object) visitVariable}. */
    default R visitField(JFieldSymbol sym, P param) {
        return visitVariable(sym, param);
    }

    /** Delegates to {@link #visitVariable(JVariableSymbol, Object) visitVariable}. */
    default R visitLocal(JLocalVariableSymbol sym, P param) {
        return visitVariable(sym, param);
    }

    /** Delegates to {@link #visitLocal(JLocalVariableSymbol, Object) visitLocal}. */
    default R visitFormal(JFormalParamSymbol sym, P param) {
        return visitLocal(sym, param);
    }

}
