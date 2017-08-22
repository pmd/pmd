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
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.DumpFacade;
import net.sourceforge.pmd.lang.apex.rule.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

import net.sf.saxon.sxpath.IndependentContext;

public class ApexHandler extends AbstractLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new AbstractASTXPathHandler() {
            public void initialize() {
            }

            public void initialize(IndependentContext context) {
            }
        };
    }

    public RuleViolationFactory getRuleViolationFactory() {
        return ApexRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new ApexParserOptions();
    }

    public Parser getParser(ParserOptions parserOptions) {
        return new ApexParser(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse) {
        return new VisitorStarter() {
            public void start(Node rootNode) {
                new DumpFacade().initializeWith(writer, prefix, recurse, (ApexNode<?>) rootNode);
            }
        };
    }

}
