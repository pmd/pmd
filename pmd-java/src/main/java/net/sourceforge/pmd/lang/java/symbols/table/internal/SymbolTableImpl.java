/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainNode;

final class SymbolTableImpl implements JSymbolTable {

    static final JSymbolTable EMPTY = new SymbolTableImpl(ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup());

    private final ShadowChainNode<JVariableSymbol, ScopeInfo> vars;
    private final ShadowChainNode<JTypeDeclSymbol, ScopeInfo> types;
    private final ShadowChainNode<JMethodSymbol, ScopeInfo> methods;

    SymbolTableImpl(ShadowChainNode<JVariableSymbol, ScopeInfo> vars,
                    ShadowChainNode<JTypeDeclSymbol, ScopeInfo> types,
                    ShadowChainNode<JMethodSymbol, ScopeInfo> methods) {
        this.vars = vars;
        this.types = types;
        this.methods = methods;
    }

    @Override
    public ShadowChain<JVariableSymbol, ScopeInfo> variables() {
        return vars.asChain();
    }

    @Override
    public ShadowChain<JTypeDeclSymbol, ScopeInfo> types() {
        return types.asChain();
    }

    @Override
    public ShadowChain<JMethodSymbol, ScopeInfo> methods() {
        return methods.asChain();
    }

    @Override
    public String toString() {
        return "NSymTableImpl{"
            + "vars=" + vars
            + ", types=" + types
            + ", methods=" + methods
            + '}';
    }

    static JSymbolTable withVars(JSymbolTable parent, ShadowChainNode<JVariableSymbol, ScopeInfo> vars) {
        return new SymbolTableImpl(vars, parent.types().asNode(), parent.methods().asNode());
    }

    static JSymbolTable withTypes(JSymbolTable parent, ShadowChainNode<JTypeDeclSymbol, ScopeInfo> types) {
        return new SymbolTableImpl(parent.variables().asNode(), types, parent.methods().asNode());
    }
}
