/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * A {@link Node} which knows about the scope within it has been declared.
 */
public interface ScopedNode extends Node {

    Scope getScope();
}
