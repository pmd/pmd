
package test.net.sourceforge.pmd.jaxen;
 
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
public class RegexpAcceptanceTest extends SimpleAggregatorTst {

     @Test
     public void testSimple() throws Throwable {
         Rule r = new XPathRule();
         r.addProperty("xpath", "//ClassOrInterfaceDeclaration[matches(@Image, 'F?o')]");
         r.setMessage("");
         runTests(r, "RegexpAcceptance");
     }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(RegexpAcceptanceTest.class);
    }
}
