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
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSet;
import java.util.Iterator;
import java.util.HashMap;



/**
 * put your documentation comment here
 */
public class ActiveRuleSetPropertyGroup
        implements PropertyGroup {

    public static final String RULESETS = "RuleSets";
    public static final Object RULESETS_TOPIC = new Object();
    public static HashMap ruleSets = new HashMap();

    /**
     * Populate the ruleSets hashmap with the known rule sets and their corresponding
     * global properties.
     */
    static {
        try {
            RuleSetFactory rsf = new RuleSetFactory();
            Iterator iter = rsf.getRegisteredRuleSets();

            while (iter.hasNext()) {
                RuleSet rs = (RuleSet)iter.next();
                GlobalProperty gp = new GlobalProperty("RuleSets", rs.getName(), "true");
                RuleSetProperty rsp = new RuleSetProperty(gp, rs);
                ruleSets.put(rs.getName(), rsp);
            }
        }
        catch (Exception e) {
        e.printStackTrace();
        }
    }

    /**
    * Standard Constructor
    */
    public ActiveRuleSetPropertyGroup () {
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
            return  new PropertyPageFactory("Active PMD RuleSets", "Set the Active PMD RuleSets") {

                public PropertyPage createPropertyPage () {
                    return  new ActiveRuleSetPropertyPage();
                }
            };
        }
        return  null;
    }
}



