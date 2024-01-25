/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import javax.xml.namespace.QName;

import net.sourceforge.pmd.lang.Language;

import net.sf.saxon.lib.ExtensionFunctionCall;


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

    public Type[] getArgumentTypes() {
        return new Type[0];
    }

    public abstract Type getResultType(Type[] suppliedArgumentTypes);

    /**
     * If the function depends on the context item or the default XPath namespace, then
     * this method should return {@code true}.
     */
    public boolean dependsOnContext() {
        return false;
    }

    public abstract ExtensionFunctionCall makeCallExpression();

    public enum Type {
        SINGLE_STRING,
        SINGLE_BOOLEAN,
        SINGLE_INTEGER,
        SINGLE_ELEMENT,
        STRING_SEQUENCE, OPTIONAL_STRING, OPTIONAL_DECIMAL,
    }
}
