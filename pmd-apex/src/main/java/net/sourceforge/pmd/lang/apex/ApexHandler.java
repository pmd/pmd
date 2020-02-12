/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetricsProvider;
import net.sourceforge.pmd.lang.apex.rule.internal.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public class ApexHandler extends AbstractPmdLanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();


    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return ApexRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new ApexParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ApexParser(parserOptions);
    }


    @Override
    public LanguageMetricsProvider<ASTUserClassOrInterface<?>, ASTMethod> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }
}
