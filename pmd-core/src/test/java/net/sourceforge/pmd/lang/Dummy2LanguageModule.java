/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

/**
 * A second dummy language used for testing PMD.
 */
public class Dummy2LanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy2";
    public static final String TERSE_NAME = "dummy2";

    public Dummy2LanguageModule() {
        super(NAME, null, TERSE_NAME, null, "dummy2");
        addVersion("1.0", new DummyLanguageModule.Handler(), true);
    }
}
