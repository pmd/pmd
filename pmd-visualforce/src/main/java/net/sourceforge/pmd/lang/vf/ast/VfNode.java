/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface VfNode extends Node {
    /**
     * Accept the visitor. *
     */
    Object jjtAccept(VfParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    Object childrenAccept(VfParserVisitor visitor, Object data);
}
