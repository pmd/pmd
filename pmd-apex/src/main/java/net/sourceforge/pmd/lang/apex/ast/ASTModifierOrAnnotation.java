/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;

public class ASTModifierOrAnnotation extends AbstractApexNode<ModifierOrAnnotation> {

    @Deprecated
    @InternalApi
    public ASTModifierOrAnnotation(ModifierOrAnnotation modifierOrAnnotation) {
        super(modifierOrAnnotation);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
