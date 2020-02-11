/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.GenericDeclaration;
import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.LexicalScope;
import net.sourceforge.pmd.util.CollectionUtil;

abstract class AbstractTypeParamOwnerSymbol<T extends GenericDeclaration> extends AbstractReflectedSymbol implements JTypeParameterOwnerSymbol {

    protected final T reflected;
    private final List<JTypeVar> typeParams;
    private LexicalScope scope;

    AbstractTypeParamOwnerSymbol(SymbolFactory factory, T tparamOwner) {
        super(factory);
        this.reflected = tparamOwner;
        this.typeParams = CollectionUtil.map(
            reflected.getTypeParameters(),
            tvar -> new ReflectedTypeParamImpl(this, tvar).getTypeMirror()
        );
    }

    @Override
    public LexicalScope getLexicalScope() {
        if (scope == null) {
            scope = JTypeParameterOwnerSymbol.super.getLexicalScope();
        }
        return scope;
    }

    @Override
    public final List<JTypeVar> getTypeParameters() {
        return typeParams;
    }
}
