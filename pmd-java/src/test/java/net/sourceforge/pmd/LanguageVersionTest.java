package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

import org.junit.runners.Parameterized.Parameters;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.3", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.3") },
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.4", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4") },
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.5", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5") },
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.6", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.6") },
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.7", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.7") },
                { JavaLanguageModule.NAME, JavaLanguageModule.TERSE_NAME, "1.8", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8") },

                // this one won't be found: case sensitive!
                { "JAVA", "JAVA", "1.7", null },
            });
    }
}
