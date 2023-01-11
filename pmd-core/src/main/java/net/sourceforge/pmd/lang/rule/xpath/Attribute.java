/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];

    private final Node parent;
    private final String name;
    private Method method;
    private List<?> value;
    private String stringValue;

    /** Creates a new attribute belonging to the given node using its accessor. */
    public Attribute(Node parent, String name, Method m) {
        this.parent = parent;
        this.name = name;
        this.method = m;
    }

    /** Creates a new attribute belonging to the given node using its string value. */
    public Attribute(Node parent, String name, String value) {
        this.parent = parent;
        this.name = name;
        this.value = Collections.singletonList(value);
        this.stringValue = value;
    }



    /**
     * Gets the generic type of the value of this attribute.
     */
    public Type getType() {
        return method == null ? String.class : method.getGenericReturnType();
    }

    public Class<?> getErasedType() {
        return method == null ? String.class : method.getReturnType();
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
        if (value != null) {
            return value.get(0);
        }

        // this lazy loading reduces calls to Method.invoke() by about 90%
        try {
            value = Collections.singletonList(method.invoke(parent, EMPTY_OBJ_ARRAY));
            return value.get(0);
        } catch (IllegalAccessException | InvocationTargetException iae) {
            iae.printStackTrace();
        }
        return null;
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
