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
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinHasAnnotationFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinHasUnresolvedReferenceFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinInsideLoopFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinIsNullableFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinIsWithinDirectFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinIsWithinFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinMatchesSigFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinModifiersFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinNodeTextFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeIsExactlyFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeIsFunction;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    // Holder-class pattern: defers initialization until getXPathHandler() is first called,
    // by which time KotlinLanguageModule is fully registered in LanguageRegistry.
    private static final class XPathHandlerHolder {
        static final XPathHandler HANDLER =
                XPathHandler.getHandlerForFunctionDefs(
                        KotlinTypeIsFunction.INSTANCE,
                        KotlinTypeIsExactlyFunction.INSTANCE,
                        KotlinMatchesSigFunction.INSTANCE,
                        KotlinHasAnnotationFunction.INSTANCE,
                        KotlinHasUnresolvedReferenceFunction.INSTANCE,
                        KotlinInsideLoopFunction.INSTANCE,
                        KotlinIsNullableFunction.INSTANCE,
                        KotlinIsWithinFunction.INSTANCE,
                        KotlinIsWithinDirectFunction.INSTANCE,
                        KotlinModifiersFunction.INSTANCE,
                        KotlinNodeTextFunction.INSTANCE);
    }

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
        return XPathHandlerHolder.HANDLER;
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
