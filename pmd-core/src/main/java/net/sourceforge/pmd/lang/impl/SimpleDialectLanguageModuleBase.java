/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.ViolationDecorator;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

/**
 * The simplest implementation of a dialect, where only a {@link LanguageMetadata}
 * needs to be implemented. Everything gets delegated to the base language,
 * with all dialect extension applied.
 *
 * @author Juan Martín Sotuyo Dodero
 * @since 7.13.0
 * @experimental Since 7.13.0. See <a href="https://github.com/pmd/pmd/pull/5438">[core] Support language dialects #5438</a>.
 */
@Experimental
public class SimpleDialectLanguageModuleBase extends LanguageModuleBase implements PmdCapableLanguage, CpdCapableLanguage {

    private final Function<LanguagePropertyBundle, BasePmdDialectLanguageVersionHandler> handler;

    protected SimpleDialectLanguageModuleBase(DialectLanguageMetadata metadata) {
        this(metadata, new BasePmdDialectLanguageVersionHandler());
    }

    protected SimpleDialectLanguageModuleBase(DialectLanguageMetadata metadata, BasePmdDialectLanguageVersionHandler handler) {
        this(metadata, p -> handler);
    }

    protected SimpleDialectLanguageModuleBase(DialectLanguageMetadata metadata, Function<LanguagePropertyBundle, BasePmdDialectLanguageVersionHandler> makeHandler) {
        super(metadata);
        assert getBaseLanguageId() != null : "Language " + getId() + " is not a dialect of another language.";

        this.handler = makeHandler;
    }

    private @NonNull Language getBaseLanguageFromRegistry(LanguageRegistry registry) {
        final Language baseLanguage = registry.getLanguageById(getBaseLanguageId());

        if (baseLanguage == null) {
            throw new IllegalStateException(
                    "Language " + getId() + " has unsatisfied dependencies: "
                            + getBaseLanguageId() + " is not found in " + registry
            );
        }

        return baseLanguage;
    }

    /**
     * Creates a combined property bundle with all properties from the dialect and the base language.
     * To define dialect-specific properties to be added to this bundle, override {@link #newDialectPropertyBundle()}
     * @return A new set of properties
     */
    @Override
    public final LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle baseBundle = getBaseLanguageFromRegistry(LanguageRegistry.PMD).newPropertyBundle();
        LanguagePropertyBundle dialectBundle = newDialectPropertyBundle();

        for (PropertyDescriptor<?> pd : baseBundle.getPropertyDescriptors()) {
            if (!dialectBundle.hasDescriptor(pd)) {
                dialectBundle.definePropertyDescriptor(pd);
            }
        }

        return dialectBundle;
    }

    protected @NonNull LanguagePropertyBundle newDialectPropertyBundle() {
        return new LanguagePropertyBundle(this);
    }

    @Override
    public final LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        final PmdCapableLanguage baseLanguage = (PmdCapableLanguage) getBaseLanguageFromRegistry(LanguageRegistry.PMD);
        final BasePmdDialectLanguageVersionHandler dialectHandler = handler.apply(bundle);

        return new DialectLanguageProcessor(baseLanguage, dialectHandler, bundle);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        final CpdCapableLanguage baseLanguage = (CpdCapableLanguage) getBaseLanguageFromRegistry(LanguageRegistry.CPD);
        return baseLanguage.createCpdLexer(bundle);
    }

    /**
     * A Language processor for dialects. It delegates everything to the base language, but extends
     * the {@link LanguageVersionHandler} with any dialect-specific options.
     */
    private static final class DialectLanguageProcessor extends BatchLanguageProcessor<LanguagePropertyBundle> {
        private final LanguageProcessor baseLanguageProcessor;
        private final LanguageVersionHandler combinedHandler;

        private DialectLanguageProcessor(PmdCapableLanguage baseLanguage, BasePmdDialectLanguageVersionHandler dialectHandler, LanguagePropertyBundle bundle) {
            super(bundle);
            this.baseLanguageProcessor = baseLanguage.createProcessor(bundle);
            this.combinedHandler = new SimpleDialectLanguageVersionHandler(baseLanguageProcessor.services(), dialectHandler);
        }

        @Override
        public @NonNull LanguageVersionHandler services() {
            return combinedHandler;
        }

        @Override
        public @NonNull AutoCloseable launchAnalysis(@NonNull AnalysisTask analysisTask) {
            return baseLanguageProcessor.launchAnalysis(analysisTask);
        }

        @Override
        public void close() throws Exception {
            super.close();
            baseLanguageProcessor.close();
        }
    }

    /**
     * A composite language version handler that merges a dialect's extension with the bae language.
     */
    private static class SimpleDialectLanguageVersionHandler extends AbstractPmdLanguageVersionHandler {

        private final LanguageVersionHandler baseLanguageVersionHandler;
        private final LanguageVersionHandler dialectLanguageVersionHandler;

        SimpleDialectLanguageVersionHandler(LanguageVersionHandler baseLanguageVersionHandler, LanguageVersionHandler dialectLanguageVersionHandler) {
            this.baseLanguageVersionHandler = baseLanguageVersionHandler;
            this.dialectLanguageVersionHandler = dialectLanguageVersionHandler;
        }

        @Override
        public XPathHandler getXPathHandler() {
            // Add dialect-specific XPath functions
            return baseLanguageVersionHandler.getXPathHandler()
                    .combine(dialectLanguageVersionHandler.getXPathHandler());
        }

        @Override
        public Parser getParser() {
            // Always the base language parser for full compatibility (same AST)
            return baseLanguageVersionHandler.getParser();
        }

        @Override
        public ViolationDecorator getViolationDecorator() {
            return ViolationDecorator.chain(
                    Arrays.asList(baseLanguageVersionHandler.getViolationDecorator(),
                            dialectLanguageVersionHandler.getViolationDecorator()));
        }

        @Override
        public List<ViolationSuppressor> getExtraViolationSuppressors() {
            return CollectionUtil.concatView(
                    baseLanguageVersionHandler.getExtraViolationSuppressors(),
                    dialectLanguageVersionHandler.getExtraViolationSuppressors());
        }

        @Override
        public LanguageMetricsProvider getLanguageMetricsProvider() {
            if (baseLanguageVersionHandler.getLanguageMetricsProvider() == null) {
                return dialectLanguageVersionHandler.getLanguageMetricsProvider();
            }

            if (dialectLanguageVersionHandler.getLanguageMetricsProvider() == null) {
                return baseLanguageVersionHandler.getLanguageMetricsProvider();
            }

            // merge metrics if both define any
            return () -> {
                Set<Metric<?, ?>> mergedSet = new HashSet<>();
                mergedSet.addAll(baseLanguageVersionHandler.getLanguageMetricsProvider().getMetrics());
                mergedSet.addAll(dialectLanguageVersionHandler.getLanguageMetricsProvider().getMetrics());
                return mergedSet;
            };
        }

        @Override
        public DesignerBindings getDesignerBindings() {
            // if the dialect set something it has priority
            if (DesignerBindings.DefaultDesignerBindings.getInstance().equals(dialectLanguageVersionHandler.getDesignerBindings())) {
                return baseLanguageVersionHandler.getDesignerBindings();
            }

            return dialectLanguageVersionHandler.getDesignerBindings();
        }
    }
}
