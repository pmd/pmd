/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public class ApexHandler extends AbstractLanguageVersionHandler {

	@Override
    public RuleViolationFactory getRuleViolationFactory() {
        throw new UnsupportedOperationException("getRuleViolationFactory() is not supported for Apex");
    }
	
    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ApexParser(parserOptions);
    }
}
