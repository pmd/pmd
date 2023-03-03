/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cache.ChecksumAware;
import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;

/**
 * This class represents a collection of rules along with some optional filter
 * patterns that can preclude their application on specific files.
 *
 * @see Rule
 */
public class RuleSet implements ChecksumAware {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSet.class);
    private static final String MISSING_RULE = "Missing rule";
    private static final String MISSING_RULESET_DESCRIPTION = "RuleSet description must not be null";
    private static final String MISSING_RULESET_NAME = "RuleSet name must not be null";

    private final long checksum;

    private final List<Rule> rules;
    private final String fileName;
    private final String name;
    private final String description;

    /*
     * Order is unimportant, but we preserve the order given by the user to be deterministic.
     * Using Sets is useless, since Pattern does not override #equals anyway.
     */
    private final List<Pattern> excludePatterns;
    private final List<Pattern> includePatterns;

    private final Predicate<String> filter;

    /**
     * Creates a new RuleSet with the given checksum.
     *
     * @param builder
     *            A rule set builder.
     */
    private RuleSet(final RuleSetBuilder builder) {
        checksum = builder.checksum;
        fileName = builder.fileName;
        name = Objects.requireNonNull(builder.name, MISSING_RULESET_NAME);
        description = Objects.requireNonNull(builder.description, MISSING_RULESET_DESCRIPTION);
        // TODO: ideally, the rules would be unmodifiable, too. But removeDysfunctionalRules might change the rules.
        rules = builder.rules;
        excludePatterns = Collections.unmodifiableList(new ArrayList<>(builder.excludePatterns));
        includePatterns = Collections.unmodifiableList(new ArrayList<>(builder.includePatterns));

        final Predicate<String> regexFilter = PredicateUtil.buildRegexFilterIncludeOverExclude(includePatterns, excludePatterns);
        filter = PredicateUtil.toNormalizedFileFilter(regexFilter);
    }

    public RuleSet(final RuleSet rs) {
        checksum = rs.checksum;
        fileName = rs.fileName;
        name = rs.name;
        description = rs.description;

        rules = new ArrayList<>(rs.rules.size());
        for (final Rule rule : rs.rules) {
            rules.add(rule.deepCopy());
        }

        excludePatterns = rs.excludePatterns; // we can share immutable lists of immutable elements
        includePatterns = rs.includePatterns;
        filter = rs.filter; // filters are immutable, can be shared
    }

    /**
     * Creates a new ruleset containing a single rule. The ruleset will
     * have default description, name, and null file name.
     *
     * @param rule The rule being created
     *
     * @return The newly created RuleSet
     */
    public static RuleSet forSingleRule(final Rule rule) {
        final long checksum;
        if (rule instanceof XPathRule) {
            checksum = ((XPathRule) rule).getXPathExpression().hashCode();
        } else {
            // TODO : Is this good enough? all properties' values + rule name
            checksum = rule.getPropertiesByPropertyDescriptor().values().hashCode() * 31 + rule.getName().hashCode();
        }

        final RuleSetBuilder builder =
            new RuleSetBuilder(checksum)
                .withName(rule.getName())
                .withDescription("RuleSet for " + rule.getName());
        builder.addRule(rule);
        return builder.build();
    }


    /**
     * Creates a new ruleset with the given metadata such as name, description,
     * fileName, exclude/include patterns are used. The rules are taken from the given
     * collection.
     *
     * <p><strong>Note:</strong> The rule instances are shared between the collection
     * and the new ruleset (copy-by-reference). This might lead to concurrency issues,
     * if the rules of the collection are also referenced by other rulesets and used
     * in different threads.
     * </p>
     *
     * @param name            the name of the ruleset
     * @param description     the description
     * @param fileName        the filename
     * @param excludePatterns list of exclude patterns
     * @param includePatterns list of include patterns, that override the exclude patterns
     * @param rules           the collection with the rules to add to the new ruleset
     *
     * @return the new ruleset
     *
     * @throws NullPointerException If any parameter is null, or the collections contain null elements
     */
    public static RuleSet create(String name,
                                 String description,
                                 String fileName,
                                 Collection<Pattern> excludePatterns,
                                 Collection<Pattern> includePatterns,
                                 Iterable<? extends Rule> rules) {
        RuleSetBuilder builder = new RuleSetBuilder(0L); // TODO: checksum missing
        builder.withName(name)
               .withDescription(description)
               .withFileName(fileName)
               .replaceFileExclusions(excludePatterns)
               .replaceFileInclusions(includePatterns);
        for (Rule rule : rules) {
            builder.addRule(rule);
        }
        return builder.build();
    }

    /**
     * Creates a copy of the given ruleset. All properties like name, description, fileName
     * and exclude/include patterns are copied.
     *
     * <p><strong>Note:</strong> The rule instances are shared between the original
     * and the new ruleset (copy-by-reference). This might lead to concurrency issues,
     * if the original ruleset and the new ruleset are used in different threads.
     * </p>
     *
     * @param original the original rule set to copy from
     *
     * @return the copy
     */
    public static RuleSet copy(RuleSet original) {
        return new RuleSet(original);
    }

    /* package */ static class RuleSetBuilder {

        public String description;
        public String name;
        public String fileName;
        private final List<Rule> rules = new ArrayList<>();
        private final Set<Pattern> excludePatterns = new LinkedHashSet<>();
        private final Set<Pattern> includePatterns = new LinkedHashSet<>();
        private final long checksum;

        /* package */ RuleSetBuilder(final long checksum) {
            this.checksum = checksum;
        }

        /** Copy constructor. Takes the same checksum as the original ruleset. */
        /* package */ RuleSetBuilder(final RuleSet original) {
            checksum = original.getChecksum();
            this.withName(original.getName())
                .withDescription(original.getDescription())
                .withFileName(original.getFileName())
                .replaceFileExclusions(original.getFileExclusions())
                .replaceFileInclusions(original.getFileInclusions());
            addRuleSet(original);
        }

        /**
         * Add a new rule to this ruleset. Note that this method does not check
         * for duplicates.
         *
         * @param newRule
         *            the rule to be added
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRule(final Rule newRule) {
            if (newRule == null) {
                throw new IllegalArgumentException(MISSING_RULE);
            }

            // check for duplicates - adding more than one rule with the same name will
            // be problematic - see #RuleSet.getRuleByName(String)
            for (Rule rule : rules) {
                if (rule.getName().equals(newRule.getName()) && rule.getLanguage().equals(newRule.getLanguage())) {
                    LOG.warn("The rule with name {} is duplicated. "
                            + "Future versions of PMD will reject to load such rulesets.",
                            newRule.getName());
                    break;
                }
            }

            rules.add(newRule);
            return this;
        }

        /**
         * Finds an already added rule by same name and language, if it already exists.
         * @param rule the rule to search
         * @return the already added rule or <code>null</code> if no rule was added yet to the builder.
         */
        Rule getExistingRule(final Rule rule) {
            for (Rule r : rules) {
                if (r.getName().equals(rule.getName()) && r.getLanguage().equals(rule.getLanguage())) {
                    return r;
                }
            }

            return null;
        }

        /**
         * Checks, whether a rule with the same name and language already exists in the
         * ruleset.
         * @param rule to rule to check
         * @return <code>true</code> if the rule already exists, <code>false</code> if the given
         *     rule is the first configuration of this rule.
         */
        boolean hasRule(final Rule rule) {
            return getExistingRule(rule) != null;
        }

        /**
         * Adds a rule. If a rule with the same name and language already
         * existed before in the ruleset, then the new rule will replace it.
         * This makes sure that the rule configured is overridden.
         *
         * @param rule
         *            the new rule to add
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleReplaceIfExists(final Rule rule) {
            if (rule == null) {
                throw new IllegalArgumentException(MISSING_RULE);
            }

            for (final Iterator<Rule> it = rules.iterator(); it.hasNext();) {
                final Rule r = it.next();
                if (r.getName().equals(rule.getName()) && r.getLanguage().equals(rule.getLanguage())) {
                    it.remove();
                }
            }
            addRule(rule);
            return this;
        }

        /**
         * Only adds a rule to the ruleset if no rule with the same name for the
         * same language was added before, so that the existent rule
         * configuration won't be overridden.
         *
         * @param ruleOrRef
         *            the new rule to add
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleIfNotExists(final Rule ruleOrRef) {
            if (ruleOrRef == null) {
                throw new IllegalArgumentException(MISSING_RULE);
            }

            // resolve the underlying rule, to avoid adding duplicated rules
            // if the rule has been renamed/merged and moved at the same time
            Rule rule = ruleOrRef;
            while (rule instanceof RuleReference) {
                rule = ((RuleReference) rule).getRule();
            }

            boolean exists = hasRule(rule);
            if (!exists) {
                addRule(ruleOrRef);
            }
            return this;
        }

        /**
         * Add a new rule by reference to this ruleset.
         *
         * @param ruleSetFileName
         *            the ruleset which contains the rule
         * @param rule
         *            the rule to be added
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleByReference(final String ruleSetFileName, final Rule rule) {
            if (StringUtils.isBlank(ruleSetFileName)) {
                throw new RuntimeException(
                        "Adding a rule by reference is not allowed with an empty rule set file name.");
            }
            if (rule == null) {
                throw new IllegalArgumentException("Cannot add a null rule reference to a RuleSet");
            }
            final RuleReference ruleReference;
            if (rule instanceof RuleReference) {
                ruleReference = (RuleReference) rule;
            } else {
                final RuleSetReference ruleSetReference = new RuleSetReference(ruleSetFileName);
                ruleReference = new RuleReference(rule, ruleSetReference);
            }
            rules.add(ruleReference);
            return this;
        }

        /**
         * Add all rules of a whole RuleSet to this RuleSet
         *
         * @param ruleSet
         *            the RuleSet to add
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleSet(final RuleSet ruleSet) {
            rules.addAll(ruleSet.getRules());
            return this;
        }

        /**
         * Add all rules by reference from one RuleSet to this RuleSet. The
         * rules can be added as individual references, or collectively as an
         * all rule reference.
         *
         * @param ruleSet
         *            the RuleSet to add
         * @param allRules
         *            <code>true</code> if the ruleset should be added
         *            collectively or <code>false</code> to add individual
         *            references for each rule.
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleSetByReference(final RuleSet ruleSet, final boolean allRules) {
            return addRuleSetByReference(ruleSet, allRules, (String[]) null);
        }

        /**
         * Add all rules by reference from one RuleSet to this RuleSet. The
         * rules can be added as individual references, or collectively as an
         * all rule reference.
         *
         * @param ruleSet
         *            the RuleSet to add
         * @param allRules
         *            <code>true</code> if the ruleset should be added
         *            collectively or <code>false</code> to add individual
         *            references for each rule.
         * @param excludes
         *            names of the rules that should be excluded.
         * @return The same builder, for a fluid programming interface
         */
        public RuleSetBuilder addRuleSetByReference(final RuleSet ruleSet, final boolean allRules,
                final String... excludes) {
            if (StringUtils.isBlank(ruleSet.getFileName())) {
                throw new RuntimeException(
                        "Adding a rule by reference is not allowed with an empty rule set file name.");
            }
            final RuleSetReference ruleSetReference;
            if (excludes == null) {
                ruleSetReference = new RuleSetReference(ruleSet.getFileName(), allRules);
            } else {
                ruleSetReference = new RuleSetReference(ruleSet.getFileName(), allRules, new LinkedHashSet<>(Arrays.asList(excludes)));
            }

            for (final Rule rule : ruleSet.getRules()) {
                final RuleReference ruleReference = new RuleReference(rule, ruleSetReference);
                rules.add(ruleReference);
            }
            return this;
        }

        /**
         * Adds some new file exclusion patterns.
         *
         * @param p1   The first pattern
         * @param rest Additional patterns
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder withFileExclusions(Pattern p1, Pattern... rest) {
            Objects.requireNonNull(p1, "Pattern was null");
            Objects.requireNonNull(rest, "Other patterns was null");
            excludePatterns.add(p1);
            for (Pattern p : rest) {
                Objects.requireNonNull(p, "Pattern was null");
                excludePatterns.add(p);
            }
            return this;
        }

        /**
         * Adds some new file exclusion patterns.
         *
         * @param patterns Exclusion patterns to add
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder withFileExclusions(Collection<? extends Pattern> patterns) {
            Objects.requireNonNull(patterns, "Pattern collection was null");
            for (Pattern p : patterns) {
                Objects.requireNonNull(p, "Pattern was null");
                excludePatterns.add(p);
            }
            return this;
        }

        /**
         * Replaces the existing exclusion patterns with the given patterns.
         *
         * @param patterns Exclusion patterns to set
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder replaceFileExclusions(Collection<? extends Pattern> patterns) {
            Objects.requireNonNull(patterns, "Pattern collection was null");
            excludePatterns.clear();
            for (Pattern p : patterns) {
                Objects.requireNonNull(p, "Pattern was null");
                excludePatterns.add(p);
            }
            return this;
        }


        /**
         * Adds some new file inclusion patterns.
         *
         * @param p1   The first pattern
         * @param rest Additional patterns
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder withFileInclusions(Pattern p1, Pattern... rest) {
            Objects.requireNonNull(p1, "Pattern was null");
            Objects.requireNonNull(rest, "Other patterns was null");
            includePatterns.add(p1);
            for (Pattern p : rest) {
                Objects.requireNonNull(p, "Pattern was null");
                includePatterns.add(p);
            }
            return this;
        }

        /**
         * Adds some new file inclusion patterns.
         *
         * @param patterns Inclusion patterns to add
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder withFileInclusions(Collection<? extends Pattern> patterns) {
            Objects.requireNonNull(patterns, "Pattern collection was null");
            for (Pattern p : patterns) {
                Objects.requireNonNull(p, "Pattern was null");
                includePatterns.add(p);
            }
            return this;
        }

        /**
         * Replaces the existing inclusion patterns with the given patterns.
         *
         * @param patterns Inclusion patterns to set
         *
         * @return This builder
         *
         * @throws NullPointerException If any of the specified patterns is null
         */
        public RuleSetBuilder replaceFileInclusions(Collection<? extends Pattern> patterns) {
            Objects.requireNonNull(patterns, "Pattern collection was null");
            includePatterns.clear();
            for (Pattern p : patterns) {
                Objects.requireNonNull(p, "Pattern was null");
                includePatterns.add(p);
            }
            return this;
        }

        public RuleSetBuilder withFileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public RuleSetBuilder withName(final String name) {
            this.name = Objects.requireNonNull(name, MISSING_RULESET_NAME);
            return this;
        }

        public RuleSetBuilder withDescription(final String description) {
            this.description = Objects.requireNonNull(description, MISSING_RULESET_DESCRIPTION);
            return this;
        }

        public boolean hasDescription() {
            return this.description != null;
        }

        public String getName() {
            return name;
        }

        public RuleSet build() {
            return new RuleSet(this);
        }

        public void filterRulesByPriority(RulePriority minimumPriority) {
            Iterator<Rule> iterator = rules.iterator();
            while (iterator.hasNext()) {
                Rule rule = iterator.next();
                if (rule.getPriority().compareTo(minimumPriority) > 0) {
                    LOG.debug("Removing rule {} due to priority: {} required: {}",
                            rule.getName(), rule.getPriority(), minimumPriority);
                    iterator.remove();
                }
            }
        }
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
     * Returns the actual Collection of rules in this ruleset
     *
     * @return a Collection with the rules. All objects are of type {@link Rule}
     */
    public Collection<Rule> getRules() {
        return rules;
    }


    /**
     * Returns the first Rule found with the given name (case-sensitive).
     *
     * Note: Since we support multiple languages, rule names are not expected to
     * be unique within any specific ruleset.
     *
     * @param ruleName
     *            the exact name of the rule to find
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
     * Check if a given source file should be checked by rules in this RuleSet.
     * A file should not be checked if there is an <code>exclude</code> pattern
     * which matches the file, unless there is an <code>include</code> pattern
     * which also matches the file. In other words, <code>include</code>
     * patterns override <code>exclude</code> patterns.
     *
     * @param qualFileName the source path to check
     *
     * @return <code>true</code> if the file should be checked,
     *     <code>false</code> otherwise
     */
    // TODO get rid of this overload
    @InternalApi
    public boolean applies(String qualFileName) {
        return filter.test(qualFileName);
    }

    /**
     * Check if a given source file should be checked by rules in this RuleSet.
     * A file should not be checked if there is an <code>exclude</code> pattern
     * which matches the file, unless there is an <code>include</code> pattern
     * which also matches the file. In other words, <code>include</code>
     * patterns override <code>exclude</code> patterns.
     *
     * @param file a text file
     *
     * @return <code>true</code> if the file should be checked,
     *     <code>false</code> otherwise
     */
    boolean applies(TextFile file) {
        return applies(file.getDisplayName());
    }

    /**
     * Does the given Rule apply to the given LanguageVersion? If so, the
     * Language must be the same and be between the minimum and maximums
     * versions on the Rule.
     *
     * @param rule
     *            The rule.
     * @param languageVersion
     *            The language version.
     *
     * @return <code>true</code> if the given rule matches the given language,
     *         which means, that the rule would be executed.
     *
     * @deprecated This is internal API, removed in PMD 7. You should
     * not use a ruleset directly.
     */
    @Deprecated
    @InternalApi
    public static boolean applies(Rule rule, LanguageVersion languageVersion) {
        final LanguageVersion min = rule.getMinimumLanguageVersion();
        final LanguageVersion max = rule.getMaximumLanguageVersion();
        assert rule.getLanguage() != null : "Rule has no language " + rule;
        return rule.getLanguage().equals(languageVersion.getLanguage())
                && (min == null || min.compareTo(languageVersion) <= 0)
                && (max == null || max.compareTo(languageVersion) >= 0);
    }

    /**
     * Two rulesets are equals, if they have the same name and contain the same
     * rules.
     *
     * @param o
     *            the other ruleset to compare with
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the file exclusion patterns as an unmodifiable list.
     */
    public List<Pattern> getFileExclusions() {
        return excludePatterns;
    }

    /**
     * Returns the file inclusion patterns as an unmodifiable list.
     */
    public List<Pattern> getFileInclusions() {
        return includePatterns;
    }


    /**
     * Remove and collect any misconfigured rules.
     * TODO remove this method. This mutates rulesets for nothing. Whether
     *  a rule is dysfunctional or not should be checked when it is initialized.
     *
     * @param collector
     *            the removed rules will be added to this collection
     *
     * @deprecated This is internal API, removed in PMD 7. You should
     * not use a ruleset directly.
     */
    @Deprecated
    @InternalApi
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

    @Override
    public long getChecksum() {
        return checksum;
    }
}
