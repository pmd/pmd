/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.rule;

import net.sourceforge.pmd.lang.DummyLanguageModule;

public class MockRule extends net.sourceforge.pmd.lang.rule.MockRule {
    public MockRule() {
        super();
        setLanguage(DummyLanguageModule.getInstance());
    }

    public MockRule(String name, String description, String message, String ruleSetName, RulePriority priority) {
        super(name, description, message, ruleSetName, priority);
        setLanguage(DummyLanguageModule.getInstance());
    }

    public MockRule(String name, String description, String message, String ruleSetName) {
        super(name, description, message, ruleSetName);
        setLanguage(DummyLanguageModule.getInstance());
    }
}
