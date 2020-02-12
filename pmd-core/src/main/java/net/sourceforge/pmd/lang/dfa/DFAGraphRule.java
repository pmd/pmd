/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa;

import java.util.List;

import net.sourceforge.pmd.Rule;

/**
 * @deprecated Only used by the deprecated designer
 */
@Deprecated
public interface DFAGraphRule extends Rule {
    List<DFAGraphMethod> getMethods();
}
