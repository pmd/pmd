/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ScriptNode;

public final class ASTScriptNode extends AbstractEcmascriptNode<ScriptNode> {

    ASTScriptNode(ScriptNode scriptNode) {
        super(scriptNode);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
