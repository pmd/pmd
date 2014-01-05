/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.jsp.ast.DumpFacade;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the JSP parser.
 * 
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspHandler extends AbstractLanguageVersionHandler {

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
        return JspRuleViolationFactory.INSTANCE;
    }

    public Parser getParser(ParserOptions parserOptions) {
        return new JspParser(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            public void start(Node rootNode) {
                new DumpFacade().initializeWith(writer, prefix, recurse, (JspNode) rootNode);
            }
        };
    }
}
