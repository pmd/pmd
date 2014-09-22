package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.ant.SourceLanguage;

import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.lang.LanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionModule;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.fortran.FortranLanguageModule;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.php.PhpLanguageModule;
import net.sourceforge.pmd.lang.ruby.RubyLanguageModule;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;
import net.sourceforge.pmd.lang.xsl.XslLanguageModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LanguageVersionTest {

    private String language;
    private String version;
    private String terseName;
    private LanguageVersionModule expected;

    public LanguageVersionTest(String language, String version, LanguageVersionModule expected) {
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
                { JavaLanguageModule.NAME, "1.3", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.3") },
                { JavaLanguageModule.NAME, "1.4", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4") },
                { JavaLanguageModule.NAME, "1.5", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5") },
                { JavaLanguageModule.NAME, "1.6", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.6") },
                { JavaLanguageModule.NAME, "1.7", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.7") },
                { JavaLanguageModule.NAME, "1.8", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8") },
                { JspLanguageModule.NAME, "", LanguageRegistry.getLanguage(JspLanguageModule.NAME).getDefaultVersion() },
                { XmlLanguageModule.NAME, "", LanguageRegistry.getLanguage(XmlLanguageModule.NAME).getDefaultVersion() },
                { XslLanguageModule.NAME, "", LanguageRegistry.getLanguage(XslLanguageModule.NAME).getDefaultVersion() },
                { EcmascriptLanguageModule.NAME, "3", LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME).getDefaultVersion() },
                { CppLanguageModule.NAME, "", LanguageRegistry.getLanguage(CppLanguageModule.NAME).getDefaultVersion() },
                { FortranLanguageModule.NAME, "", LanguageRegistry.getLanguage(FortranLanguageModule.NAME).getDefaultVersion() },
                { PhpLanguageModule.NAME, "", LanguageRegistry.getLanguage(PhpLanguageModule.NAME).getDefaultVersion() },
                { RubyLanguageModule.NAME, "", LanguageRegistry.getLanguage(RubyLanguageModule.NAME).getDefaultVersion() },
                { VmLanguageModule.NAME, "", LanguageRegistry.getLanguage(VmLanguageModule.NAME).getDefaultVersion() },

                // this one won't be found: case sensitive!
                { "JAVA", "1.7", null },
            });
    }

    @Test
    public void testGetLanguageVersionForTerseName() {
//        throw new RuntimeException("Finish this.");
//        assertEquals(expected, LanguageRegistry.findVersionByTerseName(terseName));
    }

    @Test
    public void testFindVersionsForLanguageTerseName() {
        SourceLanguage sourceLanguage = new SourceLanguage();
        sourceLanguage.setName(language);
        sourceLanguage.setVersion(version);

        LanguageModule language = LanguageRegistry.getLanguage(sourceLanguage.getName());
        LanguageVersionModule languageVersion = null;
        if(language != null) {
            languageVersion = language.getVersion(sourceLanguage.getVersion());
        }

        assertEquals(expected, languageVersion);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LanguageVersionTest.class);
    }
}
