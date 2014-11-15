/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.List;

import net.sourceforge.pmd.Rule;

public interface DFAGraphRule extends Rule {
    List<DFAGraphMethod> getMethods();
}
