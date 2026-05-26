/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.util.concurrent.ExecutorService;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;
import net.sourceforge.pmd.lang.kotlin.internal.KotlinDesignerBindings;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;


public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    private static final XPathHandler XPATH_HANDLER = XPathHandler.noFunctionDefinitions();

    private PmdKotlinParser parser;

    /**
     * @deprecated Since 7.25.0. Don't create this class directly, use {@link KotlinLanguageModule#getInstance()},
     *             {@link KotlinLanguageModule#createProcessor(LanguagePropertyBundle)},
     *             {@link KotlinLanguageProcessor#services()} instead.
     */
    @Deprecated
    public KotlinHandler() {
        // default constructor - needed for backwards compatibility
    }

    KotlinHandler(ExecutorService timeoutExecutor) {
        this.parser = InternalApiBridge.newPmdKotlinParser(timeoutExecutor);
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return KotlinDesignerBindings.INSTANCE;
    }
}
