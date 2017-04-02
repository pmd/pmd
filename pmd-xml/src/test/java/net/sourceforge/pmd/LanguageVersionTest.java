/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.pom.PomLanguageModule;
import net.sourceforge.pmd.lang.wsdl.WsdlLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;
import net.sourceforge.pmd.lang.xsl.XslLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { XmlLanguageModule.NAME, XmlLanguageModule.TERSE_NAME, "",
                LanguageRegistry.getLanguage(XmlLanguageModule.NAME).getDefaultVersion(), },
            { XslLanguageModule.NAME, XslLanguageModule.TERSE_NAME, "",
                LanguageRegistry.getLanguage(XslLanguageModule.NAME).getDefaultVersion(), },
            { WsdlLanguageModule.NAME, WsdlLanguageModule.TERSE_NAME, "",
                LanguageRegistry.getLanguage(WsdlLanguageModule.NAME).getDefaultVersion(), },
            { PomLanguageModule.NAME, PomLanguageModule.TERSE_NAME, "",
                LanguageRegistry.getLanguage(PomLanguageModule.NAME).getDefaultVersion(), }, });
    }
}
