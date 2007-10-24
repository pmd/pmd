package test.net.sourceforge.pmd.jsp.rules;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DuplicateJspImportTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("jsp", "DuplicateJspImports");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DuplicateJspImportTest.class);
    }
}
