/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.TypeParamOwnerNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstTParamOwner<T extends TypeParamOwnerNode & AccessNode>
    extends AbstractAstBackedSymbol<T> implements JTypeParameterOwnerSymbol {

    private final List<JTypeParameterSymbol> tparams;
    private final int modifiers;


    AbstractAstTParamOwner(T node, AstSymFactory factory) {
        super(node, factory);
        this.modifiers = JModifier.toReflect(node.getModifiers().getEffectiveModifiers());

        List<JTypeParameterSymbol> result = map(
            ASTList.orEmpty(node.getTypeParameters()),
            it -> new AstTypeParamSym(it, factory, this)
        );

        // this needs to be set before calling computeBounds
        this.tparams = Collections.unmodifiableList(result);

    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public List<JTypeParameterSymbol> getTypeParameters() {
        return tparams;
    }


    @Override
    public @NonNull String getPackageName() {
        return node.getRoot().getPackageName();
    }

    @Override
    public int getTypeParameterCount() {
        if (tparams != null) {
            return tparams.size();
        }
        ASTTypeParameters ps = node.getTypeParameters();
        return ps == null ? 0 : ps.getNumChildren();
    }
}
