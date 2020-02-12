/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

public class SymbolToStrings {
    public static final SymbolToStrings REFLECT = new SymbolToStrings("reflected");
    public static final SymbolToStrings AST = new SymbolToStrings("ast");

    private final String impl;

    public SymbolToStrings(String impl) {
        // util class
        this.impl = impl;
    }

    public String toString(JElementSymbol symbol) {
        if (symbol instanceof JClassSymbol) {
            return classToString((JClassSymbol) symbol);
        } else if (symbol instanceof JConstructorSymbol) {
            return ctorToString((JConstructorSymbol) symbol);
        } else if (symbol instanceof JMethodSymbol) {
            return methodToString((JMethodSymbol) symbol);
        } else if (symbol instanceof JFieldSymbol) {
            return fieldToString((JFieldSymbol) symbol);
        } else if (symbol instanceof JLocalVariableSymbol) {
            return localToString((JLocalVariableSymbol) symbol);
        } else if (symbol instanceof JTypeParameterSymbol) {
            return typeParamToString((JTypeParameterSymbol) symbol);
        }
        throw new IllegalArgumentException("Unknown symbol type " + symbol);
    }

    public String classToString(JClassSymbol sym) {
        return withImpl("class", sym.getBinaryName());
    }

    public String methodToString(JMethodSymbol sym) {
        return withImpl("method", sym.getSimpleName(), classToString(sym.getEnclosingClass()));
    }

    public String ctorToString(JConstructorSymbol sym) {
        return withImpl("ctor", classToString(sym.getEnclosingClass()));
    }

    public String fieldToString(JFieldSymbol sym) {
        return withImpl("field", sym.getSimpleName(), classToString(sym.getEnclosingClass()));
    }

    public String localToString(JLocalVariableSymbol sym) {
        return withImpl("local", sym.getSimpleName());
    }

    public String typeParamToString(JTypeParameterSymbol sym) {
        return withImpl("tparam", sym.getSimpleName(), toString(sym.getDeclaringSymbol()));
    }

    private String withImpl(String kind, String... rest) {
        return impl + ":" + kind + "(" + StringUtils.joinWith(", ", (Object[]) rest) + ")";
    }

}
