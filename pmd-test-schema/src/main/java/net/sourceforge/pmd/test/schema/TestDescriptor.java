/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * @author Clément Fournier
 */
public class TestDescriptor {

    private boolean ignored;
    private String description;
    private LanguageVersion languageVersion;
    private Properties properties = new Properties();
    private int index;
    private final Rule rule;
    private String code;
    private int expectedProblems;
    private List<Integer> expectedLineNumbers;
    private List<String> expectedMessages;

    public TestDescriptor(int index, Rule rule) {
        this.index = index;
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void recordExpectedViolations(int expectedProblems, List<Integer> expectedLineNumbers, List<String> expectedMessages) {
        this.expectedProblems = expectedProblems;
        this.expectedLineNumbers = expectedLineNumbers;
        this.expectedMessages = expectedMessages;
    }
}
