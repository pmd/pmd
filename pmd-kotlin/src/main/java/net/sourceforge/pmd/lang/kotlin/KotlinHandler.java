/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinHasAnnotationFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinHasUnresolvedReferenceFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinInsideLoopFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinIsNullableFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinMatchesSigFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinModifiersFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinNodeTextFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeIsExactlyFunction;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeIsFunction;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;


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
                        KotlinModifiersFunction.INSTANCE,
                        KotlinNodeTextFunction.INSTANCE);
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
