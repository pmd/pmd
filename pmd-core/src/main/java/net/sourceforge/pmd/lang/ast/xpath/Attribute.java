/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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


    private static final Logger LOG = Logger.getLogger(Attribute.class.getName());
    private static final ConcurrentMap<String, Boolean> DETECTED_DEPRECATED_ATTRIBUTES = new ConcurrentHashMap<>();

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];

    private final Node parent;
    private final String name;
    private Method method;
    private Object value;
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
        this.value = value;
        this.stringValue = value;
    }


    public String getName() {
        return name;
    }


    public Node getParent() {
        return parent;
    }

    public Object getValue() {
        if (value != null) {
            return value;
        }

        if (method.isAnnotationPresent(Deprecated.class) && LOG.isLoggable(Level.WARNING)
                && DETECTED_DEPRECATED_ATTRIBUTES.putIfAbsent(getLoggableAttributeName(), Boolean.TRUE) == null) {
            // this message needs to be kept in sync with PMDCoverageTest
            LOG.warning("Use of deprecated attribute '" + getLoggableAttributeName() + "' in XPath query");
        }

        // this lazy loading reduces calls to Method.invoke() by about 90%
        try {
            value = method.invoke(parent, EMPTY_OBJ_ARRAY);
            return value;
        } catch (IllegalAccessException | InvocationTargetException iae) {
            iae.printStackTrace();
        }
        return null;
    }

    public String getStringValue() {
        if (stringValue != null) {
            return stringValue;
        }
        Object v = this.value;
        if (this.value == null) {
            v = getValue();
        }
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


    private String getLoggableAttributeName() {
        return parent.getXPathNodeName() + "/@" + name;
    }

    @Override
    public String toString() {
        return name + ':' + getValue() + ':' + parent.getXPathNodeName();
    }
}
