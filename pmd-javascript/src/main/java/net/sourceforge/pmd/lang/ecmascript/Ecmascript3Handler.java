/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

/**
 * Implementation of LanguageVersionHandler for the ECMAScript Version 3.
 */
public class Ecmascript3Handler extends AbstractPmdLanguageVersionHandler {

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new EcmascriptParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new Ecmascript3Parser((EcmascriptParserOptions) parserOptions);
    }

}
