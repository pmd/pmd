/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.apex.rule.ApexRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the ECMAScript Version 3.
 */
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
}
