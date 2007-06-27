package test.net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class ImportsRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("imports", "DontImportJavaLang"));
        rules.add(findRule("imports", "DuplicateImports"));
        rules.add(findRule("imports", "ImportFromSamePackage"));
        rules.add(findRule("imports", "UnusedImports"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ImportsRulesTest.class);
    }
}
