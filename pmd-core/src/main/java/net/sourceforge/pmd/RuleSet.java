/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
// FUTURE Implement Cloneable and clone()
public class RuleSet {

    private static final Logger LOG = Logger.getLogger(RuleSet.class.getName());
    private static final String MISSING_RULE = "Missing rule";

    private List<Rule> rules = new ArrayList<>();
    private String fileName;
    private String name = "";
    private String description = "";

    // TODO should these not be Sets or is their order important?
    private List<String> excludePatterns = new ArrayList<>(0);
    private List<String> includePatterns = new ArrayList<>(0);

    private Filter<File> filter;

    /**
     * A convenience constructor
     *
     * @param name the rule set name
     * @param theRules the rules to add to the rule set
     * @return a new rule set with the given rules added
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
     * Add a new rule to this ruleset. Note that this method does not check for
     * duplicates.
     *
     * @param rule the rule to be added
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException(MISSING_RULE);
        }
        rules.add(rule);
    }

    /**
     * Adds a rule. If a rule with the same name and language already existed
     * before in the ruleset, then the new rule will replace it. This makes sure
     * that the rule configured is overridden.
     *
     * @param rule the new rule to add
     * @return <code>true</code> if the new rule replaced an existing one,
     *         otherwise <code>false</code>.
     */
    public boolean addRuleReplaceIfExists(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException(MISSING_RULE);
        }

