/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Language-independent framework to represent code metrics. To find the build-in
 * metrics for a language, find the language-specific
 * utility class containing {@link net.sourceforge.pmd.lang.metrics.Metric}
 * constants, eg in java, {@code JavaMetrics}.
 *
 * <p>See {@link net.sourceforge.pmd.lang.metrics.Metric} and {@link net.sourceforge.pmd.lang.metrics.MetricsUtil}
 * for usage documentation. In some language modules, XPath rules may
 * use metrics through an XPath function, e.g. <a href="pmd_userdocs_extending_writing_xpath_rules.html#pmd-java-metric">pmd-java:metric</a>
 * function.
 */
package net.sourceforge.pmd.lang.metrics;
