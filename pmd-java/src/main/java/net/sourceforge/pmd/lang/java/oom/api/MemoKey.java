/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Keys for the memoization maps. By default, MetricKeys are used, but if the version is not
 * {@link Metric.Version#STANDARD}, then the version is used instead. This is why versions cannot be shared across
 * metrics.
 *
 * @author Clément Fournier
 */
public interface MemoKey {
}
