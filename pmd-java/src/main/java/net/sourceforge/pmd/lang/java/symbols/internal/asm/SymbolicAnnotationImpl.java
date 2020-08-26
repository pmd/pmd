/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * An annotation parsed from a class file.
 */
final class SymbolicAnnotationImpl implements SymAnnot {

    /** Many annotations have no attributes so this can stay null in this case. */
    private @Nullable Map<String, SymbolicValue> attributes;
    private final boolean runtimeVisible;
    private final String typeBinaryName;

    // TODO would be nice to link back to the class symbol, to get default values
    SymbolicAnnotationImpl(boolean runtimeVisible, String descriptor) {
        this.runtimeVisible = runtimeVisible;

        // use a constant for this common values
        if ("Ljava/lang/Deprecated;".equals(descriptor)) {
            this.typeBinaryName = "java.lang.Deprecated";
        } else {
            this.typeBinaryName = ClassNamesUtil.classDescriptorToBinaryName(descriptor);
        }
    }

    void addAttribute(String name, SymbolicValue value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    @Override
    public boolean valueEquals(Object o) {
        return false; // TODO? look into lang3/AnnotationUtils for a template
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }

    @Override
    public Map<String, SymbolicValue> getExplicitAttributes() {
        return attributes == null ? Collections.emptyMap() : attributes;
    }

    @Override
    public RetentionPolicy getRetention() {
        return runtimeVisible ? RetentionPolicy.RUNTIME
                              : RetentionPolicy.CLASS;
    }

    @Override
    public boolean isOfType(String binaryName) {
        return typeBinaryName.equals(binaryName);
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
        if (!that.isOfType(typeBinaryName)) {
            return false;
        }
        return attributes == null
               ? that.getExplicitAttributes().isEmpty()
               : attributes.equals(that.getExplicitAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, typeBinaryName);
    }

    @Override
    public String toString() {
        return "@" + typeBinaryName + getExplicitAttributes();
    }
}
