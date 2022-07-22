/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * This is a functional implementation of {@link RuleViolationFactory}.
 * It uses only the standard {@link ViolationSuppressor}s (constants in the interface).
 * It may be extended to add more suppression options.
 *
 * <p>Implementations should be internal. Only the interface should be exposed.
 */
public class DefaultRuleViolationFactory implements RuleViolationFactory {
    // todo move to package reporting

    private static final DefaultRuleViolationFactory DEFAULT = new DefaultRuleViolationFactory();

    // volatile for lazy init - see #getAllSuppressors
    private volatile Set<ViolationSuppressor> allSuppressors; // NOPMD volatile needed for lazy init

    @Override
    public SuppressedViolation suppressOrNull(Node location, RuleViolation violation) {
        for (ViolationSuppressor suppressor : getAllSuppressors()) {
            SuppressedViolation suppressed = suppressor.suppressOrNull(violation, location);
            if (suppressed != null) {
                return suppressed;
            }
        }
        return null;
    }

    /**
     * Returns a list of additional suppressors for this language. These
     * are added to regular //NOPMD, regex and XPath suppression.
     */
    protected List<ViolationSuppressor> getSuppressors() {
        return Collections.emptyList();
    }

    private Set<ViolationSuppressor> getAllSuppressors() {
        Set<ViolationSuppressor> result = allSuppressors;
        if (result == null) {
            // lazy loaded because calling getSuppressors in constructor
            // is not safe wrt initialization of static constants
            // (order dependent otherwise)
            result = new LinkedHashSet<>(getSuppressors());
            result.add(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR);
            result.add(ViolationSuppressor.REGEX_SUPPRESSOR);
            result.add(ViolationSuppressor.XPATH_SUPPRESSOR);

            // note 1: allSuppressors must be volatile to avoid other threads seeing the HashSet under construction
            // note 2: multiple threads might create their own HashSets and the last HashSet is stored and overwrites
            // previously created HashSets. This is ok, because this method is supposed to be idempotent.
            allSuppressors = result;
        }
        return result;
    }

    /** Returns the default instance (no additional suppressors, creates a ParametricRuleViolation). */
    public static RuleViolationFactory defaultInstance() {
        return DEFAULT;
    }
}
