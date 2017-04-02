/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sourceforge.pmd.lang.ast.Node;

public class AttributeAxisIterator implements Iterator<Attribute> {

    private static class MethodWrapper {
        public Method method;
        public String name;

        MethodWrapper(Method m) {
            this.method = m;
            this.name = truncateMethodName(m.getName());
        }

        private String truncateMethodName(String n) {
            // about 70% of the methods start with 'get', so this case goes
            // first
            if (n.startsWith("get")) {
                return n.substring("get".length());
            }
            if (n.startsWith("is")) {
                return n.substring("is".length());
            }
            if (n.startsWith("has")) {
                return n.substring("has".length());
            }
            if (n.startsWith("uses")) {
                return n.substring("uses".length());
            }

            return n;
        }
    }

    private Attribute currObj;
    private MethodWrapper[] methodWrappers;
    private int position;
    private Node node;

    private static ConcurrentMap<Class<?>, MethodWrapper[]> methodCache =
            new ConcurrentHashMap<Class<?>, MethodWrapper[]>();

    public AttributeAxisIterator(Node contextNode) {
        this.node = contextNode;
        if (!methodCache.containsKey(contextNode.getClass())) {
            Method[] preFilter = contextNode.getClass().getMethods();
            List<MethodWrapper> postFilter = new ArrayList<>();
            for (Method element : preFilter) {
                if (isAttributeAccessor(element)) {
                    postFilter.add(new MethodWrapper(element));
                }
            }
            methodCache.putIfAbsent(contextNode.getClass(), postFilter.toArray(new MethodWrapper[postFilter.size()]));
        }
        this.methodWrappers = methodCache.get(contextNode.getClass());

        this.position = 0;
        this.currObj = getNextAttribute();
    }

    @Override
    public Attribute next() {
        if (currObj == null) {
            throw new IndexOutOfBoundsException();
        }
        Attribute ret = currObj;
        currObj = getNextAttribute();
        return ret;
    }

    @Override
    public boolean hasNext() {
        return currObj != null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Attribute getNextAttribute() {
        if (methodWrappers == null || position == methodWrappers.length) {
            return null;
        }
        MethodWrapper m = methodWrappers[position++];
        return new Attribute(node, m.name, m.method);
    }

    protected boolean isAttributeAccessor(Method method) {

        String methodName = method.getName();
        boolean deprecated = method.getAnnotation(Deprecated.class) != null;

        return !deprecated
                && (Integer.TYPE == method.getReturnType() || Boolean.TYPE == method.getReturnType()
                        || Double.TYPE == method.getReturnType() || String.class == method.getReturnType())
                && method.getParameterTypes().length == 0 && Void.TYPE != method.getReturnType()
                && !methodName.startsWith("jjt") && !"toString".equals(methodName) && !"getScope".equals(methodName)
                && !"getClass".equals(methodName) && !"getTypeNameNode".equals(methodName)
                && !"getImportedNameNode".equals(methodName) && !"hashCode".equals(methodName);
    }
}
