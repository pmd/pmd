/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.testframework;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class MockRule implements Rule {

    private String name;
    private String description;
    private String message;
    private Set violations = new HashSet();
    private Properties properties = new Properties();
    private String example;
    private int priority;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getPriorityName() {
        return null;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public void addProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public int getIntProperty(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

    public double getDoubleProperty(String name) {
        return Double.parseDouble(properties.getProperty(name));
    }

    public boolean getBooleanProperty(String name) {
        return Boolean.valueOf(properties.getProperty(name)).booleanValue();
    }

    public String getStringProperty(String name) {
        return properties.getProperty(name);
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean include() {
        return true;
    }

    public void setInclude(boolean include) {
    }

    /**
     * For use by RuleSetFactory only!
     */
    public MockRule() {
    }

    public MockRule(String name, String description, String message) {
        this.name = name;
        this.description = description;
        this.message = message;
    }


    public void addViolation(RuleViolation violation) {
        violations.add(violation);
    }

    public void apply(List astCompilationUnits, RuleContext ctx) {
        Report report = ctx.getReport();

        Iterator vs = violations.iterator();
        while (vs.hasNext()) {
            report.addRuleViolation((RuleViolation) vs.next());
        }
    }

}
