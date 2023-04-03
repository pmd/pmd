/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;

final class SymbolTableImpl implements JSymbolTable {

    static final JSymbolTable EMPTY = new SymbolTableImpl(ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup(), ShadowChainBuilder.rootGroup());

    private final ShadowChainNode<JVariableSig, ScopeInfo> vars;
    private final ShadowChainNode<JTypeMirror, ScopeInfo> types;
    private final ShadowChainNode<JMethodSig, ScopeInfo> methods;

    SymbolTableImpl(ShadowChainNode<JVariableSig, ScopeInfo> vars,
                    ShadowChainNode<JTypeMirror, ScopeInfo> types,
                    ShadowChainNode<JMethodSig, ScopeInfo> methods) {
        this.vars = vars;
        this.types = types;
        this.methods = methods;
    }

    @Override
    public ShadowChain<JVariableSig, ScopeInfo> variables() {
        return vars.asChain();
    }

    @Override
    public ShadowChain<JTypeMirror, ScopeInfo> types() {
        return types.asChain();
    }

    @Override
    public ShadowChain<JMethodSig, ScopeInfo> methods() {
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

    static JSymbolTable withVars(JSymbolTable parent, ShadowChainNode<JVariableSig, ScopeInfo> vars) {
        return new SymbolTableImpl(vars, parent.types().asNode(), parent.methods().asNode());
    }

    static JSymbolTable withTypes(JSymbolTable parent, ShadowChainNode<JTypeMirror, ScopeInfo> types) {
        return new SymbolTableImpl(parent.variables().asNode(), types, parent.methods().asNode());
    }
}
