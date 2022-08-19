/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.initializer.ConstructorInitializer;

public class ASTNewMapInitExpression extends AbstractApexNode.Single<ConstructorInitializer> {

    @Deprecated
    @InternalApi
    public ASTNewMapInitExpression(ConstructorInitializer constructorInitializer) {
        super(constructorInitializer);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
