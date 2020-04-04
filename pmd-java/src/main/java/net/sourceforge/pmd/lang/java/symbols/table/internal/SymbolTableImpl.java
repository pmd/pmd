/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowGroup;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowGroupBuilder;

final class SymbolTableImpl implements JSymbolTable {

    static JSymbolTable EMPTY = new SymbolTableImpl(ShadowGroupBuilder.rootGroup(), ShadowGroupBuilder.rootGroup(), ShadowGroupBuilder.rootGroup());

    private final ShadowGroup<JVariableSymbol, ScopeInfo> vars;
    private final ShadowGroup<JTypeDeclSymbol, ScopeInfo> types;
    private final ShadowGroup<JMethodSymbol, ScopeInfo> methods;

    SymbolTableImpl(ShadowGroup<JVariableSymbol, ScopeInfo> vars,
                    ShadowGroup<JTypeDeclSymbol, ScopeInfo> types,
                    ShadowGroup<JMethodSymbol, ScopeInfo> methods) {
        this.vars = vars;
        this.types = types;
        this.methods = methods;
    }

    @Override
    public ShadowGroup<JVariableSymbol, ScopeInfo> variables() {
        return vars;
    }

    @Override
    public ShadowGroup<JTypeDeclSymbol, ScopeInfo> types() {
        return types;
    }

    @Override
    public ShadowGroup<JMethodSymbol, ScopeInfo> methods() {
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

    static JSymbolTable withVars(JSymbolTable parent, ShadowGroup<JVariableSymbol, ScopeInfo> vars) {
        return new SymbolTableImpl(vars, parent.types(), parent.methods());
    }

    static JSymbolTable withTypes(JSymbolTable parent, ShadowGroup<JTypeDeclSymbol, ScopeInfo> types) {
        return new SymbolTableImpl(parent.variables(), types, parent.methods());
    }
}
