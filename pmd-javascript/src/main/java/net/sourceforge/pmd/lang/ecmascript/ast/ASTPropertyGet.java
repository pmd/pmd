/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.PropertyGet;

public final class ASTPropertyGet extends AbstractInfixEcmascriptNode<PropertyGet> {
    ASTPropertyGet(PropertyGet propertyGet) {
        super(propertyGet, false);
        super.setImage(".");
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
