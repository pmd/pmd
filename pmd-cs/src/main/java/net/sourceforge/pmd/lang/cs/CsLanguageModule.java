/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.cs;

import net.sourceforge.pmd.lang.BaseLanguageModule;

public class CsLanguageModule extends BaseLanguageModule {

    public static final String NAME = "C#";
    public static final String TERSE_NAME = "cs";

    public CsLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "cs");
        addVersion("", null, true);
    }
}
