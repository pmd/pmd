/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import net.sourceforge.pmd.Rule;

public class TestDescriptor {

    private String code;
    private String description;
    private int numberOfProblemsExpected;
    private Rule rule;

    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule) {
        this.rule = rule;
        this.code = code;
        this.description = description;
        this.numberOfProblemsExpected = numberOfProblemsExpected;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfProblemsExpected() {
        return numberOfProblemsExpected;
    }

    public Rule getRule() {
        return rule;
    }
}
