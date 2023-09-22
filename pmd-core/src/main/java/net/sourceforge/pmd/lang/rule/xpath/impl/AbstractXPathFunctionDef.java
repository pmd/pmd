/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import net.sourceforge.pmd.lang.Language;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;


/**
 * Base impl for an XPath function definition.
 * This uses Saxon API.
 *
 * @since 7.0.0
 */
public abstract class AbstractXPathFunctionDef extends ExtensionFunctionDefinition {

    private static final String PMD_URI_PREFIX = "http://pmd.sourceforge.net/";
    private final StructuredQName qname;

    private AbstractXPathFunctionDef(String localName, String namespacePrefix, String uri) {
        this.qname = new StructuredQName(namespacePrefix, uri, localName);
    }

    protected AbstractXPathFunctionDef(String localName) {
        this(localName, "pmd", PMD_URI_PREFIX + "pmd-core");
    }

    protected AbstractXPathFunctionDef(String localName, Language language) {
        this(localName, "pmd-" + language.getId(), PMD_URI_PREFIX + "pmd-" + language.getId());
    }

    @Override
    public final StructuredQName getFunctionQName() {
        return qname;
    }
}
