/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties.InferenceLoggingVerbosity;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.BaseContextNodeTestFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetModifiersFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MatchesSignatureFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MetricFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.NodeIsFunction;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.reporting.ViolationDecorator;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

/**
 * @author Clément Fournier
 */
public class JavaLanguageProcessor extends BatchLanguageProcessor<JavaLanguageProperties>
    implements LanguageVersionHandler {

    private final LanguageMetricsProvider myMetricsProvider = new JavaMetricsProvider();
    private final JavaParser parser;
    private final JavaParser parserWithoutProcessing;
    private TypeSystem typeSystem;

    public JavaLanguageProcessor(JavaLanguageProperties properties, TypeSystem typeSystem) {
        super(properties);
        this.typeSystem = typeSystem;

        String suppressMarker = properties.getSuppressMarker();
        this.parser = new JavaParser(suppressMarker, this, true);
        this.parserWithoutProcessing = new JavaParser(suppressMarker, this, false);
    }

    public JavaLanguageProcessor(JavaLanguageProperties properties) {
        this(properties, TypeSystem.usingClassLoaderClasspath(properties.getAnalysisClassLoader()));
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return this;
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    public JavaParser getParserWithoutProcessing() {
        return parserWithoutProcessing;
    }

    public TypeSystem getTypeSystem() {
        return typeSystem;
    }

    TypeInferenceLogger newTypeInfLogger() {
        InferenceLoggingVerbosity verbosity = getProperties().getProperty(JavaLanguageProperties.INTERNAL_INFERENCE_LOGGING_VERBOSITY);
        if (verbosity == InferenceLoggingVerbosity.VERBOSE) {
            return new VerboseLogger(System.err);
        } else if (verbosity == InferenceLoggingVerbosity.SIMPLE) {
            return new SimpleLogger(System.err);
        } else {
            return TypeInferenceLogger.noop();
        }
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return JavaDesignerBindings.INSTANCE;
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public List<ViolationSuppressor> getExtraViolationSuppressors() {
        return AnnotationSuppressionUtil.ALL_JAVA_SUPPRESSORS;
    }

    @Override
    public ViolationDecorator getViolationDecorator() {
        return JavaViolationDecorator.INSTANCE;
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return myMetricsProvider;
    }

    private static final XPathHandler XPATH_HANDLER =
        XPathHandler.getHandlerForFunctionDefs(
            BaseContextNodeTestFun.TYPE_IS_EXACTLY,
            BaseContextNodeTestFun.TYPE_IS,
            BaseContextNodeTestFun.HAS_ANNOTATION,
            MatchesSignatureFunction.INSTANCE,
            NodeIsFunction.INSTANCE,
            GetModifiersFun.GET_EFFECTIVE,
            GetModifiersFun.GET_EXPLICIT,
            MetricFunction.INSTANCE,
            GetCommentOnFunction.INSTANCE
        );

    public void setTypeSystem(TypeSystem ts) {
        this.typeSystem = Objects.requireNonNull(ts);
    }
}
