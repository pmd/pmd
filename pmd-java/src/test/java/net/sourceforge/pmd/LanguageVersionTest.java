/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.3",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.3"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.4",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.4"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.5",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.5"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.6",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.6"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.7",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.7"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.8",
                getLanguage(JavaLanguageModule.NAME).getVersion("1.8"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "9",
                getLanguage(JavaLanguageModule.NAME).getVersion("9"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "10",
                getLanguage(JavaLanguageModule.NAME).getVersion("10"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "11",
                getLanguage(JavaLanguageModule.NAME).getVersion("11"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "12",
                getLanguage(JavaLanguageModule.NAME).getVersion("12"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "12-preview",
                getLanguage(JavaLanguageModule.NAME).getVersion("12-preview"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "13",
                getLanguage(JavaLanguageModule.NAME).getVersion("13"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "13-preview",
                getLanguage(JavaLanguageModule.NAME).getVersion("13-preview"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "14",
                getLanguage(JavaLanguageModule.NAME).getVersion("14"), },
            { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "14-preview",
                getLanguage(JavaLanguageModule.NAME).getVersion("14-preview"), },

            // this one won't be found: case sensitive!
            { "JAVA", "JAVA", "1.7", null, },
        });
    }
}
