/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import javax.xml.namespace.QName;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base impl for an XPath function definition.
 *
 * @since 7.0.0
 */
public abstract class XPathFunctionDefinition {

    private static final String PMD_URI_PREFIX = "http://pmd.sourceforge.net/";
    private final QName qname;

    private XPathFunctionDefinition(String localName, String namespacePrefix, String uri) {
        this.qname = new QName(uri, localName, namespacePrefix);
    }

    protected XPathFunctionDefinition(String localName) {
        this(localName, "pmd", PMD_URI_PREFIX + "pmd-core");
    }

    protected XPathFunctionDefinition(String localName, Language language) {
        this(localName, "pmd-" + language.getId(), PMD_URI_PREFIX + "pmd-" + language.getId());
    }

    public final QName getQName() {
        return qname;
    }

    /**
     * Defines the types of the function arguments. By default, an empty array is returned, indicating
     * that the function takes no arguments.
     */
    public Type[] getArgumentTypes() {
        return new Type[0];
    }

    /**
     * Defines the return type of the function.
     */
    public abstract Type getResultType();

    /**
     * If the function depends on the context item, then
     * this method should return {@code true}.
     *
     * <p>Note: Only if this is true, the contextNode parameter will be present in the
     * {@link FunctionCall#call(Node, Object[])} method.
     */
    public boolean dependsOnContext() {
        return false;
    }

    /**
     * Create a call on this function. This method is called, when a function call
     * is found in the XPath expression.
     */
    public abstract FunctionCall makeCallExpression();

    /**
     * Supported types of a custom XPath function. These can be used as {@link #getResultType() result types}
     * or {@link #getArgumentTypes() argument types}.
     */
    public enum Type {
        /** Represents {@link String}. */
        SINGLE_STRING,
        /** Represents {@link Boolean}. */
        SINGLE_BOOLEAN,
        /** Represents {@link Integer}. */
        SINGLE_INTEGER,
        /** Represents any node. Usually used as an argument type. */
        SINGLE_ELEMENT,
        /** Represents a {@link java.util.List} of {@link String}, potentially empty. */
        STRING_SEQUENCE,
        /** Represents a {@link java.util.Optional} {@link String}. */
        OPTIONAL_STRING,
        /** Represents a {@link java.util.Optional} {@link Double}. */
        OPTIONAL_DECIMAL,
    }

    /**
     * Provides the actual implementation of a custom XPath function.
     */
    public interface FunctionCall {
        /**
         * This method is called at runtime to evaluate the XPath function expression.
         *
         * @param contextNode the context node or {@code null}, if this function doesn't depend on the context.
         *                    See {@link XPathFunctionDefinition#dependsOnContext()}.
         * @param arguments The arguments converted as the corresponding java types.
         *                  See {@link XPathFunctionDefinition#getArgumentTypes()}.
         * @return The result of the function. This should be the corresponding java type of
         * {@link XPathFunctionDefinition#getResultType()}.
         * @throws XPathFunctionException when any problem during evaluation occurs, like invalid arguments.
         */
        Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException;

        /**
         * This is called once before the function is evaluated. It can be used to optimize the
         * implementation by doing expensive operations only once and cache the result.
         * This is useful, if the argument of the function is of type {@link String} and is provided
         * as a String literal in the XPath expression.
         *
         * <p>This is an optional step. The default implementation does nothing.
         *
         * @param arguments The arguments converted as the corresponding java types.
         *                  See {@link XPathFunctionDefinition#getArgumentTypes()}.
         *                  Note: This array might contain {@code null} elements, if the values are
         *                  not known yet because they are dynamic. Only literal values are available.
         * @throws XPathFunctionException when any problem during initialization occurs, like invalid arguments.
         */
        default void staticInit(Object[] arguments) throws XPathFunctionException {
            // default implementation does nothing
        }
    }
}
