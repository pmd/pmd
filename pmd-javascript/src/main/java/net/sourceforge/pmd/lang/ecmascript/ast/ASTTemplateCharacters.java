/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.TemplateCharacters;

public final class ASTTemplateCharacters extends AbstractEcmascriptNode<TemplateCharacters> {

    ASTTemplateCharacters(TemplateCharacters templateCharacters) {
        super(templateCharacters);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getValue() {
        return node.getValue();
    }
}
