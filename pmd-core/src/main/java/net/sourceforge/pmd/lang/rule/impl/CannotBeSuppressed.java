/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.impl;

import net.sourceforge.pmd.lang.rule.Rule;

/**
 * Marker interface for rules that cannot be suppressed. Those rules
 * are excluded from {@link UnnecessaryPmdSuppressionRule}. Other rules
 * must run before that rule for their violations to be handled properly
 * by the rule. As of PMD 7.21.0, the only rule that needs to implement
 * this interface is {@link UnnecessaryPmdSuppressionRule} itself.
 *
 * @since 7.21.0
 */
public interface CannotBeSuppressed extends Rule {
}
