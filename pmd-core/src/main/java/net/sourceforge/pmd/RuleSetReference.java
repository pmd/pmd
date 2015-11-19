/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents a reference to RuleSet.
 */
public class RuleSetReference {
	private String ruleSetFileName;
	private boolean allRules;
	private Set<String> excludes = new LinkedHashSet<>(0);

	public RuleSetReference() {	}
	
	public RuleSetReference(String theFilename) {
		ruleSetFileName = theFilename;
	}
	
	public String getRuleSetFileName() {
		return ruleSetFileName;
	}

	public void setRuleSetFileName(String ruleSetFileName) {
		this.ruleSetFileName = ruleSetFileName;
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
