/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author daniels
 */
public class Attribute {


    private static final Logger LOG = Logger.getLogger(Attribute.class.getName());
    private static final Set<String> DETECTED_DEPRECATED_ATTRIBUTES = new HashSet<>();


    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private Node parent;
    private String name;
    private Method method;
    private Object value;
    private String stringValue;

    public Attribute(Node parent, String name, Method m) {
        this.parent = parent;
        this.name = name;
        this.method = m;
    }

    public Attribute(Node parent, String name, String value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.stringValue = value;
    }

    public Object getValue() {
        if (value != null) {
            return value;
        }

        if (method.isAnnotationPresent(Deprecated.class) && LOG.isLoggable(Level.WARNING) && !DETECTED_DEPRECATED_ATTRIBUTES.contains(getLoggableAttributeName())) {
            DETECTED_DEPRECATED_ATTRIBUTES.add(getLoggableAttributeName());
            LOG.warning("Use of deprecated attribute '" + getLoggableAttributeName() + "' in xpath query");
        }

        // this lazy loading reduces calls to Method.invoke() by about 90%
        try {
            return method.invoke(parent, EMPTY_OBJ_ARRAY);
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
        if (v == null) {
            stringValue = "";
        } else {
            stringValue = String.valueOf(v);
        }
        return stringValue;
    }

    private String getLoggableAttributeName() {
        return parent.getXPathNodeName() + "/@" + name;
    }

    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name + ':' + getValue() + ':' + parent;
    }
}
