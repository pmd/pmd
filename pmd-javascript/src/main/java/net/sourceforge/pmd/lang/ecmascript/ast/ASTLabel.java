/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Label;

public class ASTLabel extends AbstractEcmascriptNode<Label> {
    ASTLabel(Label label) {
        super(label);
        super.setImage(label.getName());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
