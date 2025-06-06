/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.Rule;

/**
 * @author Clément Fournier
 */
public class RuleTestDescriptor {

    private boolean disabled;
    private boolean focused;
    private String description;
    private LanguageVersion languageVersion;
    private final Properties properties = new Properties();
    private final int index;
    private final Rule rule;
    private String code;
    private int expectedProblems;
    private List<Integer> expectedLineNumbers;
    private List<Integer> expectedEndLineNumbers;
    private List<String> expectedMessages;
    private List<SuppressionDescriptor> expectedSuppressions;
    private int lineNumber;

    public static final class SuppressionDescriptor {
        private final int line;
        private final String suppressorId;

        private SuppressionDescriptor(int line, String suppressorId) {
            this.line = line;
            this.suppressorId = suppressorId;
        }

        public int getLine() {
            return line;
        }

        public String getSuppressorId() {
            return suppressorId;
        }
    }

    public RuleTestDescriptor(int index, Rule rule) {
        this.index = index;
        this.rule = rule;
        this.languageVersion = rule.getLanguage().getDefaultVersion();
    }

    public Rule getRule() {
        return rule;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
        if (!languageVersion.getLanguage().equals(this.getRule().getLanguage())) {
            throw new IllegalArgumentException("Invalid version " + languageVersion);
        }
        this.languageVersion = languageVersion;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void recordExpectedViolations(int expectedProblems, List<Integer> expectedLineNumbers, List<String> expectedMessages) {
        checkListSize(expectedProblems, expectedLineNumbers);
        checkListSize(expectedProblems, expectedMessages);

        this.expectedProblems = expectedProblems;
        this.expectedLineNumbers = expectedLineNumbers;
        this.expectedMessages = expectedMessages;
    }

    public void recordExpectedViolations(int expectedProblems, List<Integer> expectedLineNumbers, List<Integer> expectedEndLineNumbers, List<String> expectedMessages) {
        checkListSize(expectedProblems, expectedEndLineNumbers);
        this.expectedEndLineNumbers = expectedEndLineNumbers;
        recordExpectedViolations(expectedProblems, expectedLineNumbers, expectedMessages);
    }

    private void checkListSize(int expectedProblems, List<?> expectedMessages) {
        if (!expectedMessages.isEmpty() && expectedProblems != expectedMessages.size()) {
            throw new IllegalArgumentException(
                "Expected list of size " + expectedProblems + ", got " + expectedMessages);
        }
    }

    public int getExpectedProblems() {
        return expectedProblems;
    }

    public int getIndex() {
        return index;
    }

    public List<Integer> getExpectedLineNumbers() {
        return expectedLineNumbers;
    }

    public List<Integer> getExpectedEndLineNumbers() {
        return expectedEndLineNumbers;
    }

    public List<String> getExpectedMessages() {
        return expectedMessages;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean hasExpectedSuppressions() {
        return expectedSuppressions != null;
    }

    void createEmptyExpectedSuppression() {
        expectedSuppressions = new ArrayList<>();
    }

    void recordExpectedSuppression(String line, String suppressor) {
        this.expectedSuppressions.add(new SuppressionDescriptor(Integer.parseInt(line), suppressor));
    }

    public List<SuppressionDescriptor> getExpectedSuppressions() {
        return expectedSuppressions;
    }
}
