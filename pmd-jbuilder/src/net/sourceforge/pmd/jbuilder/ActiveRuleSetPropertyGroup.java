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


package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.GlobalProperty;
import com.borland.primetime.properties.PropertyGroup;
import com.borland.primetime.properties.PropertyPage;
import com.borland.primetime.properties.PropertyPageFactory;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;

import java.util.HashMap;
import java.util.Iterator;


/**
 * put your documentation comment here
 */
public class ActiveRuleSetPropertyGroup
        implements PropertyGroup {

    public static ActiveRuleSetPropertyGroup currentInstance = null;
    public HashMap ruleSets = new HashMap();

    /**
     * Standard Constructor
     */
    public ActiveRuleSetPropertyGroup() {
        currentInstance = this;
    }

    private void getImportedRuleSets() {
        for (Iterator e = ImportedRuleSetPropertyGroup.currentInstance.getRuleSets().iterator(); e.hasNext();) {
            RuleSet rs = (RuleSet) e.next();
            GlobalProperty gp = new GlobalProperty(Constants.RULESETS, rs.getName(), "true");
            RuleSetProperty rsp = new RuleSetProperty(gp, rs);
            ruleSets.put(rs.getName(), rsp);
        }
    }

    protected void addImportedRuleSet(RuleSet rs) {
        GlobalProperty gp = new GlobalProperty(Constants.RULESETS, rs.getName(), "true");
        RuleSetProperty rsp = new RuleSetProperty(gp, rs);
        ruleSets.put(rs.getName(), rsp);
    }

    /**
     * Called by JBuilder
     */
    public void initializeProperties() {
        /**
         * Populate the ruleSets hashmap with the known rule sets and their corresponding
         * global properties.
         */
        try {
            GlobalProperty.readProperties();
            RuleSetFactory rsf = new RuleSetFactory();
            Iterator iter = rsf.getRegisteredRuleSets();

            while (iter.hasNext()) {
                RuleSet rs = (RuleSet) iter.next();
                GlobalProperty gp = new GlobalProperty(Constants.RULESETS, rs.getName(), "true");
                RuleSetProperty rsp = new RuleSetProperty(gp, rs);
                ruleSets.put(rs.getName(), rsp);
            }
            getImportedRuleSets();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create the panel that will go in the property page
     *
     * @param topic Topic of page (represented as a tab in the property page)
     * @return Factory for creating property pages - this factory will create RuleSetPropertyPages
     */
    public PropertyPageFactory getPageFactory(Object topic) {
        if (topic == Constants.RULESETS_TOPIC) {
            return new PropertyPageFactory("Active PMD RuleSets", "Set the Active PMD RuleSets") {

                public PropertyPage createPropertyPage() {
                    return new ActiveRuleSetPropertyPage();
                }
            };
        }
        return null;
    }
}



