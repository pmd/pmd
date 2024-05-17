/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Represents an XPath attribute of a specific node.
 * Attributes know their name, the node they wrap,
 * and have access to their value.
 *
 * <p>Two attributes are equal if they have the same name
 * and their parent nodes are equal.
 *
 * <p>Note that attributes do not support just any type, but
 * a restricted set of value types that can be mapped to XPath types.
 * The exact supported types are not specified, but include at
 * least Java primitives and String.
 *
 * @see Node#getXPathAttributesIterator()
 */
public final class Attribute {
    private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);

    private final @NonNull Node parent;
    private final @NonNull String name;

    private final @Nullable MethodHandle handle;
    private final @Nullable Method method;
    /** If true, we won't invoke the method handle again. */
    private boolean invoked;

    /** May be null after invocation too. */
    private @Nullable Object value;

    /** Must be non-null after {@link #getStringValue()} has been invoked. */
    private String stringValue;

    /**
     * Creates a new attribute belonging to the given node using its accessor.
     *
     * @param handle A method handle, used to fetch the attribute.
     * @param method The method corresponding to the method handle. This
     *               is used to perform reflective queries, eg to
     *               find annotations on the attribute getter, but only
     *               the method handle is ever invoked.
     */
    public Attribute(@NonNull Node parent, @NonNull String name, @NonNull MethodHandle handle, @NonNull Method method) {
        this.parent = Objects.requireNonNull(parent);
        this.name = Objects.requireNonNull(name);
        this.handle = Objects.requireNonNull(handle);
        this.method = Objects.requireNonNull(method);
    }

    /** Creates a new attribute belonging to the given node using its string value. */
    public Attribute(@NonNull Node parent, @NonNull String name, @Nullable String value) {
        this.parent = Objects.requireNonNull(parent);
        this.name = Objects.requireNonNull(name);
        this.value = value;
        this.handle = null;
        this.method = null;
        this.stringValue = value == null ? "" : value;
        this.invoked = true;
    }


    /**
     * Gets the generic type of the value of this attribute.
     */
    public Type getType() {
        return method == null ? String.class : method.getGenericReturnType();
    }

    /** Return the name of the attribute (without leading @ sign). */
    public @NonNull String getName() {
        return name;
    }


    /** Return the node that owns this attribute. */
    public @NonNull Node getParent() {
        return parent;
    }

    /**
     * Returns null for "not deprecated", empty string for "deprecated without replacement",
     * otherwise name of replacement attribute.
     *
     * @apiNote Internal API
     */
    String replacementIfDeprecated() {
        if (method == null) {
            return null;
        } else {
            DeprecatedAttribute annot = method.getAnnotation(DeprecatedAttribute.class);
            return annot != null
                   ? annot.replaceWith()
                   : method.isAnnotationPresent(Deprecated.class)
                     ? DeprecatedAttribute.NO_REPLACEMENT
                     : null;
        }
    }

    /**
     * Return whether this attribute was deprecated. This is the case if the getter
     * has the annotation {@link Deprecated} or {@link DeprecatedAttribute}.
     */
    public boolean isDeprecated() {
        return replacementIfDeprecated() != null;
    }

    /**
     * Return the value of the attribute. This may return null. The getter
     * is invoked at most once.
     */
    public Object getValue() {
        if (this.invoked) {
            return this.value;
        } else if (handle == null) {
            throw new NullPointerException("Cannot fetch value of attribute with null getter! " + this);
        }

        Object value;
        // this lazy loading reduces calls to Method.invoke() by about 90%
        try {
            value = handle.invokeExact(parent);
        } catch (Throwable iae) { // NOPMD
            LOG.debug("Exception while fetching attribute value", iae);
            value = null;
        }
        this.value = value;
        this.invoked = true;
        return value;
    }

    /**
     * Return the string value of the attribute. If the getter returned null,
     * then return the empty string (which is a falsy value in XPath).
     * Otherwise, return a string representation of the value (e.g. with
     * {@link Object#toString()}, but this is not guaranteed).
     */
    public @NonNull String getStringValue() {
        if (stringValue != null) {
            return stringValue;
        }
        Object v = getValue();

        if (v == null) {
            stringValue = "";
        } else {
            stringValue = v.toString();
        }
        return stringValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        return Objects.equals(parent, attribute.parent)
            && Objects.equals(name, attribute.name);
    }


    @Override
    public int hashCode() {
        return parent.hashCode() * 31 + name.hashCode();
    }

    @Override
    public String toString() {
        return parent.getXPathNodeName() + "/@" + name + " = " + getValue();
    }
}
