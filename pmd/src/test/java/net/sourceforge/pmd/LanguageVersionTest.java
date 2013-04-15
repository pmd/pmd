package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LanguageVersionTest {

    private String language;
    private String version;
    private String terseName;
    private LanguageVersion expected;

    public LanguageVersionTest(String language, String version, LanguageVersion expected) {
        this.language = language;
        this.version = version;
        this.terseName = language;
        if (version != null && !version.isEmpty()) {
            this.terseName += " " + version;
        }
        this.expected = expected;
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { "java", "1.3", LanguageVersion.JAVA_13 },
                { "java", "1.4", LanguageVersion.JAVA_14 },
                { "java", "1.5", LanguageVersion.JAVA_15 },
                { "java", "1.6", LanguageVersion.JAVA_16 },
                { "java", "1.7", LanguageVersion.JAVA_17 },
                { "jsp", "", LanguageVersion.JSP },
                { "xml", "", LanguageVersion.XML },
                { "xsl", "", LanguageVersion.XSL },
                { "ecmascript", "3", LanguageVersion.ECMASCRIPT },
                { "cpp", "", LanguageVersion.CPP },
                { "fortran", "", LanguageVersion.FORTRAN },
                { "php", "", LanguageVersion.PHP },
                { "ruby", "", LanguageVersion.RUBY },

                // this one won't be found: case sensitive!
                { "JAVA", "1.7", null },
            });
    }

    @Test
    public void testGetLanguageVersionForTerseName() {
        assertEquals(expected, LanguageVersion.findByTerseName(terseName));
    }

    @Test
    public void testFindVersionsForLanguageTerseName() {
        SourceLanguage sourceLanguage = new SourceLanguage();
        sourceLanguage.setName(language);
        sourceLanguage.setVersion(version);

        LanguageVersion languageVersion = LanguageVersion.findVersionsForLanguageTerseName(sourceLanguage.getName(),
                sourceLanguage.getVersion());

        assertEquals(expected, languageVersion);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LanguageVersionTest.class);
    }
}
