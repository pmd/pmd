
 package test.net.sourceforge.pmd.jaxen;
 
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.rules.XPathRule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class RegexpAcceptanceTest extends SimpleAggregatorTst {
     public void testSimple() throws Throwable {
         Rule r = new XPathRule();
         r.addProperty("xpath", "//ClassOrInterfaceDeclaration[matches(@Image, 'F?o')]");
         r.setMessage("");
         runTests(r, "RegexpAcceptance");
     }
 }
