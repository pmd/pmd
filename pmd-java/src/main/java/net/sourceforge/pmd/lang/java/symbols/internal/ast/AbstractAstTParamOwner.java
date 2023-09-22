/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.TypeParamOwnerNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeVar;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstTParamOwner<T extends TypeParamOwnerNode & AccessNode>
    extends AbstractAstAnnotableSym<T> implements JTypeParameterOwnerSymbol {

    private final List<JTypeVar> tparams;
    private final int modifiers;

    AbstractAstTParamOwner(T node, AstSymFactory factory) {
        super(node, factory);
        this.modifiers = JModifier.toReflect(node.getModifiers().getEffectiveModifiers());
        this.tparams = Collections.unmodifiableList(map(
            ASTList.orEmpty(node.getTypeParameters()),
            it -> new AstTypeParamSym(it, factory, this).getTypeMirror()
        ));
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return tparams;
    }

    @Override
    public @NonNull String getPackageName() {
        return node.getRoot().getPackageName();
    }

}