        boolean replaced = false;
        for (Iterator<Rule> it = rules.iterator(); it.hasNext();) {
            Rule r = it.next();
            if (r.getName().equals(rule.getName()) && r.getLanguage() == rule.getLanguage()) {
                it.remove();
                replaced = true;
            }
        }
        addRule(rule);
        return replaced;
    }

    /**
     * Only adds a rule to the ruleset if no rule with the same name for the
     * same language was added before, so that the existent rule configuration
     * won't be overridden.
     *
     * @param rule the new rule to add
     * @return <code>true</code> if the rule was added, <code>false</code>
     *         otherwise
     */
    public boolean addRuleIfNotExists(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException(MISSING_RULE);
        }

        boolean exists = false;
        for (Rule r : rules) {
            if (r.getName().equals(rule.getName()) && r.getLanguage() == rule.getLanguage()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addRule(rule);
        }
        return !exists;
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
        RuleReference ruleReference;
        if (rule instanceof RuleReference) {
            ruleReference = (RuleReference) rule;
        } else {
            RuleSetReference ruleSetReference = new RuleSetReference();
            ruleSetReference.setRuleSetFileName(ruleSetFileName);
            ruleReference = new RuleReference();
            ruleReference.setRule(rule);
            ruleReference.setRuleSetReference(ruleSetReference);
        }
        rules.add(ruleReference);
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
     *
     * @param language The Language.
     * @return <code>true</code> if a Rule for the Language uses the DFA layer,
     *         <code>false</code> otherwise.
     */
    public boolean usesDFA(Language language) {
        for (Rule r : rules) {
            if (r.getLanguage().equals(language) && r.usesDFA()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first Rule found with the given name (case-sensitive).
     *
     * Note: Since we support multiple languages, rule names are not expected to
     * be unique within any specific ruleset.
     *
     * @param ruleName the exact name of the rule to find
     * @return the rule or null if not found
     */
    public Rule getRuleByName(String ruleName) {

        for (Rule r : rules) {
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
     * Add all rules by reference from one RuleSet to this RuleSet. The rules
     * can be added as individual references, or collectively as an all rule
     * reference.
     *
     * @param ruleSet the RuleSet to add
     * @param allRules <code>true</code> if the ruleset should be added
     *            collectively or <code>false</code> to add individual
     *            references for each rule.
     */
    public void addRuleSetByReference(RuleSet ruleSet, boolean allRules) {
        addRuleSetByReference(ruleSet, allRules, (String[]) null);
    }

    /**
     * Add all rules by reference from one RuleSet to this RuleSet. The rules
     * can be added as individual references, or collectively as an all rule
     * reference.
     *
     * @param ruleSet the RuleSet to add
     * @param allRules <code>true</code> if the ruleset should be added
     *            collectively or <code>false</code> to add individual
     *            references for each rule.
     * @param excludes names of the rules that should be excluded.
     */
    public void addRuleSetByReference(RuleSet ruleSet, boolean allRules, String... excludes) {
        if (StringUtil.isEmpty(ruleSet.getFileName())) {
            throw new RuntimeException("Adding a rule by reference is not allowed with an empty rule set file name.");
        }
        RuleSetReference ruleSetReference = new RuleSetReference(ruleSet.getFileName());
        ruleSetReference.setAllRules(allRules);
        if (excludes != null) {
            ruleSetReference.setExcludes(new HashSet<>(Arrays.asList(excludes)));
        }
        for (Rule rule : ruleSet.getRules()) {
            RuleReference ruleReference = new RuleReference(rule, ruleSetReference);
            rules.add(ruleReference);
        }
    }

    /**
     * Check if a given source file should be checked by rules in this RuleSet.
     * A file should not be checked if there is an <code>exclude</code> pattern
     * which matches the file, unless there is an <code>include</code> pattern
     * which also matches the file. In other words, <code>include</code>
     * patterns override <code>exclude</code> patterns.
     *
     * @param file the source file to check
     * @return <code>true</code> if the file should be checked,
     *         <code>false</code> otherwise
     */
    public boolean applies(File file) {
        // Initialize filter based on patterns
        if (filter == null) {
            Filter<String> regexFilter = Filters.buildRegexFilterIncludeOverExclude(includePatterns, excludePatterns);
            filter = Filters.toNormalizedFileFilter(regexFilter);
        }

        return file != null ? filter.filter(file) : true;
    }

    /**
     * Triggers that start lifecycle event on each rule in this ruleset. Some
     * rules perform initialization tasks on start.
     *
     * @param ctx the current context
     */
    public void start(RuleContext ctx) {
        for (Rule rule : rules) {
            rule.start(ctx);
        }
    }

    /**
     * Executes the rules in this ruleset against each of the given nodes.
     *
     * @param acuList the node list, usually the root nodes like compilation
     *            units
     * @param ctx the current context
     */
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
            } catch (RuntimeException e) {
                if (ctx.isIgnoreExceptions()) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.log(Level.WARNING, "Exception applying rule " + rule.getName()
                            + " on file " + ctx.getSourceCodeFilename() + ", continuing with next rule",
                            e);
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Does the given Rule apply to the given LanguageVersion? If so, the
     * Language must be the same and be between the minimum and maximums
     * versions on the Rule.
     *
     * @param rule The rule.
     * @param languageVersion The language version.
     *
     * @return <code>true</code> if the given rule matches the given language,
     *         which means, that the rule would be executed.
     */
    public static boolean applies(Rule rule, LanguageVersion languageVersion) {
        final LanguageVersion min = rule.getMinimumLanguageVersion();
        final LanguageVersion max = rule.getMaximumLanguageVersion();
        return rule.getLanguage().equals(languageVersion.getLanguage())
                && (min == null || min.compareTo(languageVersion) <= 0)
                && (max == null || max.compareTo(languageVersion) >= 0);
    }

    /**
     * Triggers the end lifecycle event on each rule in the ruleset. Some rules
     * perform a final summary calculation or cleanup in the end.
     *
     * @param ctx the current context
     */
    public void end(RuleContext ctx) {
        for (Rule rule : rules) {
            rule.end(ctx);
        }
    }

    /**
     * Two rulesets are equals, if they have the same name and contain the same
     * rules.
     *
     * @param o the other ruleset to compare with
     * @return <code>true</code> if o is a ruleset with the same name and rules,
     *         <code>false</code> otherwise
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

    /**
     * Adds a new file exclusion pattern.
     *
     * @param aPattern the pattern
     */
    public void addExcludePattern(String aPattern) {
        if (excludePatterns.contains(aPattern)) {
            return;
        }

        excludePatterns.add(aPattern);
        patternsChanged();
    }

    /**
     * Adds new file exclusion patterns.
     *
     * @param someExcludePatterns the patterns
     */
    public void addExcludePatterns(Collection<String> someExcludePatterns) {
        int added = CollectionUtil.addWithoutDuplicates(someExcludePatterns, excludePatterns);
        if (added > 0) {
            patternsChanged();
        }
    }

    /**
     * Replaces the existing exclusion patterns with the given patterns.
     *
     * @param theExcludePatterns the new patterns
     */
    public void setExcludePatterns(Collection<String> theExcludePatterns) {
        if (excludePatterns.equals(theExcludePatterns)) {
            return;
        }

        excludePatterns.clear();
        CollectionUtil.addWithoutDuplicates(theExcludePatterns, excludePatterns);
        patternsChanged();
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    /**
     * Adds a new inclusion pattern.
     *
     * @param aPattern the pattern
     */
    public void addIncludePattern(String aPattern) {
        if (includePatterns.contains(aPattern)) {
            return;
        }

        includePatterns.add(aPattern);
        patternsChanged();
    }

    /**
     * Adds new inclusion patterns.
     *
     * @param someIncludePatterns the patterns
     */
    public void addIncludePatterns(Collection<String> someIncludePatterns) {
        int added = CollectionUtil.addWithoutDuplicates(someIncludePatterns, includePatterns);
        if (added > 0) {
            patternsChanged();
        }
    }

    /**
     * Replaces the existing inclusion patterns with the given patterns.
     *
     * @param theIncludePatterns the new patterns
     */
    public void setIncludePatterns(Collection<String> theIncludePatterns) {
        if (includePatterns.equals(theIncludePatterns)) {
            return;
        }

        includePatterns.clear();
        CollectionUtil.addWithoutDuplicates(theIncludePatterns, includePatterns);
        patternsChanged();
    }

    private void patternsChanged() {
        filter = null; // ensure we start with one that reflects the current
                       // patterns
    }

    /**
     * Does any Rule for the given Language use Type Resolution?
     *
     * @param language The Language.
     * @return <code>true</code> if a Rule for the Language uses Type
     *         Resolution, <code>false</code> otherwise.
     */
    public boolean usesTypeResolution(Language language) {
        for (Rule r : rules) {
            if (r.getLanguage().equals(language) && r.usesTypeResolution()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove and collect any misconfigured rules.
     *
     * @param collector the removed rules will be added to this collection
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
