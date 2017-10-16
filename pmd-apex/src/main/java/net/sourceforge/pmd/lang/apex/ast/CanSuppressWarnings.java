/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;

public interface CanSuppressWarnings {
    boolean hasSuppressWarningsAnnotationFor(Rule rule);
}
