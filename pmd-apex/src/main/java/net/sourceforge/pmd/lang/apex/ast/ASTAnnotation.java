/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.Annotation;

public final class ASTAnnotation extends AbstractApexNode<Annotation> {

    ASTAnnotation(Annotation annotation) {
        super(annotation);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getType().getApexName();
    }

    public boolean isResolved() {
        return node.getType().isResolved();
    }
}
