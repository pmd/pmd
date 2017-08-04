/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

/**
 * Statistics for an operation. Keeps a map of all memoized metrics results.
 *
 * @author Clément Fournier
 */
class OperationStats {

    private final String name;


    OperationStats(String name) {
        this.name = name;
    }


    String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OperationStats stats = (OperationStats) o;

        return name != null ? name.equals(stats.name) : stats.name == null;
    }


    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
