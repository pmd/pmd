/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersionHandler;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class EcmascriptLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Ecmascript";
    public static final String TERSE_NAME = "ecmascript";
    private static final Ecmascript3Handler DEFAULT = new Ecmascript3Handler();

    public EcmascriptLanguageModule() {
        super(NAME, null, TERSE_NAME, "js");
        addVersion("3", DEFAULT, true);
    }

    public static LanguageVersionHandler defaultHandler() {
        return DEFAULT;
    }
}
