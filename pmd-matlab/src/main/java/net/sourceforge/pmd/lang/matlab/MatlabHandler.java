/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the Matlab Language.
 *
 * @deprecated There is no full PMD support for Matlab.
 */
@Deprecated
public class MatlabHandler extends AbstractLanguageVersionHandler {

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        throw new UnsupportedOperationException("getRuleViolationFactory() is not supported for Matlab");
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new MatlabParser(parserOptions);
    }
}
