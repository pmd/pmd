/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * An annotation parsed from a class file.
 */
final class SymbolicAnnotationImpl implements SymAnnot {

    private final JClassSymbol typeStub;
    /** Many annotations have no attributes so this remains the singleton emptyMap in this case. */
    private @NonNull Map<String, SymbolicValue> attributes = Collections.emptyMap();
    private final boolean runtimeVisible;

    // TODO would be nice to link back to the class symbol, to get default values
    SymbolicAnnotationImpl(AsmSymbolResolver resolver, boolean runtimeVisible, String descriptor) {
        this.runtimeVisible = runtimeVisible;
        this.typeStub = resolver.resolveFromInternalNameCannotFail(ClassNamesUtil.classDescriptorToBinaryName(descriptor));
    }

    void addAttribute(String name, SymbolicValue value) {
        if (attributes.isEmpty()) {
            attributes = new HashMap<>(); // make it modifiable
        }
        attributes.put(name, value);
    }

    @Override
    public boolean valueEquals(Object o) {
        return false; // TODO? look into lang3/AnnotationUtils for a template
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public @Nullable SymbolicValue getAttributeOrDefault(String name) {
        SymbolicValue value = getAttribute(name);
        if (value != null) {
            return value;
        }
        if (!typeStub.isAnnotation()) {
            return null; // path taken for invalid stuff & unresolved classes
        }
        for (JMethodSymbol m : typeStub.getDeclaredMethods()) {
            if (m.getSimpleName().equals(name)
                && m.getArity() == 0
                && !m.isStatic()) {
                return m.getDefaultAnnotationValue(); // nullable
            }
        }
        return null;
    }

    @Override
    public Map<String, SymbolicValue> getExplicitAttributes() {
        return attributes;
    }

    @Override
    public RetentionPolicy getRetention() {
        return runtimeVisible ? RetentionPolicy.RUNTIME
                              : RetentionPolicy.CLASS;
    }

    @Override
    public boolean isOfType(String binaryName) {
        return typeStub.getBinaryName().equals(binaryName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymAnnot)) {
            return false;
        }
        SymAnnot that = (SymAnnot) o;
        if (!that.isOfType(typeStub.getBinaryName())) {
            return false;
        }
        return attributes.equals(that.getExplicitAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, typeStub);
    }

    @Override
    public String toString() {
        return "@" + typeStub + getExplicitAttributes();
    }
}
