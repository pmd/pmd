package net.sourceforge.pmd.ui.preferences;

import java.util.List;

import net.sourceforge.pmd.RuleSet;

/**
 * Helper class to display rule set exclude/include patterns in a table
 * 
 * @version $Revision$
 * 
 * $Log$
 */
public class RuleSetExcludeIncludePattern {
	private final RuleSet ruleSet;
	private final boolean exclude;
	private final int index;

	/**
	 * Constructor with a RuleSet object and an index
	 */
	public RuleSetExcludeIncludePattern(RuleSet ruleSet, boolean exclude, int index) {
		this.ruleSet = ruleSet;
		this.exclude = exclude;
		this.index = index;
	}

	/**
	 * Returns the pattern.
	 * @return String
	 */
	public String getPattern() {
		List patterns = getPatterns();
		return (String)patterns.get(index);
	}

	/**
	 * Sets the pattern.
	 * @param value The value to set
	 */
	public void setPattern(String value) {
		List patterns = getPatterns();
		patterns.set(index, value);
	}

	private List getPatterns() {
		return exclude ? ruleSet.getExcludePatterns() : ruleSet.getIncludePatterns();
	}
}
