/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  net.sourceforge.pmd.jbuilder;

import  com.borland.primetime.properties.PropertyGroup;
import  com.borland.primetime.properties.PropertyPageFactory;
import  com.borland.primetime.properties.GlobalProperty;
import  com.borland.primetime.properties.PropertyPage;


/**
 * put your documentation comment here
 */
public class RuleSetPropertyGroup
        implements PropertyGroup {
    /**
     BEGIN - RULE DEFINITION SECTION
     To add new rule sets simply create a new rule set name constant, create GlobalProperty constant,
     and add each to their respective arrays.
     */
    public static final String RULESETS = "RuleSets";
    public static final String RULESET_BASIC = "basic";
    public static final String RULESET_DESIGN = "design";
    public static final String RULESET_UNUSEDCODE = "unusedcode";
    public static final String RULESET_COUGAAR = "cougaar";
    public static final String RULESET_NAMING = "naming";
    public static final Object RULESETS_TOPIC = new Object();
    public static final GlobalProperty PROPKEY_RULESET_DESIGN = new GlobalProperty(RULESETS, RULESET_DESIGN, "true");
    public static final GlobalProperty PROPKEY_RULESET_BASIC = new GlobalProperty(RULESETS, RULESET_BASIC, "true");
    public static final GlobalProperty PROPKEY_RULESET_UNUSEDCODE = new GlobalProperty(RULESETS, RULESET_UNUSEDCODE, "true");
    public static final GlobalProperty PROPKEY_RULESET_COUGAAR = new GlobalProperty(RULESETS, RULESET_COUGAAR, "true");
    public static final GlobalProperty PROPKEY_RULESET_NAMING = new GlobalProperty(RULESETS, RULESET_NAMING, "true");
    //make sure that these two arrays are synchronized
    public static final String[] RULESET_NAMES =  {
        RULESET_BASIC, RULESET_DESIGN, RULESET_UNUSEDCODE, RULESET_COUGAAR, RULESET_NAMING
    };
    public static final GlobalProperty[] PROPKEYS =  {
        PROPKEY_RULESET_BASIC, PROPKEY_RULESET_DESIGN, PROPKEY_RULESET_UNUSEDCODE, PROPKEY_RULESET_COUGAAR, PROPKEY_RULESET_NAMING
    };

    /**
     * END - RULE DEFINITION SECTION
     */

    /**
    * Standard Constructor
    */
    public RuleSetPropertyGroup () {
    }

    /**
     * Called by JBuilder
     */
    public void initializeProperties () {
    }

    /**
     * Create the panel that will go in the property page
     * @param topic Topic of page (represented as a tab in the property page)
     * @return Factory for creating property pages - this factory will create RuleSetPropertyPages
     */
    public PropertyPageFactory getPageFactory (Object topic) {
        if (topic == RULESETS_TOPIC) {
            return  new PropertyPageFactory("PMD Properties", "Set PMD RuleSet Properties") {

                public PropertyPage createPropertyPage () {
                    return  new RuleSetPropertyPage();
                }
            };
        }
        return  null;
    }
}



