/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute.NoAttrScope;


/**
 * Explores an AST node reflectively to iterate over its XPath
 * attributes. This is the default way the attributes of a node
 * are made accessible to XPath rules, and defines an important
 * piece of PMD's XPath support.
 */
public class AttributeAxisIterator implements Iterator<Attribute> {

    /** Caches the precomputed attribute accessors of a given class. */
    private static final ConcurrentMap<Class<?>, List<MethodWrapper>> METHOD_CACHE = new ConcurrentHashMap<>();

    /* Constants used to determine which methods are accessors */
    private static final Set<Class<?>> CONSIDERED_RETURN_TYPES
            = new HashSet<>(Arrays.<Class<?>>asList(Integer.TYPE, Boolean.TYPE, Double.TYPE, String.class,
                    Long.TYPE, Character.TYPE, Float.TYPE));

    private static final Set<String> FILTERED_OUT_NAMES
        = new HashSet<>(Arrays.asList("toString", "getNumChildren", "getIndexInParent", "getParent", "getClass", "getRuleIndex", "getXPathNodeName", "altNumber", "toStringTree", "getTypeNameNode", "hashCode", "getImportedNameNode", "getScope"));

    /* Iteration variables */
    private final Iterator<MethodWrapper> iterator;
    private final Node node;


    /**
     * Creates a new iterator that enumerates the attributes of the given node.
     * Note: if you want to access the attributes of a node, don't use this directly,
     * use instead the overridable {@link Node#getXPathAttributesIterator()}.
     */
    public AttributeAxisIterator(Node contextNode) {
        this.node = contextNode;
        this.iterator = METHOD_CACHE.computeIfAbsent(contextNode.getClass(), this::getWrappersForClass).iterator();
    }

    private List<MethodWrapper> getWrappersForClass(Class<?> nodeClass) {
        return Arrays.stream(nodeClass.getMethods())
                     .filter(m -> isAttributeAccessor(nodeClass, m))
                     .map(MethodWrapper::new)
                     .collect(Collectors.toList());
    }

    /**
     * Returns whether the given method is an attribute accessor,
     * in which case a corresponding Attribute will be added to
     * the iterator.
     *
     * @param method The method to test
     */
    protected boolean isAttributeAccessor(Class<?> nodeClass, Method method) {
        String methodName = method.getName();

        return !methodName.startsWith("jjt")
            && !FILTERED_OUT_NAMES.contains(methodName)
            && method.getParameterTypes().length == 0
            && isConsideredReturnType(method)
            // filter out methods declared in supertypes like the
            // Antlr ones, unless they're opted-in
            && Node.class.isAssignableFrom(method.getDeclaringClass())
            && !isIgnored(nodeClass, method);
    }

    private boolean isConsideredReturnType(Method method) {
        Class<?> klass = method.getReturnType();
        return CONSIDERED_RETURN_TYPES.contains(klass) || klass.isEnum();
    }

    private boolean isIgnored(Class<?> nodeClass, Method method) {
        Class<?> declaration = method.getDeclaringClass();
        if (method.isAnnotationPresent(NoAttribute.class)) {
            return true;
        } else if (declaration == Node.class || declaration == AbstractNode.class || declaration == AbstractNodeWithTextCoordinates.class) {
            // attributes from Node and AbstractNode are never suppressed
            // we don't know what might go wrong if we do suppress them
            return false;
        }

        NoAttribute declAnnot = declaration.getAnnotation(NoAttribute.class);

        if (declAnnot != null && declAnnot.scope() == NoAttrScope.ALL) {
            // then the parent didn't want children to inherit the attr
            return true;
        }

        // we don't care about the parent annotation in the following

        NoAttribute localAnnot = nodeClass.getAnnotation(NoAttribute.class);

        if (localAnnot == null) {
            return false;
        } else if (!declaration.equals(nodeClass)) {
            // then the node suppressed the attributes of its parent
            return localAnnot.scope() == NoAttrScope.INHERITED;
        } else {
            // then declaration == nodeClass so we need the scope to be ALL
            return localAnnot.scope() == NoAttrScope.ALL;

        }
    }


    @Override
    public Attribute next() {
        MethodWrapper m = iterator.next();
        return new Attribute(node, m.name, m.method);
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }


    /**
     * Associates an attribute accessor with the XPath-accessible
     * name of the attribute. This is used to avoid recomputing
     * the name of the attribute for each attribute (it's only done
     * once and put inside the {@link #METHOD_CACHE}).
     */
    private static class MethodWrapper {
        public Method method;
        public String name;


        MethodWrapper(Method m) {
            this.method = m;
            this.name = truncateMethodName(m.getName());
        }


        /**
         * This method produces the actual XPath name of an attribute
         * from the name of its accessor.
         */
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
            if ("size".equals(n)) {
                return "Size";
            }

            return n;
        }
    }
}
