/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the Objective-C Language.
 *
 * @deprecated There is no full PMD support for Objective-C.
 */
@Deprecated
public class ObjectiveCHandler extends AbstractLanguageVersionHandler {

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        throw new UnsupportedOperationException("getRuleViolationFactory() is not supported for Objective-C");
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ObjectiveCParser(parserOptions);
    }
}
