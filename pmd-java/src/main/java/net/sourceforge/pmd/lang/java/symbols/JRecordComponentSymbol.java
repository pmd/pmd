/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTRecordComponent;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * Represents a record component. Record components are associated
 * with a private final {@link JFieldSymbol} and a public
 * {@linkplain JMethodSymbol accessor method}.
 *
 * @since 7.3.0
 */
public interface JRecordComponentSymbol extends JAccessibleElementSymbol, BoundToNode<ASTRecordComponent> {

    /**
     * Record components use these modifiers by convention, although
     * they cannot have explicit modifiers in source, and
     * the associated field and method symbol have different
     * modifiers.
     */
    int RECORD_COMPONENT_MODIFIERS = Modifier.PUBLIC;


    /**
     * Returns the type of this value, under the given substitution.
     */
    JTypeMirror getTypeMirror(Substitution substitution);


    @Override
    default int getModifiers() {
        return RECORD_COMPONENT_MODIFIERS;
    }


    @Override
    @NonNull
    JClassSymbol getEnclosingClass();


    @Override
    default @NonNull String getPackageName() {
        return getEnclosingClass().getPackageName();
    }


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitRecordComponent(this, param);
    }
}
