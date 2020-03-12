/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Language-independent framework to represent code metrics. If you want
 * to compute code metrics in your rules, then you should find the language-specific
 * enums containing {@link net.sourceforge.pmd.lang.metrics.MetricKey}s
 * in the relevant language modules.
 *
 * <p>Metrics are cached by default on the nodes they're computed on.
 * Many APIs here are deprecated, this is because metrics were previously
 * cached in big static maps, which is replaced by caching on nodes.
 *
 */
package net.sourceforge.pmd.lang.metrics;
