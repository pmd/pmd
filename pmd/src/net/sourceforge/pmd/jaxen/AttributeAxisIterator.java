/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.jaxen;
import net.sourceforge.pmd.ast.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;


public class AttributeAxisIterator implements Iterator {

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private Object currObj;
    private Method[] methods;
    private int position;
    private Node node;
    
    public AttributeAxisIterator(Node contextNode) {
        this.node = contextNode;
        this.methods = contextNode.getClass().getMethods();
        this.position = 0;
        this.currObj = getNextAttribute();
    }
    
    public Object next() {
        if(currObj == null) {
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
        String name = method.getName();
        name = truncateMethodName(name);
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
        String name = method.getName();
        Class returnType = method.getReturnType();
        return (method.getParameterTypes().length == 0)
            && (Void.TYPE != returnType)
            && !name.startsWith("jjt")
            && !name.equals("toString")
            && !name.equals("getScope")
            && !name.equals("getClass")
            && !name.equals("getFinallyBlock")
            && !name.equals("getCatchBlocks")
            && !name.equals("getTypeNameNode")
            && !name.equals("getImportedNameNode")
            && !name.equals("hashCode");
    }
}