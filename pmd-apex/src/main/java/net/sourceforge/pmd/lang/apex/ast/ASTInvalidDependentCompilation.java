/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.InvalidDependentCompilation;

public final class ASTInvalidDependentCompilation extends AbstractApexNode<InvalidDependentCompilation> {

    ASTInvalidDependentCompilation(InvalidDependentCompilation userClass) {
        super(userClass);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getImage() {
        String apexName = getDefiningType();
        return apexName.substring(apexName.lastIndexOf('.') + 1);
    }
}
