/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the JSP parser.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return JspRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JspParser(parserOptions);
    }

}
