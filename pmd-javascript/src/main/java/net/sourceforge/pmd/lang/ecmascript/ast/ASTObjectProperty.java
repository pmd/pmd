/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectProperty;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTObjectProperty extends AbstractInfixEcmascriptNode<ObjectProperty> {
    @Deprecated
    @InternalApi
    public ASTObjectProperty(ObjectProperty objectProperty) {
        super(objectProperty);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isGetter() {
        return node.isGetterMethod();
    }

    public boolean isSetter() {
        return node.isSetterMethod();
    }
}
