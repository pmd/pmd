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
}
