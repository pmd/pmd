package net.sourceforge.pmd;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents a reference to RuleSet.
 */
public class RuleSetReference {
	private String ruleSetName;
	private boolean allRules;
	private Set<String> excludes = new LinkedHashSet<String>(0);

	public String getRuleSetName() {
		return ruleSetName;
	}

	public void setRuleSetName(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}

	public boolean isAllRules() {
		return allRules;
	}

	public void setAllRules(boolean allRules) {
		this.allRules = allRules;
	}

	public Set<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(Set<String> excludes) {
		this.excludes = excludes;
	}

	public void addExclude(String name) {
		this.excludes.add(name);
	}
}
