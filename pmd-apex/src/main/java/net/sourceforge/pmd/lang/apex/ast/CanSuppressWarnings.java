/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;

/**
 * @deprecated This interface will be removed, the AST should not know about rules.
 */
@Deprecated
public interface CanSuppressWarnings {
    @Deprecated
    boolean hasSuppressWarningsAnnotationFor(Rule rule);
}
