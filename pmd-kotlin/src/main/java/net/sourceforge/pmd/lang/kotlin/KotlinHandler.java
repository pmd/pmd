/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;
import net.sourceforge.pmd.lang.kotlin.internal.KotlinDesignerBindings;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;


public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    private static final XPathHandler XPATH_HANDLER = XPathHandler.noFunctionDefinitions();

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public Parser getParser() {
        return new PmdKotlinParser();
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return KotlinDesignerBindings.INSTANCE;
    }
}
