/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

public interface InvariantConfigurationFactory {
    String getName();

    InvariantConfiguration createConfiguration();
}
