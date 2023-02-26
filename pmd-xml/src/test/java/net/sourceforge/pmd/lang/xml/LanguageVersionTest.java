/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;
import net.sourceforge.pmd.lang.pom.PomLanguageModule;
import net.sourceforge.pmd.lang.wsdl.WsdlLanguageModule;
import net.sourceforge.pmd.lang.xsl.XslLanguageModule;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
                new TestDescriptor(XmlLanguageModule.NAME, XmlLanguageModule.TERSE_NAME, "1.1",
                    getLanguage(XmlLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(XslLanguageModule.NAME, XslLanguageModule.TERSE_NAME, "3.0",
                    getLanguage(XslLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(WsdlLanguageModule.NAME, WsdlLanguageModule.TERSE_NAME, "2.0",
                    getLanguage(WsdlLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(PomLanguageModule.NAME, PomLanguageModule.TERSE_NAME, "4.0.0",
                    getLanguage(PomLanguageModule.NAME).getDefaultVersion()));
    }
}
