/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics.cyclo;

/**
 * Simple accumulator to keep track of the number of decision points.
 */
public class Accumulator {
    public int val = 1;

    public void addDecisionPoint() {
        val++;
    }

    public void addDecisionPoints(int x) {
        val += x;
    }
}
