/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pcollections.PMap;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

abstract class AbstractTypeParamOwnerSymbol<T extends GenericDeclaration> extends AbstractReflectedSymbol implements JTypeParameterOwnerSymbol {

    protected final T reflected;

    private PMap<String, JTypeParameterSymbol> lexicalScope;
    private List<JTypeParameterSymbol> typeParams;


    AbstractTypeParamOwnerSymbol(ReflectionSymFactory factory, T tparamOwner) {
        super(factory);
        this.reflected = tparamOwner;
    }


    @Override
    public final List<JTypeParameterSymbol> getTypeParameters() {
        if (typeParams == null) {
            buildTypeParams(reflected.getTypeParameters());
        }

        return typeParams;
    }

    private void buildTypeParams(TypeVariable<?>[] tparams) {

        List<ReflectedTypeParamImpl> result =
            tparams.length == 0
            ? Collections.emptyList()
            : Arrays.stream(tparams)
                    .map(tvar -> new ReflectedTypeParamImpl(symFactory, this, tvar))
                    .collect(toList());

        // this needs to be set before calling computeBounds
        this.typeParams = Collections.unmodifiableList(result);

    }

    @Override
    public int getTypeParameterCount() {
        return typeParams != null ? typeParams.size() : reflected.getTypeParameters().length;
    }

    @Override
    public PMap<String, JTypeParameterSymbol> getLexicalScope() {
        if (lexicalScope == null) {
            lexicalScope = JTypeParameterOwnerSymbol.super.getLexicalScope();
        }
        return lexicalScope;
    }
}
