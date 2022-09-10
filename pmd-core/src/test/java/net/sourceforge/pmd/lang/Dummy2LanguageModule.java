/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

/**
 * A second dummy language used for testing PMD.
 */
public class Dummy2LanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy2";
    public static final String TERSE_NAME = "dummy2";

    public Dummy2LanguageModule() {
        super(NAME, null, TERSE_NAME, "dummy2");
        addVersion("1.0", new DummyLanguageModule.Handler(), true);
    }

    public static Dummy2LanguageModule getInstance() {
        return (Dummy2LanguageModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
    }

}
