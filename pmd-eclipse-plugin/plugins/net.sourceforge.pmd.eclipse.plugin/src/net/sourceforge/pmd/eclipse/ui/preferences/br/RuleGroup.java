package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Holds a collection of rules as assembled by the tree widget manager.
 * 
 * @author Brian Remedios
 */
public class RuleGroup implements Comparable<RuleGroup> {

	private Comparable     id;
	private String         label;
	private String         description;
	private List<Rule>     rules = new ArrayList<Rule>();
	
	/**
	 * @param theId Object
	 * @param theDescription String
	 */
	public RuleGroup(Comparable<?> theId, String theLabel, String theDescription) {
		id = theId;
		label = theLabel;
		description = theDescription;
	}

	/**
	 * If the receiver holds just a single rule then return
	 * it, otherwise return null.
	 * 
	 * @return Rule
	 */
	public Rule soleRule() {
	    return rules.size() == 1 ? rules.get(0) : null;
	}
	
	/**
	 * @return Comparable
	 */
	public Comparable<?> id() { return id; }
	
	/**
	 * @return String
	 */
	public String description() { return description; }
	
	/**
	 * @return String
	 */
	public String label() {
		
	    if (label != null) return label;
		return id == null ? "" : id.toString();
	}
	
	/**
	 * @return int
	 */
	public int ruleCount() { return rules.size(); }
	
	/**
	 * @param ref Rule
	 */
	public void add(Rule ref) { rules.add(ref); }
	
	/**
	 * @return Rule[]
	 */
	public Rule[] rules() { 
		return rules.toArray(new Rule[rules.size()]); 
	}
	
	public void setPriority(RulePriority priority) {	    
	    for (Rule rule : rules) rule.setPriority(priority);
	}
	
	
	public boolean allRulesUseDefaultValues() {
	    
	    for (Rule rule : rules) {
	        if (!rule.usesDefaultValues()) return false;
	    }
	    
	    return true;
	}
	
	/**
	 * Returns the name of the ruleset common to all rules
	 * held by the receiver, returns null if they differ.
	 * 
	 * @return String
	 */
	public String commonRuleset() {
	    
	    if (rules.isEmpty()) return null;
	    
	    String rulesetName = rules.get(0).getRuleSetName();
	    for (int i=1; i<rules.size(); i++) {
	        if (!StringUtil.areSemanticEquals(rules.get(i).getRuleSetName(), rulesetName)) return null;
	    }
	    return rulesetName;
	}
	
   /**
     * Returns the priority level common to all rules held
     * by the receiver, returns null if they differ.
     * 
     * @return RulePriority
     */
    public RulePriority commonPriority() {
        
        if (rules.isEmpty()) return null;
        
        RulePriority priority = rules.get(0).getPriority();
        for (int i=1; i<rules.size(); i++) {
            if (rules.get(i).getPriority() != priority) return null;
        }
        return priority;
    }
	
    /**
     * Returns the value of the string property of all rules held
     * by the receiver, returns null if the values differ.
     * 
     * 
     * @return String
     */
    // TODO make this into a Generic method
    public String commonStringProperty(StringProperty desc) {
        
        if (rules.isEmpty()) return null;
        
        String value = rules.get(0).getProperty(desc);
        for (int i=1; i<rules.size(); i++) {
            if (!StringUtil.areSemanticEquals(rules.get(i).getProperty(desc), value)) return null;
        }
        return value;
    }
    
	/**
	 * @return boolean
	 */
	public boolean hasRules() { return !rules.isEmpty(); }
	
	
	public String toString() { return label() + " rules: " + ruleCount(); }

	public int compareTo(RuleGroup otherGroup) {
		
		if (id == null) return -1;
		if (otherGroup.id() == null) return -1;
		
		return id.compareTo(otherGroup.id());
	};
	
	public void setProperty(StringProperty desc, String value) {
	    for (Rule rule : rules) rule.setProperty(desc, value);
	}
}
