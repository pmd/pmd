package test.net.sourceforge.pmd.jsp.rules;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DuplicateJspImportTest extends SimpleAggregatorTst {
    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("jsp", "DuplicateJspImports");
    }

    @Test
    public void testAll() {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DuplicateJspImportTest.class);
    }
}
