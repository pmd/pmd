/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import net.sourceforge.pmd.AbstractRule;

public class MockRule extends AbstractRule {

    public MockRule(String name, String description, String message, String ruleSetName) {
        super();
        this.name = name;
        this.description = description;
        this.message = message;
        this.ruleSetName = ruleSetName;
    }

    public MockRule(String name, String description, String message, String ruleSetName, int priority) {
        super();
        this.name = name;
        this.description = description;
        this.message = message;
        this.ruleSetName = ruleSetName;
        this.priority = priority;
    }

    public MockRule() {
        super();
    }
}
