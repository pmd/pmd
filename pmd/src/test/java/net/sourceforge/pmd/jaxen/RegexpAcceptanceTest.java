
package net.sourceforge.pmd.jaxen;
 
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import net.sourceforge.pmd.testframework.TestDescriptor;

import org.junit.Test;

 
public class RegexpAcceptanceTest extends SimpleAggregatorTst {

	private static final String xPath = "//ClassOrInterfaceDeclaration[matches(@Image, 'F?o')]";
     @Test
     public void testSimple() throws Throwable {
         Rule r = new XPathRule(xPath);
         r.setLanguage(Language.JAVA);
         r.setMessage("");
         TestDescriptor[] testDescriptors = extractTestsFromXml(r, "RegexpAcceptance");
         for (TestDescriptor testDescriptor: testDescriptors) {
             testDescriptor.setReinitializeRule(false);
         }
         runTests(testDescriptors);
     }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(RegexpAcceptanceTest.class);
    }
}
