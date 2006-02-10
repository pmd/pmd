/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.ast.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author daniels
 */
public class Attribute {

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private Node parent;
    private String name;
    private Method method;

    public Attribute(Node parent, String name, Method m) {
        this.parent = parent;
        this.name = name;
        this.method = m;
    }

    public String getValue() {
        // this lazy loading reduces calls to Method.invoke() by about 90%
        try {
            Object res = method.invoke(parent, EMPTY_OBJ_ARRAY);
            if (res != null) {
                if (res instanceof String) {
                    return (String) res;
                }
                return String.valueOf(res);
            }
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return "";
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
