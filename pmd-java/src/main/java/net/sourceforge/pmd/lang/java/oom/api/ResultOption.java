/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Option for class metrics determining what they return.
 *
 * @author Clément Fournier
 */
public enum ResultOption implements MetricOption {
    DEFAULT, SUM, AVERAGE, HIGHEST
}
