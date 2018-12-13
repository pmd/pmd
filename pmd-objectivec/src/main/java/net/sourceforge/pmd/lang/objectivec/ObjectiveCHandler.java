/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec;

import net.sourceforge.pmd.lang.AbstractCpdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;


/**
 * Implementation of LanguageVersionHandler for the Objective-C Language.
 *
 * @deprecated There is no full PMD support for Objective-C.
 */
public class ObjectiveCHandler extends AbstractCpdLanguageVersionHandler {

    @Override
    protected String getLanguageName() {
        return "Objective-C";
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ObjectiveCParser(parserOptions);
    }
}
