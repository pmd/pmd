/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Ignore;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * Stores the information required to run a complete test.
 */
@Ignore("this is not a unit test")
public class TestDescriptor {
    private Rule rule;
    private Properties properties;
    private String description;
    private int numberOfProblemsExpected;
    private List<String> expectedMessages = new ArrayList<>();
    private List<Integer> expectedLineNumbers = new ArrayList<>();
    private String code;
    private LanguageVersion languageVersion;
    // default, avoids unintentional mixing of state between test cases
    private boolean reinitializeRule = true;
    private boolean ignored = true;
    private boolean useAuxClasspath = true;
    private boolean isFocused = false;
    private int numberInDocument = -1;

    public TestDescriptor() {
        // Empty default descriptor added to please mvn surefire plugin
    }

    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule) {
        this(code, description, numberOfProblemsExpected, rule, rule.getLanguage().getDefaultVersion());
    }

    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule,
            LanguageVersion languageVersion) {
        this.rule = rule;
        this.code = code;
        this.description = description;
        this.numberOfProblemsExpected = numberOfProblemsExpected;
        this.languageVersion = languageVersion;
    }

    public int getNumberInDocument() {
        return numberInDocument;
    }

    public void setNumberInDocument(int numberInDocument) {
        this.numberInDocument = numberInDocument;
    }

    public void setExpectedMessages(List<String> messages) {
        expectedMessages.clear();
        expectedMessages.addAll(messages);
    }

    public List<String> getExpectedMessages() {
        return expectedMessages;
    }

    public void setExpectedLineNumbers(List<Integer> expectedLineNumbers) {
        this.expectedLineNumbers.clear();
        this.expectedLineNumbers.addAll(expectedLineNumbers);
    }

    public List<Integer> getExpectedLineNumbers() {
        return expectedLineNumbers;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getCode() {
        return code;
    }

    public LanguageVersion getLanguageVersion() {
        return languageVersion;
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

    public boolean getReinitializeRule() {
        return reinitializeRule;
    }

    public void setReinitializeRule(boolean reinitializeRule) {
        this.reinitializeRule = reinitializeRule;
    }

    /** If true, the test should never be executed. Replaces the previous weird isRegressionTest. */
    public boolean isIgnored() {
        return ignored;
    }

    /** The latest test in file order (if any) which is focused gets executed alone. */
    public boolean isFocused() {
        return isFocused;
    }

    public void setIgnored(boolean isIgnored) {
        this.ignored = isIgnored;
    }

    public void setFocus(boolean isFocused) {
        this.isFocused = isFocused;
    }

    public void setUseAuxClasspath(boolean useAuxClasspath) {
        this.useAuxClasspath = useAuxClasspath;
    }

    public boolean isUseAuxClasspath() {
        return useAuxClasspath;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object data) {
        if (this == data) {
            return true;
        }
        if (data == null || getClass() != data.getClass()) {
            return false;
        }
        TestDescriptor that = (TestDescriptor) data;
        return Objects.equals(rule, that.rule) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, description);
    }
}
