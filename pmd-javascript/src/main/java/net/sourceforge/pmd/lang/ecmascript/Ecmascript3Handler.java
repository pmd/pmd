/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the ECMAScript Version 3.
 */
public class Ecmascript3Handler extends AbstractPmdLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return EcmascriptRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new EcmascriptParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new Ecmascript3Parser(parserOptions);
    }

}
