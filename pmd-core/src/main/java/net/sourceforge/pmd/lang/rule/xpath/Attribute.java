/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Represents an XPath attribute of a specific node.
 * Attributes know their name, the node they wrap,
 * and have access to their value.
 *
 * <p>Two attributes are equal if they have the same name
 * and their parent nodes are equal.
 *
 * @author daniels
 */
public class Attribute {
    private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);

    private final Node parent;
    private final String name;

    private final MethodHandle handle;
    private final Method method;
    private boolean invoked;

    private Object value;
    private String stringValue;

    /** Creates a new attribute belonging to the given node using its accessor. */
    public Attribute(Node parent, String name, MethodHandle handle, Method m) {
        this.parent = parent;
        this.name = name;
        this.handle = handle;
        this.method = m;
    }

    /** Creates a new attribute belonging to the given node using its string value. */
    public Attribute(Node parent, String name, String value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.handle = null;
        this.method = null;
        this.stringValue = value;
        this.invoked = true;
    }



    /**
     * Gets the generic type of the value of this attribute.
     */
    public Type getType() {
        return method == null ? String.class : method.getGenericReturnType();
    }

    public String getName() {
        return name;
    }


    public Node getParent() {
        return parent;
    }

    /**
     * Returns null for "not deprecated", empty string for "deprecated without replacement",
     * otherwise name of replacement attribute.
     */
    @InternalApi
    public String replacementIfDeprecated() {
        if (method == null) {
            return null;
        } else {
            DeprecatedAttribute annot = method.getAnnotation(DeprecatedAttribute.class);
            String result = annot != null
                   ? annot.replaceWith()
                   : method.isAnnotationPresent(Deprecated.class)
                     ? DeprecatedAttribute.NO_REPLACEMENT
                     : null;
            if (result == null && List.class.isAssignableFrom(method.getReturnType())) {
                // Lists are generally deprecated, see #2451
                result = DeprecatedAttribute.NO_REPLACEMENT;
            }
            return result;
        }
    }

    public boolean isDeprecated() {
        return replacementIfDeprecated() != null;
    }

    public Object getValue() {
        if (this.invoked) {
            return this.value;
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

    public String getStringValue() {
        if (stringValue != null) {
            return stringValue;
        }
        Object v = getValue();

        stringValue = v == null ? "" : String.valueOf(v);
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
        return Objects.hash(parent, name);
    }

    @Override
    public String toString() {
        return name + ':' + getValue() + ':' + parent.getXPathNodeName();
    }
}
