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



/**
 * put your documentation comment here
 */
public class ImportedRuleSetPropertyGroup
        implements PropertyGroup {

    public static ImportedRuleSetPropertyGroup currentInstance = null;
    public static final String IMPORTED_RULESETS = "Imported_RuleSets";
    public static final String IMPORTED_PROPERTY = "Imported";
    public static HashMap importedRuleSets = new HashMap();
    public static HashMap importedRuleSetFileNames = new HashMap();
    private static final String DELIMETER = ";";


    /**
     * We need to initialize the imported rule sets statically so that they
     * are available to the other property groups
     */

    /**
    * Standard Constructor
    */
    public ImportedRuleSetPropertyGroup () {
        currentInstance = this;
        getImportedRuleSets();
    }

    public  Collection getRuleSets() {
        return importedRuleSets.values();
    }


    protected void addRuleSet(String ruleSetFileName, RuleSet rs) {
        if (!importedRuleSets.containsKey(rs.getName())) {
            importedRuleSets.put(rs.getName(), rs);
            importedRuleSetFileNames.put(rs.getName(), ruleSetFileName);
            updateImportedRuleSets();
            updateActiveRuleSets(rs);
            updatePropertyPages();
        }
    }

    protected void removeRuleSet(String ruleSetName) {
        importedRuleSets.remove(ruleSetName);  //remove the rule set from the importedRuleSets map
        importedRuleSetFileNames.remove(ruleSetName);  //remove the rule set file name from the importedRuleSetFileNames map

        ActiveRuleSetPropertyGroup.currentInstance.ruleSets.remove(ruleSetName);   //update the ActiveRuleSetPropertyGroup
        updatePropertyPages();
        updateImportedRuleSets();  //update the imported Rule Sets global property

    }

    protected void updatePropertyPages() {
        if (ActiveRuleSetPropertyPage.currentInstance != null)
            ActiveRuleSetPropertyPage.currentInstance.reinit();

        if (ConfigureRuleSetPropertyPage.currentInstance != null)
            ConfigureRuleSetPropertyPage.currentInstance.reinit();
    }

    private  void updateActiveRuleSets(RuleSet rs) {
        //update the active rule sets
        ActiveRuleSetPropertyGroup.currentInstance.addImportedRuleSet(rs);
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



