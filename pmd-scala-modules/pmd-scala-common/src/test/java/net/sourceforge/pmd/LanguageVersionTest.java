/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.13",
                LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getVersion("2.13"), },
            { ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.12",
                LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getVersion("2.12"), },
            { ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.11",
                LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getVersion("2.11"), },
            { ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.10",
                LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getVersion("2.10"), }, });
    }
}
