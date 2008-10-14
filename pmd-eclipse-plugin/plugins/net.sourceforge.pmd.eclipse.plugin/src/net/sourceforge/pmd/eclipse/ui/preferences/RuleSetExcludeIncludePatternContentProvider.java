package net.sourceforge.pmd.eclipse.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleSet;

/**
 * This class implements a content provider for the ruleset exclude/include
 * pattern tables of the PMD Preference page
 *
 */
public class RuleSetExcludeIncludePatternContentProvider extends AbstractStructuredContentProvider {

	private final boolean exclude;

	public RuleSetExcludeIncludePatternContentProvider(boolean exclude) {
		this.exclude = exclude;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public RuleSetExcludeIncludePattern[] getElements(Object inputElement) {
	    RuleSetExcludeIncludePattern[] result = new RuleSetExcludeIncludePattern[0];

		if (inputElement instanceof RuleSet) {
			RuleSet ruleSet = (RuleSet)inputElement;
			List<String> patterns = exclude ? ruleSet.getExcludePatterns() : ruleSet.getIncludePatterns();
			List<RuleSetExcludeIncludePattern> patternList = new ArrayList<RuleSetExcludeIncludePattern>();
			for (int i = 0; i < patterns.size(); i++) {
				patternList.add(new RuleSetExcludeIncludePattern(ruleSet, exclude, i));
			}
			result = patternList.toArray(result);
		}

		return result;
	}
}