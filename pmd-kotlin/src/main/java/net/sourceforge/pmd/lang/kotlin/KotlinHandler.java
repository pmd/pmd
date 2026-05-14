/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;


public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    // Holder-class pattern: defers initialization until getXPathHandler() is first called,
    // by which time KotlinLanguageModule is fully registered in LanguageRegistry.
    // XPath functions (hasAnnotation, hasImport, etc.) are registered in a follow-up PR.
    private static final class XPathHandlerHolder {
        static final XPathHandler HANDLER = XPathHandler.noFunctionDefinitions();
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPathHandlerHolder.HANDLER;
    }

    @Override
    public Parser getParser() {
        return new PmdKotlinParser();
    }
}
