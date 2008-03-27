/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import java.util.Properties;

import net.sourceforge.pmd.LanguageVersion;
import net.sourceforge.pmd.Rule;

/**
 * Stores the information required to run a complete test.
 */
public class TestDescriptor {
    private Rule rule;
    private Properties properties;
    private String description;
    private int numberOfProblemsExpected;
    private String code;
    private LanguageVersion languageVersion;
    private boolean reinitializeRule = false;   //default
    private boolean isRegressionTest = true;

    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule) {
        this(code, description, numberOfProblemsExpected, rule, RuleTst.DEFAULT_LANGUAGE_VERSION);
    }
    
    public TestDescriptor(String code, String description, int numberOfProblemsExpected, Rule rule, LanguageVersion languageVersion) {
        this.rule = rule;
        this.code = code;
        this.description = description;
        this.numberOfProblemsExpected = numberOfProblemsExpected;
        this.languageVersion = languageVersion;
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

    /**
     * Checks whether we are testing for regression problems only.
     * Return value is based on the system property "pmd.regress".
     * 
     * @return <code>true</code> if system property "pmd.regress" is set to <code>true</code>, <code>false</code> otherwise
     */
    public static boolean inRegressionTestMode() {
        //get the "pmd.regress" System property
        return Boolean.getBoolean("pmd.regress");
    }

    public boolean isRegressionTest() {
        return isRegressionTest;
    }

    public void setRegressionTest(boolean isRegressionTest) {
        this.isRegressionTest = isRegressionTest;
    }
}
