/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import net.sourceforge.pmd.lang.AbstractCpdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

/**
 * Implementation of LanguageVersionHandler for the C++ Language.
 * @deprecated There is no full PMD support for c++.
 */
@Deprecated
public class CppHandler extends AbstractCpdLanguageVersionHandler {

    @Override
    protected String getLanguageName() {
        return "C++";
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new CppParser(parserOptions);
    }
}
