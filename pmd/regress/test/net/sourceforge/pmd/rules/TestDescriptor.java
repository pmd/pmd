package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;

public class TestDescriptor {

    public String code;
    public String description;
    public int numberOfProblemsExpected;
    public Rule rule;

    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule) {
        this.rule = rule;
        this.code = code;
        this.description = description;
        this.numberOfProblemsExpected = numberOfProblemsExpected;
    }
}
