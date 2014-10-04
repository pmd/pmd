package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AbstractLanguageVersionTest {

    private String name;
    private String version;
    private String terseName;
    private LanguageVersion expected;

    public AbstractLanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        this.name = name;
        this.version = version;
        this.terseName = terseName;
        if (version != null && !version.isEmpty()) {
            this.terseName += " " + version;
        }
        this.expected = expected;
    }

    @Test
    public void testGetLanguageVersionForTerseName() {
        assertEquals(expected, LanguageRegistry.findLanguageVersionByTerseName(terseName));
    }

    @Test
    public void testFindVersionsForLanguageNameAndVersion() {
        SourceLanguage sourceLanguage = new SourceLanguage();
        sourceLanguage.setName(name);
        sourceLanguage.setVersion(version);

        Language language = LanguageRegistry.getLanguage(sourceLanguage.getName());
        LanguageVersion languageVersion = null;
        if(language != null) {
            languageVersion = language.getVersion(sourceLanguage.getVersion());
        }

        assertEquals(expected, languageVersion);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AbstractLanguageVersionTest.class);
    }
}
