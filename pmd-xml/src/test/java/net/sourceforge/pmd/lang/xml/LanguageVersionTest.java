/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.xml.pom.PomDialectModule;
import net.sourceforge.pmd.lang.xml.wsdl.WsdlDialectModule;
import net.sourceforge.pmd.lang.xml.xsl.XslDialectModule;
import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
            TestDescriptor.defaultVersionIs(XmlLanguageModule.getInstance(), "1.1"),
            TestDescriptor.defaultVersionIs(XslDialectModule.getInstance(), "3.0"),
            TestDescriptor.defaultVersionIs(WsdlDialectModule.getInstance(), "2.0"),
            TestDescriptor.defaultVersionIs(PomDialectModule.getInstance(), "4.0.0")
        );
    }
}
