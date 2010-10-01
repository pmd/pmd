package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.RuleSet;

/**
 * A value and label extractor interface for anything implementing the RuleSet interface
 * and may be real fields or values held as properties.
 *
 * Value returned are typed as comparable to facilitate sorting. Never return null,
 * return an empty string instead.
 *
 * @author Brian Remedios
 */
public interface RuleSetFieldAccessor {

	Comparable<?> valueFor(RuleSet ruleSet);

//	Comparable<?> valueFor(RuleSetCollection collection);
	
//	Set<Comparable<?>> uniqueValuesFor(RuleSetCollection collection);
	
	String labelFor(RuleSet ruleSet);

	RuleSetFieldAccessor name = new BasicRuleSetFieldAccessor() {
		public Comparable<String> valueFor(RuleSet ruleSet) {
			return ruleSet.getName();
		}
	};
	
	RuleSetFieldAccessor fileName = new BasicRuleSetFieldAccessor() {
		public Comparable<String> valueFor(RuleSet ruleSet) {
			return ruleSet.getFileName();
		}
	};
	
	RuleSetFieldAccessor description = new BasicRuleSetFieldAccessor() {
		public Comparable<String> valueFor(RuleSet ruleSet) {
			return ruleSet.getDescription();
		}
	};
	
	RuleSetFieldAccessor size = new BasicRuleSetFieldAccessor() {
		public Comparable<Integer> valueFor(RuleSet ruleSet) {
			return ruleSet.size();
		}
	};
	
	RuleSetFieldAccessor includePatternCount = new BasicRuleSetFieldAccessor() {
		public Comparable<Integer> valueFor(RuleSet ruleSet) {
			return ruleSet.getIncludePatterns().size();
		}
	};
	
	RuleSetFieldAccessor excludePatternCount = new BasicRuleSetFieldAccessor() {
		public Comparable<Integer> valueFor(RuleSet ruleSet) {
			return ruleSet.getExcludePatterns().size();
		}
	};
}
