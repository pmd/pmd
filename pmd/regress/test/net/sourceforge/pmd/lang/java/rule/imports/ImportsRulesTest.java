package test.net.sourceforge.pmd.lang.java.rule.imports;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ImportsRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("imports", "DontImportJavaLang");
        addRule("imports", "DuplicateImports");
        addRule("imports", "ImportFromSamePackage");
        addRule("imports", "TooManyStaticImports");
        addRule("imports", "UnnecessaryFullyQualifiedName");
        addRule("imports", "UnusedImports");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ImportsRulesTest.class);
    }
}
