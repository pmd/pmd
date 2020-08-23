/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

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

    protected AbstractXPathFunctionDef(String localName, String languageTerseName) {
        String namespacePrefix = "pmd-" + languageTerseName;
        String uri = PMD_URI_PREFIX + namespacePrefix;
        this.qname = new StructuredQName(namespacePrefix, uri, localName);
    }

    @Override
    public final StructuredQName getFunctionQName() {
        return qname;
    }
}
