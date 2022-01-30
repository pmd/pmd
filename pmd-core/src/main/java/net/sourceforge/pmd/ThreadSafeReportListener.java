/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * Marker interface for report listeners that, being thread-safe, need not
 * extra synchronization.
 *
 * Thread-safety is required only for concurrently notifying about different files.
 * Same file violations are guaranteed to be reported serially.
 *
 * @deprecated All entry points of PMD that allowed usage of this are now deprecated.
 *     This will be replaced by another TBD mechanism in PMD 7.
 */
@Deprecated
public interface ThreadSafeReportListener {
    /**
     * A new violation has been found.
     *
     * @param ruleViolation
     *            the found violation.
     */
    void ruleViolationAdded(RuleViolation ruleViolation);

}
