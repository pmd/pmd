package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.GlobalProperty;
import net.sourceforge.pmd.RuleSet;
import java.util.Iterator;
import net.sourceforge.pmd.Rule;
import java.util.HashMap;
import com.borland.primetime.ide.Browser;
import com.borland.primetime.ide.MessageCategory;

/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class RuleSetProperty {
    private  GlobalProperty globalRuleSetProp;
    private RuleSet originalRuleSet;
    private RuleSet activeRuleSet;

    /**
     * These values show the selection state of the rules as contained in the GlobalProperties
     * objects
     */
    private HashMap globalRuleProps = new HashMap();

    /**
     * these values represent the current selection state of the rules as shown in the interface
     * and NOT their state as persisted in the GlobalProperties
     */
    private HashMap ruleSelectionState = new HashMap();


    public RuleSetProperty(GlobalProperty prop, RuleSet ruleSet) {
        this.globalRuleSetProp = prop;
        this.originalRuleSet = ruleSet;
        validateRules();
    }

    private String buildPropName(String ruleSetName, String ruleName) {
        return ruleSetName + ruleName;
    }
    /**
     * Construct a new ruleset based on whether indivual rules are enabled or disabled
     * within this ruleset
     */
    private void validateRules() {
        activeRuleSet = new RuleSet();
        activeRuleSet.setName(originalRuleSet.getName());
        for (Iterator iter = originalRuleSet.getRules().iterator(); iter.hasNext(); ) {
            Rule rule = (Rule)iter.next();
            String propName = buildPropName(originalRuleSet.getName(), rule.getName());
            GlobalProperty gp = new GlobalProperty("Rules", propName, "true");  //get the global property
            globalRuleProps.put(propName, gp);
            if (Boolean.valueOf(gp.getValue()).booleanValue()) {     //if the rule is enabled
                activeRuleSet.addRule(rule);  //then add the rule to the active ruleset
            }
            ruleSelectionState.put(propName, Boolean.valueOf(gp.getValue()));  //update the ruleSelectionState hashmap
        }
    }

    /**
     * update the active ruleset based ruleSelectionState settings
     * This should only be called when you want to persist the current rule selection
     * settings (e.g., the writeProperties method in the ConfigureRuleSetPropertyPage)
     */
    public void revalidateRules() {
        activeRuleSet = new RuleSet();
        activeRuleSet.setName(originalRuleSet.getName());
        for (Iterator iter = originalRuleSet.getRules().iterator(); iter.hasNext(); ) {
            Rule rule = (Rule)iter.next();
            String propName = buildPropName(originalRuleSet.getName(), rule.getName());
            boolean selectionState = ((Boolean)ruleSelectionState.get(propName)).booleanValue();
            GlobalProperty gp = (GlobalProperty)globalRuleProps.get(propName);
            gp.setValue(String.valueOf(selectionState));  //update the global properties value
            if (selectionState) {     //if the rule is enabled
                activeRuleSet.addRule(rule);
            }
        }
    }

    /**
     * Reset the rule selections to the values in the Global properties
     */
    public void resetRuleSelectionState() {
        GlobalProperty.readProperties();
        ruleSelectionState = new HashMap();
        for (Iterator iter = originalRuleSet.getRules().iterator(); iter.hasNext(); ) {
            Rule rule = (Rule)iter.next();
            String propName = buildPropName(originalRuleSet.getName(), rule.getName());
            GlobalProperty gp = (GlobalProperty)globalRuleProps.get(propName);
            Boolean selectionState = Boolean.valueOf(gp.getValue());
            ruleSelectionState.put(propName, selectionState);
        }
    }

    public RuleSet getActiveRuleSet() {
        return activeRuleSet;
    }

    public RuleSet getOriginalRuleSet() {
        return originalRuleSet;
    }

    public GlobalProperty getGlobalProperty() {
        return globalRuleSetProp;
    }

    /**
     * Gets the current selection state of the rule.  This is the state as it appears in the interface and
     * not necessarily as it is represented in the global property.  The selection state is used to set the
     * GlobalProperty once this RuleSetProperty is revalidated.
     * @param ruleName the rule name
     * @return true of the rule is selected, false otherwise
     */
    public boolean isRuleSelected(String ruleName) {
        return ((Boolean)ruleSelectionState.get(buildPropName(originalRuleSet.getName(), ruleName))).booleanValue();
    }

    /**
     * sets whether or not this rule is selected to be enabled when the ruleset property is
     * revalidated
     * @param ruleName the rule name
     * @param isSelected whether or not the rule is selected to be enabled
     */
    public void setRuleSelected(String ruleName, boolean isSelected) {
        ruleSelectionState.put(buildPropName(originalRuleSet.getName(), ruleName), new Boolean(isSelected));
    }
}