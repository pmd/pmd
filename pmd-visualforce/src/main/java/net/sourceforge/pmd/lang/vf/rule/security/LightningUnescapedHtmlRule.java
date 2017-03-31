/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.List;

import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;

public class LightningUnescapedHtmlRule extends AbstractVfRule {

    @Override
    public Object visit(ASTElement node, Object data) {
        if ("aura:unescapedHtml".equalsIgnoreCase(node.getName())) {
            List<ASTAttribute> attribs = node.findChildrenOfType(ASTAttribute.class);
            for (ASTAttribute attr : attribs) {
                if ("value".equalsIgnoreCase(attr.getName())) {
                    ASTElExpression value = attr.getFirstDescendantOfType(ASTElExpression.class);
                    if (value != null) {
                        addViolation(data, node);
                    }
                }

            }

        }

        return super.visit(node, data);
    }

}
