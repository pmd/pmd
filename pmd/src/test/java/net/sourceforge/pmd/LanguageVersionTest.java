package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.ant.SourceLanguage;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
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

    private String name;
    private String version;
    private String terseName;
    private LanguageVersion expected;

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        this.name = name;
        this.version = version;
        this.terseName = terseName;
        if (version != null && !version.isEmpty()) {
            this.terseName += " " + version;
        }
        this.expected = expected;
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
                { JspLanguageModule.NAME, JspLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(JspLanguageModule.NAME).getDefaultVersion() },
                { XmlLanguageModule.NAME, XmlLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(XmlLanguageModule.NAME).getDefaultVersion() },
                { XslLanguageModule.NAME, XslLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(XslLanguageModule.NAME).getDefaultVersion() },
                { EcmascriptLanguageModule.NAME, EcmascriptLanguageModule.TERSE_NAME, "3", LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME).getDefaultVersion() },
                { CppLanguageModule.NAME, CppLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(CppLanguageModule.NAME).getDefaultVersion() },
                { FortranLanguageModule.NAME, FortranLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(FortranLanguageModule.NAME).getDefaultVersion() },
                { PhpLanguageModule.NAME, PhpLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(PhpLanguageModule.NAME).getDefaultVersion() },
                { RubyLanguageModule.NAME, RubyLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(RubyLanguageModule.NAME).getDefaultVersion() },
                { VmLanguageModule.NAME, VmLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(VmLanguageModule.NAME).getDefaultVersion() },

                // this one won't be found: case sensitive!
                { "JAVA", "JAVA", "1.7", null },
            });
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
        return new JUnit4TestAdapter(LanguageVersionTest.class);
    }
}
