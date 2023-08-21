/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;

public final class ASTModifierOrAnnotation extends AbstractApexNode<ModifierOrAnnotation> {

    ASTModifierOrAnnotation(ModifierOrAnnotation modifierOrAnnotation) {
        super(modifierOrAnnotation);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
