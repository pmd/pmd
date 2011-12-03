package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


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
