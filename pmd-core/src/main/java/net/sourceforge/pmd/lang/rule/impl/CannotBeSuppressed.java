/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.impl;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.rule.Rule;

/**
 * Marker interface for rules that cannot be suppressed.
 *
 * @experimental For now this is only needed to implement {@link UnnecessaryPmdSuppressionRule}.
 */
@Experimental
public interface CannotBeSuppressed extends Rule {
}
