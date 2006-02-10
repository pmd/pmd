package test.net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class RegexpAcceptanceTest extends RuleTst {

    public void testSimple() throws Throwable {
        Rule r = new XPathRule();
        r.addProperty("xpath", "//ClassOrInterfaceDeclaration[matches(@Image, 'F?o')]");
        r.setMessage("");
        runTestFromString(TEST1, 1, r);
        runTestFromString(TEST2, 0, r);
        runTestFromString(TEST3, 1, r);
    }

    private static final String TEST1 =
            "public class Foo {}";

    private static final String TEST2 =
            "public class Bar {}";

    private static final String TEST3 =
            "public class Flo {}";
}
