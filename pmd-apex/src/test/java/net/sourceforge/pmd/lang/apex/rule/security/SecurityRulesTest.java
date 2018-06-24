/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SecurityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/security.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "ApexBadCrypto");
        addRule(RULESET, "ApexCRUDViolation");
        addRule(RULESET, "ApexCSRF");
        addRule(RULESET, "ApexDangerousMethods");
        addRule(RULESET, "ApexInsecureEndpoint");
        addRule(RULESET, "ApexOpenRedirect");
        addRule(RULESET, "ApexSharingViolations");
        addRule(RULESET, "ApexSOQLInjection");
        addRule(RULESET, "ApexSuggestUsingNamedCred");
        addRule(RULESET, "ApexXSSFromEscapeFalse");
        addRule(RULESET, "ApexXSSFromURLParam");
    }
}
