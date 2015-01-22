/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ImportsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-imports";

    @Override
    public void setUp() {
        addRule(RULESET, "DontImportJavaLang");
        addRule(RULESET, "DuplicateImports");
        addRule(RULESET, "ImportFromSamePackage");
        addRule(RULESET, "TooManyStaticImports");
        addRule(RULESET, "UnnecessaryFullyQualifiedName");
        addRule(RULESET, "UnusedImports");
    }

    /**
     * This is just for testing DuplicateImports for static imports and disambiguation.
     */
    // Do not delete this method, its needed for a test case
    // see: /pmd-java/src/test/resources/net/sourceforge/pmd/lang/java/rule/imports/xml/DuplicateImports.xml
    // #1306 False positive on duplicate when using static imports
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println(message);
        }
    }
}
