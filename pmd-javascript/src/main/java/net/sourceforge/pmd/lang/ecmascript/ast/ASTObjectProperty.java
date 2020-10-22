/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectProperty;

public final class ASTObjectProperty extends AbstractInfixEcmascriptNode<ObjectProperty> {
    ASTObjectProperty(ObjectProperty objectProperty) {
        super(objectProperty);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean isGetter() {
        return node.isGetterMethod();
    }

    public boolean isSetter() {
        return node.isSetterMethod();
    }
}
