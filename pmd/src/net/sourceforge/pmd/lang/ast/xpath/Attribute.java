/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author daniels
 */
public class Attribute {

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
	// this lazy loading reduces calls to Method.invoke() by about 90%
	try {
	    return method.invoke(parent, EMPTY_OBJ_ARRAY);
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	} catch (InvocationTargetException ite) {
	    ite.printStackTrace();
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

    public String getName() {
	return name;
    }

    public Node getParent() {
	return parent;
    }

    public String toString() {
	return name + ":" + getValue() + ":" + parent;
    }
}
