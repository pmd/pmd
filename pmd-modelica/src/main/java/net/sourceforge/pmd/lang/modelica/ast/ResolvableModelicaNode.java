/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;

/**
 * A public API for resolving lexical references to class or components.
 */
public interface ResolvableModelicaNode extends ModelicaNode {
    /**
     * Tries to resolve the <b>declaration</b> of the referenced component.
     */
    ResolutionResult getResolutionCandidates();
}
