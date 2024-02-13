/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

public class RuleSetReferenceId extends net.sourceforge.pmd.lang.rule.internal.RuleSetReferenceId {
    public RuleSetReferenceId(String id) {
        super(id);
    }

    public RuleSetReferenceId(String id, RuleSetReferenceId externalRuleSetReferenceId) {
        super(id, externalRuleSetReferenceId);
    }
}
