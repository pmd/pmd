/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Thais class handles the specification of rulesets that are not defined in the
 * RuleSetFactory.getRegistererdRuleSets() method but are accessible in the claspath.</p>
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
import com.borland.primetime.ide.MessageCategory;
import com.borland.primetime.ide.Browser;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Collection;
import com.borland.primetime.properties.PropertyDialog;



public class ImportedRuleSetPropertyGroup
        implements PropertyGroup {

    public static ImportedRuleSetPropertyGroup currentInstance = null;
    public static final String IMPORTED_RULESETS = "Imported_RuleSets";
    public static final String IMPORTED_PROPERTY = "Imported";
    public static HashMap importedRuleSets = new HashMap();
    public static HashMap importedRuleSetFileNames = new HashMap();
    private static final String DELIMETER = ";";


    /**
    * Standard Constructor
    */
    public ImportedRuleSetPropertyGroup () {
        currentInstance = this;
        getImportedRuleSets();
    }

    /**
     * Get a collection of the currently imported rule sets
     * This is used by the ActiveRuleSetPropertyGroup when constructing it's list
     * of rulesets to display
     * @return Collection of rule sets
     */
    public  Collection getRuleSets() {
        return importedRuleSets.values();
    }

    /**
     * Add a new imported rule set
     * @param ruleSetFileName The file name of the new rule set
     * @param rs The rule set object
     */
    protected void addRuleSet(String ruleSetFileName, RuleSet rs) {
        if (!importedRuleSets.containsKey(rs.getName())) {
            importedRuleSets.put(rs.getName(), rs);  //register the new ruleset object
            importedRuleSetFileNames.put(rs.getName(), ruleSetFileName);  //register the ruleset file name
            updateImportedRuleSets();  //update the global property that stores the imported ruleset info
            ActiveRuleSetPropertyGroup.currentInstance.addImportedRuleSet(rs);  //notify the ActiveRuleSetPropertyGroup of the new imported rule set
            updatePropertyPages();  //update the other property pages
        }
    }

    /**
     * Remove a currently imported rule set
     * @param ruleSetName  the name of the rule set to remove
     */
    protected void removeRuleSet(String ruleSetName) {
        importedRuleSets.remove(ruleSetName);  //remove the rule set from the importedRuleSets map
        importedRuleSetFileNames.remove(ruleSetName);  //remove the rule set file name from the importedRuleSetFileNames map

        ActiveRuleSetPropertyGroup.currentInstance.ruleSets.remove(ruleSetName);   //update the ActiveRuleSetPropertyGroup
        updatePropertyPages();  //update the other property pages
        updateImportedRuleSets();  //update the imported Rule Sets global property

    }

    /**
     * Update the ActiveRUleSetPropertyPage and the ConfigureRuleSetPropertyPage so they
     * reflect the current state of the imported ruleset list
     */
    protected void updatePropertyPages() {
        if (ActiveRuleSetPropertyPage.currentInstance != null)
            ActiveRuleSetPropertyPage.currentInstance.reinit();

        if (ConfigureRuleSetPropertyPage.currentInstance != null)
            ConfigureRuleSetPropertyPage.currentInstance.reinit();
    }


    /**
     * Update the imported rule sets global property by create a delimited string out of
     * all the imported rule set file names and saving that string into the
     * global property
     */
    private void updateImportedRuleSets() {
        StringBuffer value = new StringBuffer();
        for (Iterator iter = importedRuleSetFileNames.values().iterator(); iter.hasNext(); ) {
            value.append((String)iter.next());
            if (iter.hasNext())
                value.append(DELIMETER);
        }
        GlobalProperty importedRuleSetsProps = new GlobalProperty(Constants.RULESETS, IMPORTED_PROPERTY, "");
        importedRuleSetsProps.setValue(value.toString());

    }

    /**
     * Get the imported rule set global proeprty and parse the resulting delimted string
     * into individual components.  Construct the importedRuleSets hashmap that
     * maps ruleset names to RuleSet objects.  Construct the importedRuleSetNames that
     * maps the the ruleset names to rule set file names.
     */
    private void getImportedRuleSets() {
        GlobalProperty.readProperties();
        GlobalProperty importedRuleSetsProps = new GlobalProperty(Constants.RULESETS, IMPORTED_PROPERTY, "");

        StringTokenizer  st = new StringTokenizer(importedRuleSetsProps.getValue(), DELIMETER);
        RuleSetFactory rsf = new RuleSetFactory();
        while (st.hasMoreTokens()) {
            try {
                String ruleSetFileName = st.nextToken();
                InputStream is = ClassLoader.getSystemResourceAsStream(ruleSetFileName);
                RuleSet ruleSet = rsf.createRuleSet(is);
                importedRuleSets.put(ruleSet.getName(), ruleSet);
                importedRuleSetFileNames.put(ruleSet.getName(), ruleSetFileName);
             }
            catch (Exception ex) {
            }
        }
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
        if (topic == Constants.RULESETS_TOPIC) {
            return  new PropertyPageFactory("Imported PMD RuleSets", "Set the Imported PMD RuleSets") {

                public PropertyPage createPropertyPage () {
                    return  new ImportedRuleSetPropertyPage();
                }
            };
        }
        return  null;
    }
}



