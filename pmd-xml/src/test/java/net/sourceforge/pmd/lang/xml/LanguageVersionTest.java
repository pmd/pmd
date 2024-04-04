/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.xml.pom.PomLanguageModule;
import net.sourceforge.pmd.lang.xml.wsdl.WsdlLanguageModule;
import net.sourceforge.pmd.lang.xml.xsl.XslLanguageModule;
import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
            TestDescriptor.defaultVersionIs(XmlLanguageModule.getInstance(), "1.1"),
            TestDescriptor.defaultVersionIs(XslLanguageModule.getInstance(), "3.0"),
            TestDescriptor.defaultVersionIs(WsdlLanguageModule.getInstance(), "2.0"),
            TestDescriptor.defaultVersionIs(PomLanguageModule.getInstance(), "4.0.0")
        );
    }
}
