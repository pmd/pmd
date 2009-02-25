package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 */
public class RuleSelection {

    private Object[] ruleItems;
    
    public RuleSelection(Object[] theRuleItems) {
        ruleItems = theRuleItems;
    }

    public boolean hasOneRule() {
        
        if (ruleItems.length > 1) return false;
        return allRules().size() == 1;
    }
    
    public boolean hasMultipleRules() {
        return ruleItems != null && allRules().size() > 1;
    }
    
    public Rule soleRule() {
        
        if (ruleItems == null || ruleItems.length != 1) return null;
        if (ruleItems[0] instanceof Rule) return (Rule)ruleItems[0];
        if (ruleItems[0] instanceof RuleGroup) {
            return ((RuleGroup)ruleItems[0]).soleRule();
        }
        
        return null;     // should not get here
    }
    
    public boolean allSelectedRulesUseDefaultValues() {
        
        if (ruleItems == null || ruleItems.length == 0) return true;
                
        // TODO
        return true;
    }
    
    private RulePriority commonPriorityFor(Object item) {
        
        return item instanceof Rule ?
                ((Rule)item).getPriority() :
                ((RuleGroup)item).commonPriority();
    }
    
    private String commonRulesetFor(Object item) {
        
        return item instanceof Rule ?
                ((Rule)item).getRuleSetName() :
                ((RuleGroup)item).commonRuleset();
    }
    
    private String commonStringValueFor(Object item, StringProperty desc) {
        
        return item instanceof Rule ?
                ((Rule)item).getProperty(desc) :
                ((RuleGroup)item).commonStringProperty(desc);
    }
    
    public void setPriority(RulePriority priority) {
        
        if (ruleItems == null) return;
        
        for (Object ruleItem : ruleItems) {
            if (ruleItem instanceof Rule) {
                ((Rule)ruleItem).setPriority(priority);
            }
            if (ruleItem instanceof RuleGroup) {
                ((RuleGroup)ruleItem).setPriority(priority);
            }
        }
    }
    
    public int removeAllFrom(RuleSet ruleSet) {
       
        List<Rule> rules = allRules();
        if (rules.isEmpty()) return 0;
        
        Collection<Rule> currentRules = ruleSet.getRules();
        for (Rule rule : rules) currentRules.remove(rule); 
        
        return currentRules.size();
    }
    
    public List<Rule> allRules() {
        
        List<Rule> selections = new ArrayList<Rule>();
        
        if (ruleItems == null || ruleItems.length == 0) {
            return selections;
        }
        
        for (Object ruleItem : ruleItems) {
            if (ruleItem instanceof Rule) {
                selections.add((Rule)ruleItem);
                continue;
            } else {
                Rule[] rules = ((RuleGroup)ruleItem).rules();
                for (Rule rule : rules) selections.add(rule);
            }
        }
        
        return selections;
    }
    
    /**
     *  Iterates through the currently selected rules and returns
     *  their common priority setting or null if they differ.
     */
    public RulePriority commonPriority() {
        
        if (ruleItems == null || ruleItems.length == 0) return null;
        
        RulePriority priority = commonPriorityFor(ruleItems[0]);
        if (priority == null) return null;
        
        for (int i=1; i<ruleItems.length; i++) {
            if (priority != commonPriorityFor(ruleItems[i])) return null;
        }
        
       return priority;
    }
    
    /**
     *  Iterates through the currently selected rules and returns
     *  their common ruleset name or null if they differ.
     */
    public String commonRuleset() {
        
        if (ruleItems == null || ruleItems.length == 0) return null;
        
        String rulesetName = commonRulesetFor(ruleItems[0]);
        if (StringUtil.isEmpty(rulesetName)) return null;
        
        for (int i=1; i<ruleItems.length; i++) {
            if (StringUtil.areSemanticEquals(rulesetName, commonRulesetFor(ruleItems[i]))) return null;
        }
        
       return rulesetName;
    }
    
    public String commonStringValue(StringProperty desc) {
        
        if (ruleItems == null || ruleItems.length == 0) return null;
        
        String value = commonStringValueFor(ruleItems[0], desc);
        if (value == null) return null;
        
        for (int i=1; i<ruleItems.length; i++) {
            if (StringUtil.areSemanticEquals(value, commonStringValueFor(ruleItems[i], desc))) return null;
        }
        
       return value;
    }
    
    public void setValue(StringProperty desc, String value) {
        
        if (ruleItems == null || ruleItems.length == 0) return;
                
        for (Object ruleItem : ruleItems) {
            if (ruleItem instanceof Rule) {
                ((Rule)ruleItem).setProperty(desc, value);
            } else {
                ((RuleGroup)ruleItem).setProperty(desc, value);
            }
        }
    }
}
