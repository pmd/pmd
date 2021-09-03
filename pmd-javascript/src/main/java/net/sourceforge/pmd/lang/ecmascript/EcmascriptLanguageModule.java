/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import org.mozilla.javascript.Context;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class EcmascriptLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Ecmascript";
    public static final String TERSE_NAME = "ecmascript";

    public EcmascriptLanguageModule() {
        super(NAME, null, TERSE_NAME, "js");
        addDefaultVersion("ES6", new EcmascriptHandler(Context.VERSION_ES6));
    }
}
