/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import net.sourceforge.pmd.lang.DummyLanguageModule.Handler;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * A second dummy language used for testing PMD.
 */
public class Dummy2LanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Dummy2";
    public static final String TERSE_NAME = "dummy2";

    public Dummy2LanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("dummy2")
                              .addDefaultVersion("1.0"), new Handler());
    }

    public static Dummy2LanguageModule getInstance() {
        return (Dummy2LanguageModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
    }

}
