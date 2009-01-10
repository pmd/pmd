package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
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
    
    public Rule soleRule() {
        
        if (ruleItems == null || ruleItems.length > 1) return null;
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
    
    public void setPriority(RulePriority priority) {
        
        if (ruleItems == null) return;
        
        for (int i=0; i<ruleItems.length; i++) {
            if (ruleItems[i] instanceof Rule) {
                ((Rule)ruleItems[i]).setPriority(priority);
            }
            if (ruleItems[i] instanceof RuleGroup) {
                ((RuleGroup)ruleItems[i]).setPriority(priority);
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
        
        for (int i=0; i<ruleItems.length; i++) {
            if (ruleItems[i] instanceof Rule) {
                selections.add((Rule)ruleItems[i]);
                continue;
            } else {
                Rule[] rules = ((RuleGroup)ruleItems[i]).rules();
                for (int r=0; r<rules.length; r++) selections.add(rules[r]);
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
}
