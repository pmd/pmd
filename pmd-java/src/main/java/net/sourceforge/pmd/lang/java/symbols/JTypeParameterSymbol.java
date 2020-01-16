/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;


/**
 * Represents the declaration of a type variable, ie a type parameter. Type variables are reference
 * types, but not class or interface types. They're also not declared with the same node. For those
 * reasons this type of references is distinct from {@link JClassSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JTypeParameterSymbol extends JTypeDeclSymbol, BoundToNode<ASTTypeParameter> {


    /**
     * Returns the {@link JClassSymbol} or {@link JMethodSymbol} which declared
     * this type parameter.
     */
    JTypeParameterOwnerSymbol getDeclaringSymbol();


    @Override
    @NonNull
    default String getPackageName() {
        return getDeclaringSymbol().getPackageName();
    }


    @Override
    default int getModifiers() {
        return getDeclaringSymbol().getModifiers() | Modifier.ABSTRACT | Modifier.FINAL;
    }


    @Override
    @Nullable
    default JClassSymbol getEnclosingClass() {
        JTypeParameterOwnerSymbol ownerSymbol = getDeclaringSymbol();
        return ownerSymbol instanceof JClassSymbol ? (JClassSymbol) ownerSymbol : ownerSymbol.getEnclosingClass();
    }
}
