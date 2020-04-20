/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.PackageVersionExpression;

public class ASTPackageVersionExpression extends AbstractApexNode<PackageVersionExpression> {

    @Deprecated
    @InternalApi
    public ASTPackageVersionExpression(PackageVersionExpression packageVersionExpression) {
        super(packageVersionExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
