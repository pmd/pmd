/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions.Version;

public abstract class EcmascriptParserTestBase {

    protected final JsParsingHelper js = JsParsingHelper.DEFAULT.withResourceContext(getClass());

    protected final JsParsingHelper js18 = JsParsingHelper.DEFAULT.withResourceContext(getClass())
                                                                  .withParserOptions(parserVersion(Version.VERSION_1_8));

    public ParserOptions parserVersion(EcmascriptParserOptions.Version version) {
        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        parserOptions.setRhinoLanguageVersion(version);
        return parserOptions;
    }

}
