/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection<Object[]> data() {
        Language java = getLanguage(JavaLanguageModule.NAME);
        return Arrays.asList(new Object[][] {
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.3",
              java.getVersion("1.3"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.4",
              java.getVersion("1.4"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.5",
              java.getVersion("1.5"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.6",
              java.getVersion("1.6"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.7",
              java.getVersion("1.7"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.8",
              java.getVersion("1.8"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "9",
              java.getVersion("9"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "10",
              java.getVersion("10"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "11",
              java.getVersion("11"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "12",
              java.getVersion("12"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "13",
              java.getVersion("13"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "14",
              java.getVersion("14"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "15",
              java.getVersion("15"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "16",
              java.getVersion("16"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "16-preview",
              java.getVersion("16-preview"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "17",
              java.getVersion("17"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "17-preview",
              java.getVersion("17-preview"), },

            // this one won't be found: case sensitive!
            { "JAVA", "JAVA", "1.7", null, },
        });
    }
}
