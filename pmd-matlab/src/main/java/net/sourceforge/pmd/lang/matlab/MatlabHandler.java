/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import net.sourceforge.pmd.lang.AbstractCpdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;


/**
 * Implementation of LanguageVersionHandler for the Matlab Language.
 *
 * @deprecated There is no full PMD support for Matlab.
 */
@Deprecated
public class MatlabHandler extends AbstractCpdLanguageVersionHandler {

    @Override
    protected String getLanguageName() {
        return "Matlab";
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new MatlabParser(parserOptions);
    }
}
