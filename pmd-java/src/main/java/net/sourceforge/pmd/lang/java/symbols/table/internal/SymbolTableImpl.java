/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;

final class SymbolTableImpl implements JSymbolTable {

    static JSymbolTable EMPTY = new SymbolTableImpl(ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup());

    private final ShadowChain<JVariableSymbol, ScopeInfo> vars;
    private final ShadowChain<JTypeDeclSymbol, ScopeInfo> types;
    private final ShadowChain<JMethodSymbol, ScopeInfo> methods;

    SymbolTableImpl(ShadowChain<JVariableSymbol, ScopeInfo> vars,
                    ShadowChain<JTypeDeclSymbol, ScopeInfo> types,
                    ShadowChain<JMethodSymbol, ScopeInfo> methods) {
        this.vars = vars;
        this.types = types;
        this.methods = methods;
    }

    @Override
    public ShadowChain<JVariableSymbol, ScopeInfo> variables() {
        return vars;
    }

    @Override
    public ShadowChain<JTypeDeclSymbol, ScopeInfo> types() {
        return types;
    }

    @Override
    public ShadowChain<JMethodSymbol, ScopeInfo> methods() {
        return methods;
    }

    @Override
    public String toString() {
        return "NSymTableImpl{" +
            "vars=" + vars +
            ", types=" + types +
            ", methods=" + methods +
            '}';
    }

    static JSymbolTable withVars(JSymbolTable parent, ShadowChain<JVariableSymbol, ScopeInfo> vars) {
        return new SymbolTableImpl(vars, parent.types(), parent.methods());
    }

    static JSymbolTable withTypes(JSymbolTable parent, ShadowChain<JTypeDeclSymbol, ScopeInfo> types) {
        return new SymbolTableImpl(parent.variables(), types, parent.methods());
    }
}
