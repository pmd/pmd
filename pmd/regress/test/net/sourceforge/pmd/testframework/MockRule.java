/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import net.sourceforge.pmd.AbstractRule;

public class MockRule extends AbstractRule {

    public MockRule(String name, String description, String message, String ruleSetName) {
        super();
        setName(name);
        setDescription(description);
        setMessage(message);
        setRuleSetName(ruleSetName);
    }

    public MockRule(String name, String description, String message, String ruleSetName, int priority) {
        this(name, description, message, ruleSetName);
        setPriority(priority);
    }

    public MockRule() {
        super();
    }
}
