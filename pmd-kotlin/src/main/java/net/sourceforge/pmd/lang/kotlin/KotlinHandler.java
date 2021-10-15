/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.BaseContextNodeTestFun;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;


public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    private final String kotlinRelease;

    private static final XPathHandler XPATH_HANDLER =
        XPathHandler.getHandlerForFunctionDefs(
            BaseContextNodeTestFun.HAS_CHILDREN
        );

    public KotlinHandler(String release) {
        kotlinRelease = release;
        // check language version?
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public Parser getParser() {
        return new PmdKotlinParser();
    }

    public String getKotlinRelease() {
        return kotlinRelease;
    }
}
