package test.net.sourceforge.pmd.lang.java.rule.imports;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ImportsRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-imports";

    @Before
    public void setUp() {
        addRule(RULESET, "DontImportJavaLang");
        addRule(RULESET, "DuplicateImports");
        addRule(RULESET, "ImportFromSamePackage");
        addRule(RULESET, "TooManyStaticImports");
        addRule(RULESET, "UnnecessaryFullyQualifiedName");
        addRule(RULESET, "UnusedImports");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ImportsRulesTest.class);
    }
}
