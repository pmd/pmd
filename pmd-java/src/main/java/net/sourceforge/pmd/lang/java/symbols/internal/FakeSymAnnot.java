/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * Pretends to be an annotation with no explicit attributes.
 *
 * @author Clément Fournier
 */
public class FakeSymAnnot implements SymAnnot {

    private final JClassSymbol annotationClass;

    public FakeSymAnnot(JClassSymbol annotationClass) {
        this.annotationClass = annotationClass;
        assert annotationClass.isAnnotation() : "Not an annotation " + annotationClass;
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String attrName) {
        return annotationClass.getDefaultAnnotationAttributeValue(attrName);
    }

    @Override
    public @NonNull JClassSymbol getAnnotationSymbol() {
        return annotationClass;
    }

    @Override
    public String toString() {
        return SymbolToStrings.FAKE.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.ANNOTATION.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.ANNOTATION.hash(this);
    }
}
