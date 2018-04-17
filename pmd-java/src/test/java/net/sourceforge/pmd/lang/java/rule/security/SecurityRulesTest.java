package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SecurityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/security.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "InsecureCryptoIv");
    }

}
