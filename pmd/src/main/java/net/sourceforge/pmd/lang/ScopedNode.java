/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * A {@link Node} which knows about the scope within it has been declared.
 */
public interface ScopedNode extends Node {

    Scope getScope();
}
