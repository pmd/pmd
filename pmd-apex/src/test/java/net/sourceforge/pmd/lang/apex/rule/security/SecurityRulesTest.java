/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SecurityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-security";

    @Override
    public void setUp() {
        addRule(RULESET, "ApexBadCrypto");
        addRule(RULESET, "ApexXSSFromEscapeFalse");
        addRule(RULESET, "ApexXSSFromURLParam");
        addRule(RULESET, "ApexCSRF");
        addRule(RULESET, "ApexOpenRedirect");
        addRule(RULESET, "ApexSOQLInjection");
        addRule(RULESET, "ApexSharingViolations");
        addRule(RULESET, "ApexInsecureEndpoint");
        addRule(RULESET, "ApexCRUDViolation");
        addRule(RULESET, "ApexDangerousMethods");
        addRule(RULESET, "ApexSuggestUsingNamedCred");
        addRule(RULESET, "AvoidHardcodingId");
    }
}
