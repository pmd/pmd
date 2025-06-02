/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute.NoAttrScope;
import net.sourceforge.pmd.util.AssertionUtil;


/**
 * Explores an AST node reflectively to iterate over its XPath
 * attributes. This is the default way the attributes of a node
 * are made accessible to XPath rules, and defines an important
 * piece of PMD's XPath support.
 *
 * @see Node#getXPathAttributesIterator()
 */
public class AttributeAxisIterator implements Iterator<Attribute> {

    /** Caches the precomputed attribute accessors of a given class. */
    private static final ConcurrentMap<Class<?>, List<MethodWrapper>> METHOD_CACHE = new ConcurrentHashMap<>();

    /* Constants used to determine which methods are accessors */
    private static final Set<Class<?>> CONSIDERED_RETURN_TYPES
            = setOf(Integer.TYPE, Boolean.TYPE, Double.TYPE, String.class,
                    Long.TYPE, Character.TYPE, Float.TYPE, Chars.class);

    private static final Set<String> FILTERED_OUT_NAMES
        = setOf("toString",
                "getNumChildren",
                "getIndexInParent",
                "getParent",
                "getClass",
                "getSourceCodeFile",
                "isFindBoundary",
                "getRuleIndex",
                "getXPathNodeName",
                "altNumber",
                "toStringTree",
                "getTypeNameNode",
                "hashCode",
                "getImportedNameNode",
                "getScope");

    /* Iteration variables */
    private final Iterator<MethodWrapper> iterator;
    private final Node node;


    /**
     * Creates a new iterator that enumerates the attributes of the given node.
     * Note: if you want to access the attributes of a node, don't use this directly,
     * use instead the overridable {@link Node#getXPathAttributesIterator()}.
     */
    public AttributeAxisIterator(@NonNull Node contextNode) {
        this.node = contextNode;
        this.iterator = METHOD_CACHE.computeIfAbsent(contextNode.getClass(), this::getWrappersForClass).iterator();
    }

    private List<MethodWrapper> getWrappersForClass(Class<?> nodeClass) {
        return Arrays.stream(nodeClass.getMethods())
                     .filter(m -> isAttributeAccessor(nodeClass, m))
                     .map(m -> {
                         try {
                             return new MethodWrapper(m);
                         } catch (ReflectiveOperationException e) {
                             throw AssertionUtil.shouldNotReachHere("Method '" + m + "' should be accessible, but: " + e, e);
                         }
                     })
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
            // Methods of package-private classes are not accessible.
            && Modifier.isPublic(method.getModifiers())
            && !isIgnored(nodeClass, method);
    }

    private boolean isConsideredReturnType(Method method) {
        Class<?> klass = method.getReturnType();
        if (CONSIDERED_RETURN_TYPES.contains(klass) || klass.isEnum()) {
            return true;
        }

        if (Collection.class.isAssignableFrom(klass)) {
            Type t = method.getGenericReturnType();
            if (t instanceof ParameterizedType) {
                try {
                    // ignore type variables, such as List<N>… we could check all bounds, but probably it's overkill
                    Type actualTypeArgument = ((ParameterizedType) t).getActualTypeArguments()[0];
                    if (!TypeVariable.class.isAssignableFrom(actualTypeArgument.getClass())) {
                        Class<?> elementKlass = Class.forName(actualTypeArgument.getTypeName());
                        return CONSIDERED_RETURN_TYPES.contains(elementKlass) || elementKlass.isEnum();
                    }
                } catch (ClassNotFoundException e) {
                    throw AssertionUtil.shouldNotReachHere("Method '" + method + "' should return a known type, but: " + e, e);
                }
            }
        }

        return false;
    }

    private boolean isIgnored(Class<?> nodeClass, Method method) {
        Class<?> declaration = method.getDeclaringClass();
        if (method.isAnnotationPresent(NoAttribute.class)) {
            return true;
        } else if (declaration == Node.class || declaration == AbstractNode.class) {
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
        } else if (!declaration.equals(nodeClass) || method.isBridge()) {
            // Bridge methods appear declared in the subclass but represent
            // an inherited method.

            // Then the node suppressed the attributes of its parent
            return localAnnot.scope() == NoAttrScope.INHERITED;
        } else {
            // then declaration == nodeClass so we need the scope to be ALL
            return localAnnot.scope() == NoAttrScope.ALL;

        }
    }


    @Override
    public Attribute next() {
        MethodWrapper m = iterator.next();
        return new Attribute(node, m.name, m.methodHandle, m.method);
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
        static final Lookup LOOKUP = MethodHandles.publicLookup();
        private static final MethodType GETTER_TYPE = MethodType.methodType(Object.class, Node.class);
        public final MethodHandle methodHandle;
        public final Method method;
        public final String name;


        MethodWrapper(Method m) throws IllegalAccessException {
            this.method = m;
            this.name = truncateMethodName(m.getName());
            // Note: We only support public methods on public types. If the method being called is implemented
            // in a package-private class, this won't work.
            // See git history here and https://github.com/pmd/pmd/issues/4885
            this.methodHandle = LOOKUP.unreflect(m).asType(GETTER_TYPE);
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
            } else if ("length".equals(n)) {
                return "Length";
            }

            return n;
        }
    }
}
