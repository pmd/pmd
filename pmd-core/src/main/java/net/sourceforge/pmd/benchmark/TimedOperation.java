/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

/**
 * Describes a timed operation. It's {@link AutoCloseable}, for ease of use.
 */
public interface TimedOperation extends AutoCloseable {

    /**
     * Stops tracking if not already stopped.
     */
    @Override
    void close();

    /**
     * Stops tracking if not already stopped.
     * @param extraDataCounter An optional additional data counter to track along the measurements.
     *                         Users are free to track any extra value they want (ie: number of analyzed nodes,
     *                         iterations in a loop, etc.)
     */
    void close(int extraDataCounter);
}
