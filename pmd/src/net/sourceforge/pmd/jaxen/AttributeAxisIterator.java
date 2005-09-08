/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.ast.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

// before any optimization, this took 22.9 seconds:
// time ./pmd.sh /usr/local/java/src/java/lang text basic > rpt.txt
// report size is 3167 bytes
public class AttributeAxisIterator implements Iterator {

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private Object currObj;
    private Method[] methods;
    private int position;
    private Node node;

    private static Map methodCache = new HashMap();

    public AttributeAxisIterator(Node contextNode) {
        this.node = contextNode;

        // caching the methods is almost 10x faster than
        // looking them up each time
        if (!methodCache.containsKey(contextNode.getClass())) {
            methodCache.put(contextNode.getClass(), contextNode.getClass().getMethods());
        }
        this.methods = (Method[])methodCache.get(contextNode.getClass());

        this.position = 0;
        this.currObj = getNextAttribute();
    }

    public Object next() {
        if (currObj == null) {
            throw new IndexOutOfBoundsException();
        }
        Object ret = currObj;
        currObj = getNextAttribute();
        return ret;
    }

    public boolean hasNext() {
        return currObj != null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Attribute getNextAttribute() {
        while (position < methods.length) {
            Method method = methods[position];
            try {
                if (isAttribute(method)) {
                    Class returnType = method.getReturnType();
                    if (Boolean.TYPE == returnType
                            || String.class == returnType
                            || Integer.TYPE == returnType) {
                        Attribute attribute = getAttribute(node, method);
                        if (attribute != null) {
                            return attribute;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                position++;
            }
        }
        return null;
    }

    protected Attribute getAttribute(Node node, Method method)
            throws IllegalAccessException, InvocationTargetException {
        String name = truncateMethodName(method.getName());
        Object value = method.invoke(node, EMPTY_OBJ_ARRAY);
        if (value != null) {
            if (value instanceof String) {
                return new Attribute(node, name, (String) value);
            } else {
                return new Attribute(node, name, String.valueOf(value));
            }
        } else {
            return null;
        }
    }

    protected String truncateMethodName(String name) {
        if (name.startsWith("is")) {
            name = name.substring("is".length());
        } else if (name.startsWith("uses")) {
            name = name.substring("uses".length());
        } else if (name.startsWith("has")) {
            name = name.substring("has".length());
        } else if (name.startsWith("get")) {
            name = name.substring("get".length());
        }
        return name;
    }

    protected boolean isAttribute(Method method) {
        return (method.getParameterTypes().length == 0)
                && (Void.TYPE != method.getReturnType())
                && !method.getName().startsWith("jjt")
                && !method.getName().equals("toString")
                && !method.getName().equals("getScope")
                && !method.getName().equals("getClass")
                && !method.getName().equals("getFinallyBlock")
                && !method.getName().equals("getCatchBlocks")
                && !method.getName().equals("getTypeNameNode")
                && !method.getName().equals("getImportedNameNode")
                && !method.getName().equals("hashCode");
    }
}