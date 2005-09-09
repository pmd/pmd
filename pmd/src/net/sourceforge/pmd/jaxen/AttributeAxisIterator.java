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
import java.util.List;
import java.util.ArrayList;

// before any optimization, this took 22.9 seconds:
// time ./pmd.sh /usr/local/java/src/java/lang text basic > rpt.txt
// report size is 3167 bytes
// after caching and preprocessing, takes about 21.7 seconds
public class AttributeAxisIterator implements Iterator {

    private static class MethodWrapper {
        public Method method;
        public String name;
        public MethodWrapper(Method m) {
            this.method = m;
            this.name = truncateMethodName(m.getName());
        }
        private String truncateMethodName(String n) {
            if (n.startsWith("is")) {
                n = n.substring("is".length());
            } else if (n.startsWith("uses")) {
                n = n.substring("uses".length());
            } else if (n.startsWith("has")) {
                n = n.substring("has".length());
            } else if (n.startsWith("get")) {
                n = n.substring("get".length());
            }
            return n;
        }
    }

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private Object currObj;
    private MethodWrapper[] methodWrappers;
    private int position;
    private Node node;

    private static Map methodCache = new HashMap();

    public AttributeAxisIterator(Node contextNode) {
        this.node = contextNode;

        if (!methodCache.containsKey(contextNode.getClass())) {
            Method[] preFilter = contextNode.getClass().getMethods();
            List postFilter = new ArrayList();
            for (int i = 0; i<preFilter.length; i++) {
                if (isAttribute(preFilter[i])) {
                    Class returnType = preFilter[i].getReturnType();
                    if (String.class == returnType || Integer.TYPE == returnType || Boolean.TYPE == returnType) {
                        postFilter.add(new MethodWrapper(preFilter[i]));
                    }
                }
            }
            methodCache.put(contextNode.getClass(), (MethodWrapper[])postFilter.toArray(new MethodWrapper[postFilter.size()]));
        }
        this.methodWrappers = (MethodWrapper[])methodCache.get(contextNode.getClass());

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
        while (position < methodWrappers.length) {
            MethodWrapper methodWrapper = methodWrappers[position];
            try {
                Attribute attribute = getAttribute(node, methodWrapper);
                if (attribute != null) {
                    return attribute;
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

    protected Attribute getAttribute(Node node, MethodWrapper methodWrapper)
            throws IllegalAccessException, InvocationTargetException {
        Object value = methodWrapper.method.invoke(node, EMPTY_OBJ_ARRAY);
        if (value != null) {
            if (value instanceof String) {
                return new Attribute(node, methodWrapper.name, (String) value);
            } else {
                return new Attribute(node, methodWrapper.name, String.valueOf(value));
            }
        } else {
            return null;
        }
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