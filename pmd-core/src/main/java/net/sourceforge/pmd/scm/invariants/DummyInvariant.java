/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

/**
 * Dummy invariant that is always satisfied.
 */
public class DummyInvariant implements Invariant {
    public static final class Configuration implements InvariantConfiguration {
        // no configuration options, even command line

        @Override
        public Invariant createChecker() {
            return new DummyInvariant();
        }
    }

    public static final InvariantConfigurationFactory FACTORY = new AbstractInvariant.AbstractFactory("dummy") {
        @Override
        public InvariantConfiguration createConfiguration() {
            return new Configuration();
        }
    };

    @Override
    public boolean checkIsSatisfied() throws Exception {
        return true;
    }

    @Override
    public String toString() {
        return "Dummy invariant (always satisfied)";
    }
}
