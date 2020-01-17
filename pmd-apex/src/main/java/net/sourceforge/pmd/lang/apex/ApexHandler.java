/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.io.Writer;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.DumpFacade;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetricsProvider;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileVisitorFacade;
import net.sourceforge.pmd.lang.apex.rule.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;


public class ApexHandler extends AbstractLanguageVersionHandler {

    private final ApexMetricsProvider myMetricsProvider = new ApexMetricsProvider();


    @Override
    public VisitorStarter getMultifileFacade() {
        return rootNode -> new ApexMultifileVisitorFacade().initializeWith((ApexNode<?>) rootNode);
    }


    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }

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

    @Deprecated
    @Override
    public VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse) {
        return rootNode -> new DumpFacade().initializeWith(writer, prefix, recurse, (ApexNode<?>) rootNode);
    }


    @Override
    public LanguageMetricsProvider<ASTUserClassOrInterface<?>, ASTMethod> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }
}
