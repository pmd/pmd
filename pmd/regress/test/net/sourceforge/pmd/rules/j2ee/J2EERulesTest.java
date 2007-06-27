package test.net.sourceforge.pmd.rules.j2ee;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class J2EERulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("j2ee", "LocalHomeNamingConvention"));
        rules.add(findRule("j2ee", "LocalInterfaceSessionNamingConvention"));
        rules.add(findRule("j2ee", "MDBAndSessionBeanNamingConvention"));
        rules.add(findRule("j2ee", "RemoteInterfaceNamingConvention"));
        rules.add(findRule("j2ee", "RemoteSessionInterfaceNamingConvention"));
        rules.add(findRule("j2ee", "UseProperClassLoader"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(J2EERulesTest.class);
    }
}
