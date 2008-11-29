/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface EcmascriptNode extends Node {

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(EcmascriptParserVisitor visitor, Object data);

    boolean hasSideEffects();
}
