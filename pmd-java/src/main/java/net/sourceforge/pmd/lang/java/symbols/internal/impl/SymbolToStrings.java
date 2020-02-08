/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

public enum SymbolToStrings {
    REFLECT("reflected"),
    AST("ast");

    private final String impl;

    SymbolToStrings(String impl) {
        // util class
        this.impl = impl;
    }

    public String classToString(JClassSymbol sym) {
        return withImpl(sym.getBinaryName());
    }

    public String methodToString(JMethodSymbol sym) {
        return withImpl(sym.getEnclosingClass() + "#" + sym.getSimpleName());
    }

    public String ctorToString(JConstructorSymbol sym) {
        return withImpl(sym.getEnclosingClass() + "#" + sym.getSimpleName());
    }

    public String fieldToString(JFieldSymbol sym) {
        return withImpl(sym.getEnclosingClass() + "#" + sym.getSimpleName());
    }

    public String typeParamToString(JTypeParameterSymbol sym) {
        return withImpl("tparam " + sym.getSimpleName() + " of " + sym.getEnclosingClass());
    }

    private String withImpl(String str) {
        return impl + "(" + str + ")";
    }

}
