/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { KotlinLanguageModule.NAME, KotlinLanguageModule.TERSE_NAME, "1.3",
            LanguageRegistry.getLanguage(KotlinLanguageModule.NAME).getDefaultVersion(), }, });
    }
}
