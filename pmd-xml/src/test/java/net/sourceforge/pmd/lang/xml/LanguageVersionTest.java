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
                new TestDescriptor(XmlLanguageModule.NAME, XmlLanguageModule.TERSE_NAME, "",
                    getLanguage(XmlLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(XslLanguageModule.NAME, XslLanguageModule.TERSE_NAME, "",
                    getLanguage(XslLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(WsdlLanguageModule.NAME, WsdlLanguageModule.TERSE_NAME, "",
                    getLanguage(WsdlLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(PomLanguageModule.NAME, PomLanguageModule.TERSE_NAME, "",
                    getLanguage(PomLanguageModule.NAME).getDefaultVersion()));
    }
}
