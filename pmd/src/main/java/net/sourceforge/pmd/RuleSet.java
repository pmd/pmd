/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;

/**
 * This class represents a collection of rules along with some optional filter
 * patterns that can preclude their application on specific files.
 *
 * @see Rule
 */
//FUTURE Implement Cloneable and clone()
public class RuleSet {

    private static final Logger LOG = Logger.getLogger(RuleSet.class.getName());

	private List<Rule> rules = new ArrayList<Rule>();
	private String fileName;
	private String name = "";
	private String description = "";
	
	// TODO should these not be Sets or is their order important?
	private List<String> excludePatterns = new ArrayList<String>(0);
	private List<String> includePatterns = new ArrayList<String>(0);

	private Filter<File> filter;

	/**
	 * A convenience constructor
	 * 
	 * @param name
	 * @param theRules
	 * @return
	 */
	public static RuleSet createFor(String name, Rule... theRules) {
		
		RuleSet rs = new RuleSet();
		rs.setName(name);
		for (Rule rule : theRules) {
			rs.addRule(rule);
		}
		return rs;
	}
	
	/**
	 * Returns the number of rules in this ruleset
	 *
	 * @return an int representing the number of rules
	 */
	public int size() {
		return rules.size();
	}

	/**
	 * Add a new rule to this ruleset
	 *
	 * @param rule the rule to be added
	 */
	public void addRule(Rule rule) {
		if (rule == null) {
			throw new IllegalArgumentException("Missing rule");
		}
		rules.add(rule);
	}

	/**
	 * Add a new rule by reference to this ruleset.
	 *
	 * @param ruleSetFileName the ruleset which contains the rule
	 * @param rule the rule to be added
	 */
	public void addRuleByReference(String ruleSetFileName, Rule rule) {
		if (StringUtil.isEmpty(ruleSetFileName)) {
			throw new RuntimeException("Adding a rule by reference is not allowed with an empty rule set file name.");
		}
		if (rule == null) {
			throw new IllegalArgumentException("Cannot add a null rule reference to a RuleSet");
		}
		if (!(rule instanceof RuleReference)) {
			RuleSetReference ruleSetReference = new RuleSetReference();
			ruleSetReference.setRuleSetFileName(ruleSetFileName);
			RuleReference ruleReference = new RuleReference();
			ruleReference.setRule(rule);
			ruleReference.setRuleSetReference(ruleSetReference);
			rule = ruleReference;
		}
		rules.add(rule);
	}

	/**
	 * Returns the actual Collection of rules in this ruleset
	 *
	 * @return a Collection with the rules. All objects are of type {@link Rule}
	 */
	public Collection<Rule> getRules() {
		return rules;
	}

