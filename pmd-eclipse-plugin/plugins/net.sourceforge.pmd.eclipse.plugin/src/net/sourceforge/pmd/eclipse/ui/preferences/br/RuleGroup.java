package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;

/**
 * Holds a collection of rules as assembled by the tree widget manager.
 * 
 * @author Brian Remedios
 */
public class RuleGroup implements Comparable<RuleGroup> {

	private Comparable id;
	private String     label;
	private String     description;
	private List<Rule> rules = new ArrayList<Rule>();
	
	/**
	 * @param theId Object
	 * @param theDescription String
	 */
	public RuleGroup(Comparable theId, String theLabel, String theDescription) {
		id = theId;
		label = theLabel;
		description = theDescription;
	}

	/**
	 * @return Comparable
	 */
	public Comparable id() { return id; }
	
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
}
