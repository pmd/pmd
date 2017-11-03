/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/plsql/design.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "ExcessiveMethodLength");
        addRule(RULESET, "ExcessiveObjectLength");
        addRule(RULESET, "ExcessivePackageBodyLength");
        addRule(RULESET, "ExcessivePackageSpecificationLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessiveTypeLength");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssObjectCount");
        addRule(RULESET, "NPathComplexity");
        addRule(RULESET, "TooManyFields");
    }
}