	/**
	 * Does any Rule for the given Language use the DFA layer?
	 * @param language The Language.
	 * @return <code>true</code> if a Rule for the Language uses the DFA layer,
	 * <code>false</code> otherwise.
	 */
	public boolean usesDFA(Language language) {
		for (Rule r : rules) {
			if (r.getLanguage().equals(language)) {
				if (r.usesDFA()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the first Rule found with the given name (case-sensitive).
	 * 
	 * Note: Since we support multiple languages, rule names 
	 * are not expected to be unique within any specific
	 * ruleset.
	 *
	 * @param ruleName the exact name of the rule to find
	 * @return the rule or null if not found
	 */
	public Rule getRuleByName(String ruleName) {
		
		for (Iterator<Rule> i = rules.iterator(); i.hasNext();) {
			Rule r = i.next();
			if (r.getName().equals(ruleName)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Add a whole RuleSet to this RuleSet
	 *
	 * @param ruleSet the RuleSet to add
	 */
	public void addRuleSet(RuleSet ruleSet) {
		rules.addAll(rules.size(), ruleSet.getRules());
	}

	/**
	 * Add all rules by reference from one RuleSet to this RuleSet.  The rules
	 * can be added as individual references, or collectively as an all rule
	 * reference.
	 *
	 * @param ruleSet the RuleSet to add
	 * @param allRules 
	 */
	public void addRuleSetByReference(RuleSet ruleSet, boolean allRules) {
		if (StringUtil.isEmpty(ruleSet.getFileName())) {
			throw new RuntimeException("Adding a rule by reference is not allowed with an empty rule set file name.");
		}
		RuleSetReference ruleSetReference = new RuleSetReference(ruleSet.getFileName());
		ruleSetReference.setAllRules(allRules);
		for (Rule rule : ruleSet.getRules()) {
			RuleReference ruleReference = new RuleReference(rule, ruleSetReference);
			rules.add(ruleReference);
		}
	}

	/**
	 * Check if a given source file should be checked by rules in this RuleSet.  A file
	 * should not be checked if there is an <code>exclude</code> pattern which matches
	 * the file, unless there is an <code>include</code> pattern which also matches
	 * the file.  In other words, <code>include</code> patterns override <code>exclude</code>
	 * patterns.
	 *
	 * @param file the source file to check
	 * @return <code>true</code> if the file should be checked, <code>false</code> otherwise
	 */
	public boolean applies(File file) {
		// Initialize filter based on patterns
		if (filter == null) {
			Filter<String> regexFilter = Filters.buildRegexFilterIncludeOverExclude(includePatterns, excludePatterns);
			filter = Filters.toNormalizedFileFilter(regexFilter);
		}

		return file != null ? filter.filter(file) : true;
	}

	public void start(RuleContext ctx) {
		for (Rule rule : rules) {
			rule.start(ctx);
		}
	}

	public void apply(List<? extends Node> acuList, RuleContext ctx) {
		long start = System.nanoTime();
		for (Rule rule : rules) {
            try {
                if (!rule.usesRuleChain() && applies(rule, ctx.getLanguageVersion())) {
                    rule.apply(acuList, ctx);
                    long end = System.nanoTime();
                    Benchmarker.mark(Benchmark.Rule, rule.getName(), end - start, 1);
                    start = end;
                }
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable t) {
                LOG.log(Level.WARNING, "Exception applying rule " + rule.getName() + ", continuing with next rule", t);
            }
		}
	}

	/**
	 * Does the given Rule apply to the given LanguageVersion?  If so, the
	 * Language must be the same and be between the minimum and maximums
	 * versions on the Rule.
	 * 
	 * @param rule The rule.
	 * @param languageVersion The language version.
	 */
	public static boolean applies(Rule rule, LanguageVersion languageVersion) {
		final LanguageVersion min = rule.getMinimumLanguageVersion();
		final LanguageVersion max = rule.getMinimumLanguageVersion();
		return rule.getLanguage().equals(languageVersion.getLanguage())
		&& (min == null || min.compareTo(languageVersion) <= 0)
		&& (max == null || max.compareTo(languageVersion) >= 0);
	}

	public void end(RuleContext ctx) {
		for (Rule rule : rules) {
			rule.end(ctx);
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RuleSet)) {
			return false; // Trivial
		}

		if (this == o) {
			return true; // Basic equality
		}

		RuleSet ruleSet = (RuleSet) o;
		return getName().equals(ruleSet.getName()) && getRules().equals(ruleSet.getRules());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode() + 13 * getRules().hashCode();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getExcludePatterns() {
		return excludePatterns;
	}

	public void addExcludePattern(String aPattern) {

		if (excludePatterns.contains(aPattern)) return;
		
		excludePatterns.add(aPattern);
		patternsChanged();
	}
	
	public void addExcludePatterns(Collection<String> someExcludePatterns) {
		
		int added = CollectionUtil.addWithoutDuplicates(someExcludePatterns, excludePatterns);
		if (added > 0) patternsChanged();
	}

	public void setExcludePatterns(Collection<String> theExcludePatterns) {
		
		if (excludePatterns.equals(theExcludePatterns)) return;
		
		excludePatterns.clear();
		CollectionUtil.addWithoutDuplicates(theExcludePatterns, excludePatterns);
		patternsChanged();
	}

	public List<String> getIncludePatterns() {
		return includePatterns;
	}

	public void addIncludePattern(String aPattern) {
		
		if (includePatterns.contains(aPattern)) return;
		
		includePatterns.add(aPattern);
		patternsChanged();
	}

	public void addIncludePatterns(Collection<String> someIncludePatterns) {

		int added = CollectionUtil.addWithoutDuplicates(someIncludePatterns, includePatterns);
		if (added > 0) patternsChanged();
	}

	public void setIncludePatterns(Collection<String> theIncludePatterns) {

		if (includePatterns.equals(theIncludePatterns)) return;
		
		includePatterns.clear();
		CollectionUtil.addWithoutDuplicates(theIncludePatterns, includePatterns);
		patternsChanged();
	}

	private void patternsChanged() {
		filter = null;	// ensure we start with one that reflects the current patterns
	}
	
	/**
	 * Does any Rule for the given Language use Type Resolution?
	 * @param language The Language.
	 * @return <code>true</code> if a Rule for the Language uses Type Resolution,
	 * <code>false</code> otherwise.
	 */
	public boolean usesTypeResolution(Language language) {
		for (Rule r : rules) {
			if (r.getLanguage().equals(language)) {
				if (r.usesTypeResolution()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove and collect any misconfigured rules.
	 * 
	 * @param collector
	 */
	public void removeDysfunctionalRules(Collection<Rule> collector) {
		
		Iterator<Rule> iter = rules.iterator();
		
		while (iter.hasNext()) {
			Rule rule = iter.next();
			if (rule.dysfunctionReason() != null) {
				iter.remove();
				collector.add(rule);
			}
		}
	}
	
}
