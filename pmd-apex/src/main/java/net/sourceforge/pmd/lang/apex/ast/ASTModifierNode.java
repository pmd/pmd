/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.ModifierNode;

public class ASTModifierNode extends AbstractApexNode<ModifierNode> {

    public ASTModifierNode(ModifierNode modifierNode) {
        super(modifierNode);
    }
}
