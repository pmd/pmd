package net.sourceforge.pmd.eclipse.ui.preferences;

import java.util.List;

import net.sourceforge.pmd.RuleSet;

/**
 * This class implements a content provider for the ruleset exclude/include
 * pattern tables of the PMD Preference page
 *
 */
public class RuleSetExcludeIncludePatternContentProvider extends AbstractStructuredContentProvider {

	private final boolean exclude;

	private static final RuleSetExcludeIncludePattern[] emptyRuleSetPattern = new RuleSetExcludeIncludePattern[0];

	
	public RuleSetExcludeIncludePatternContentProvider(boolean exclude) {
		this.exclude = exclude;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public RuleSetExcludeIncludePattern[] getElements(Object inputElement) {
	    
		if (inputElement instanceof RuleSet) {
			RuleSet ruleSet = (RuleSet)inputElement;
			List<String> patterns = exclude ? ruleSet.getExcludePatterns() : ruleSet.getIncludePatterns();
			RuleSetExcludeIncludePattern[] patternList = new RuleSetExcludeIncludePattern[patterns.size()];
			for (int i = 0; i < patternList.length; i++) {
				patternList[i] = new RuleSetExcludeIncludePattern(ruleSet, exclude, i);
			}
			return patternList;
		}

		return emptyRuleSetPattern;
	}
}