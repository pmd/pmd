/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
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
    private Set<ViolationSuppressor> allSuppressors;

    @Override
    public RuleViolation createViolation(Rule rule, @NonNull Node location, @NonNull String filename, @NonNull String formattedMessage) {
        return new ParametricRuleViolation<>(rule, filename, location, formattedMessage);
    }

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
        if (allSuppressors == null) {
            // lazy loaded because calling getSuppressors in constructor
            // is not safe wrt initialization of static constants
            // (order dependent otherwise)
            this.allSuppressors = new LinkedHashSet<>(getSuppressors());
            allSuppressors.add(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR);
            allSuppressors.add(ViolationSuppressor.REGEX_SUPPRESSOR);
            allSuppressors.add(ViolationSuppressor.XPATH_SUPPRESSOR);
        }
        return allSuppressors;
    }

    /** Returns the default instance (no additional suppressors, creates a ParametricRuleViolation). */
    public static RuleViolationFactory defaultInstance() {
        return DEFAULT;
    }
}
