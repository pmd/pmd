/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 11:05:13 AM
 */
package test.net.sourceforge.pmd;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;

import java.util.*;

public class MockRule implements Rule {

    private String name;
    private String desc;
    private Set violations = new HashSet();
    private Properties properties = new Properties();
    private String description;
    private String example;
    private boolean m_include;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasProperty( String name ) {
        return properties.containsKey( name );
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
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

    public Properties getProperties()
    {
        return properties;
    }

    public boolean include()
    {
        return true;
    }

    public void setInclude(boolean include)
    {
        m_include = include;
    }


    public MockRule() {}

    public MockRule(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
    public String getMessage() {return desc;}
    public void setMessage(String description) {this.desc = description;}

    public void addViolation( RuleViolation violation ) {
    violations.add( violation );
    }

    public void apply(List astCompilationUnits, RuleContext ctx) {
    Report report = ctx.getReport();

    Iterator vs = violations.iterator();
    while (vs.hasNext()) {
        report.addRuleViolation( (RuleViolation) vs.next() );
    }
    }

}
