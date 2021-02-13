/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.ast.internal.ReportingStrategy;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.BaseContextNodeTestFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetModifiersFun;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MatchesSignatureFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MetricFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.NodeIsFunction;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

public class JavaLanguageHandler extends AbstractPmdLanguageVersionHandler {

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

    private final LanguageLevelChecker<?> levelChecker;
    private final LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> myMetricsProvider = new JavaMetricsProvider();

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        super(JavaProcessingStage.class);
        this.levelChecker = new LanguageLevelChecker<>(jdkVersion, preview, ReportingStrategy.reporterThatThrows());
    }

    public int getJdkVersion() {
        return levelChecker.getJdkVersion();
    }

    @Override
    public Parser getParser() {
        return new JavaParser(levelChecker);
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
    public RuleViolationFactory getRuleViolationFactory() {
        return JavaRuleViolationFactory.INSTANCE;
    }


    @Override
    public LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }


    private static class JavaMetricsProvider extends AbstractLanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> {

        JavaMetricsProvider() {
            super(ASTAnyTypeDeclaration.class, MethodLikeNode.class);
        }

        @Override
        public List<? extends MetricKey<ASTAnyTypeDeclaration>> getAvailableTypeMetrics() {
            return Arrays.asList(JavaClassMetricKey.values());
        }


        @Override
        public List<? extends MetricKey<MethodLikeNode>> getAvailableOperationMetrics() {
            return Arrays.asList(JavaOperationMetricKey.values());
        }
    }
}
