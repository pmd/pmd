/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.internal.RuleApplicator;

/**
 * Grouping of Rules per Language in a RuleSet.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public class RuleSets {

    private final List<RuleSet> ruleSets;

    private RuleApplicator ruleApplicator;

    /**
     * Copy constructor. Deep copies RuleSets.
     *
     * @param ruleSets The RuleSets to copy.
     */
    public RuleSets(final RuleSets ruleSets) {
        List<RuleSet> rsets = new ArrayList<>();
        for (final RuleSet rs : ruleSets.ruleSets) {
            rsets.add(new RuleSet(rs));
        }
        this.ruleSets = Collections.unmodifiableList(rsets);
    }

    public RuleSets(Collection<RuleSet> ruleSets) {
        this.ruleSets = Collections.unmodifiableList(new ArrayList<>(ruleSets));
    }

    /**
     * Public constructor. Add the given rule set.
     *
     * @param ruleSet the RuleSet
     */
    public RuleSets(RuleSet ruleSet) {
        this.ruleSets = Collections.singletonList(ruleSet);
    }

    private RuleApplicator prepareApplicator() {
        return RuleApplicator.build(ruleSets.stream().flatMap(it -> it.getRules().stream())::iterator);
    }

    /**
     * Get all the RuleSets.
     *
     * @return RuleSet[]
     */
    public RuleSet[] getAllRuleSets() {
        return ruleSets.toArray(new RuleSet[0]);
    }

    public Iterator<RuleSet> getRuleSetsIterator() {
        return ruleSets.iterator();
    }

    /**
     * Return all rules from all rulesets.
     *
     * @return Set
     */
    public Set<Rule> getAllRules() {
        Set<Rule> result = new HashSet<>();
        for (RuleSet r : ruleSets) {
            result.addAll(r.getRules());
        }
        return result;
    }

    /**
     * Check if a given source file should be checked by rules in this RuleSets.
     *
     * @param file
     *            the source file to check
     * @return <code>true</code> if the file should be checked,
     *         <code>false</code> otherwise
     */
    public boolean applies(File file) {
        for (RuleSet ruleSet : ruleSets) {
            if (ruleSet.applies(file)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notify all rules of the start of processing.
     */
    public void start(RuleContext ctx) {
        for (RuleSet ruleSet : ruleSets) {
            ruleSet.start(ctx);
        }
    }

    /**
     * Apply all applicable rules to the compilation units. Applicable means the
     * language of the rules must match the language of the source (@see
     * applies).
     *
     * @param acuList
     *            the List of compilation units; the type these must have,
     *            depends on the source language
     * @param ctx
     *            the RuleContext
     */
    public void apply(List<? extends Node> acuList, RuleContext ctx) {
        if (ruleApplicator == null) {
            // initialize here instead of ctor, because some rules properties
            // are set after creating the ruleset, and jaxen xpath queries
            // initialize their XPath expressions when calling getRuleChainVisits()... fixme
            this.ruleApplicator = prepareApplicator();
        }

        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.RULE_AST_INDEXATION)) {
            ruleApplicator.index(acuList);
        }

        for (RuleSet ruleSet : ruleSets) {
            if (ruleSet.applies(ctx.getSourceCodeFile())) {
                ruleApplicator.apply(ruleSet.getRules(), ctx);
            }
        }
    }

    /**
     * Notify all rules of the end of processing.
     */
    public void end(RuleContext ctx) {
        for (RuleSet ruleSet : ruleSets) {
            ruleSet.end(ctx);
        }
    }

    /**
     * Returns the first Rule found with the given name.
     *
     * Note: Since we support multiple languages, rule names are not expected to
     * be unique within any specific ruleset.
     *
     * @param ruleName
     *            the exact name of the rule to find
     * @return the rule or null if not found
     */
    public Rule getRuleByName(String ruleName) {
        Rule rule = null;
        for (Iterator<RuleSet> i = ruleSets.iterator(); i.hasNext() && rule == null;) {
            RuleSet ruleSet = i.next();
            rule = ruleSet.getRuleByName(ruleName);
        }
        return rule;
    }

    /**
     * Determines the total count of rules that are used in all rule sets.
     *
     * @return the count
     */
    public int ruleCount() {
        int count = 0;
        for (RuleSet r : ruleSets) {
            count += r.getRules().size();
        }
        return count;
    }


    /**
     * Remove and collect any rules that report problems.
     *
     * @param collector
     */
    public void removeDysfunctionalRules(Collection<Rule> collector) {
        for (RuleSet ruleSet : ruleSets) {
            ruleSet.removeDysfunctionalRules(collector);
        }
    }

    /**
     * Retrieves a checksum of the rulesets being used. Any change to any rule
     * of any ruleset should trigger a checksum change.
     *
     * @return The checksum for this ruleset collection.
     */
    public long getChecksum() {
        long checksum = 1;
        for (final RuleSet ruleSet : ruleSets) {
            checksum = checksum * 31 + ruleSet.getChecksum();
        }
        return checksum;
    }
}
