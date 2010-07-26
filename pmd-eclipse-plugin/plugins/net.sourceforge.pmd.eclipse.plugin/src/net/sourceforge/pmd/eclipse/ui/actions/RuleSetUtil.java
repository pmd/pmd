package net.sourceforge.pmd.eclipse.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

/**
 * 
 * @author Brian Remedios
 */
public class RuleSetUtil {

	private RuleSetUtil() {}
	
	public static RuleSet newCopyOf(RuleSet original) {
		
		RuleSet rs = new RuleSet();
		rs.setName(original.getName());
		rs.setDescription(original.getDescription());
		rs.setFileName(original.getFileName());
		rs.setExcludePatterns(original.getExcludePatterns());
		rs.setIncludePatterns(original.getIncludePatterns());
		rs.addRuleSet(original);
		
		return rs;
	}
	
	/**
	 * This should not really work but the ruleset hands out its 
	 * internal container....oops!  :)
	 * 
	 * @param ruleSet
	 * @param unwantedRuleNames
	 */
	public static void retainOnly(RuleSet ruleSet, Set<String> wantedRuleNames) {
		
		Collection<Rule> rules = ruleSet.getRules();
		Collection<Rule> ruleCopy = new ArrayList<Rule>(rules.size());
		ruleCopy.addAll(rules);
				
		for (Rule rule : ruleCopy) {
			if (!wantedRuleNames.contains(rule.getName())) {
				rules.remove(rule);
			}
		}
		
	}
}
