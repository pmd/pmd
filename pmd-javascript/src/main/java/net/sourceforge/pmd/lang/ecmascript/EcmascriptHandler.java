/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;

/**
 * Implementation of LanguageVersionHandler for the ECMAScript Version 3.
 */
class EcmascriptHandler extends AbstractPmdLanguageVersionHandler {

    private final int rhinoVersion;

    EcmascriptHandler(int rhinoVersion) {
        this.rhinoVersion = rhinoVersion;
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new EcmascriptParser(rhinoVersion, parserOptions.getSuppressMarker());
    }

}
