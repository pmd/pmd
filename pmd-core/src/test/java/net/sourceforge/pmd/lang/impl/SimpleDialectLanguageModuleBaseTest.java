package net.sourceforge.pmd.lang.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageDialectModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.properties.PropertyDescriptor;

class SimpleDialectLanguageModuleBaseTest {

    @Test
    void baseLanguageXPathFunctionAvailable() throws Exception {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        try (LanguageProcessor baseProcessor = lang.createProcessor(lang.newPropertyBundle());
                LanguageProcessor dialectProcessor = dialect.createProcessor(dialect.newPropertyBundle())) {

            Set<XPathFunctionDefinition> dialectFunctions = dialectProcessor.services().getXPathHandler().getRegisteredExtensionFunctions();
            for (XPathFunctionDefinition fn : baseProcessor.services().getXPathHandler().getRegisteredExtensionFunctions()) {
                assertTrue(dialectFunctions.contains(fn),
                        "The function " + fn.getQName() + " is not available in the dialect.");
            }
        }
    }

    @Test
    void dialectSpecificXPathFunctionAvailable() throws Exception {
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        try (LanguageProcessor dialectProcessor = dialect.createProcessor(dialect.newPropertyBundle())) {
            Set<XPathFunctionDefinition> dialectFunctions = dialectProcessor.services().getXPathHandler().getRegisteredExtensionFunctions();

            XPathFunctionDefinition dummyDialectFunction = DummyLanguageDialectModule.dummyDialectFunction();
            assertTrue(dialectFunctions.contains(dummyDialectFunction),
                    "The function " + dummyDialectFunction.getQName() + " is not available in the dialect.");
        }
    }

    @Test
    void baseLanguagePropertiesAreAvailable() {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        LanguagePropertyBundle languagePropertyBundle = lang.newPropertyBundle();
        LanguagePropertyBundle dialectPropertyBundle = dialect.newPropertyBundle();

        for (PropertyDescriptor<?> pd : languagePropertyBundle.getPropertyDescriptors()) {
            assertTrue(dialectPropertyBundle.hasDescriptor(pd),
                    "The property " + pd.name() + " is not available in the dialect.");
        }
    }

    @Test
    void dialectSpecificPropertiesAreAvailable() {
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        LanguagePropertyBundle dialectPropertyBundle = dialect.newPropertyBundle();
        assertTrue(dialectPropertyBundle.hasDescriptor(DummyLanguageDialectModule.DUMMY_DIALECT_PROP),
                "The property " + DummyLanguageDialectModule.DUMMY_DIALECT_PROP.name() + " is not available in the dialect.");
    }

    @Test
    void baseLanguageMetricsAreAvailable() throws Exception {
        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        try (LanguageProcessor baseProcessor = lang.createProcessor(lang.newPropertyBundle());
                LanguageProcessor dialectProcessor = dialect.createProcessor(dialect.newPropertyBundle())) {
            for (Metric<?, ?> metric : baseProcessor.services().getLanguageMetricsProvider().getMetrics()) {
                assertNotNull(dialectProcessor.services().getLanguageMetricsProvider().getMetricWithName(metric.displayName()),
                        "The metric " + metric.displayName() + " is not available in the dialect.");
            }
        }
    }

    @Test
    void dialectSpecificMetricsAreAvailable() throws Exception {
        DummyLanguageDialectModule dialect = DummyLanguageDialectModule.getInstance();

        try (LanguageProcessor dialectProcessor = dialect.createProcessor(dialect.newPropertyBundle())) {
            assertTrue(dialectProcessor.services().getLanguageMetricsProvider().getMetrics().contains(DummyLanguageDialectModule.DUMMY_DIALECT_METRIC),
                    "The metric " + DummyLanguageDialectModule.DUMMY_DIALECT_METRIC.displayName() + "is not available in the dialect");
        }
    }
}
