/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.basic;

import java.util.List;

import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTUnparsedText;
import net.sourceforge.pmd.lang.vf.rule.AbstractVFRule;

/**
 * @author sergey.gorbaty
 *
 */
public class VfUnescapeElRule extends AbstractVFRule {

    @Override
    public Object visit(ASTElement node, Object data) {
        if (node.getName().equals("apex:outputText")) {
            final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
            boolean isUnescaped = false;
            boolean isEL = false;
            for (ASTAttribute attr : attributes) {
                if (attr != null) {
                    String name = attr.getName();
                    switch (name) {
                    case "escape":
                        final ASTUnparsedText text = attr.getFirstDescendantOfType(ASTUnparsedText.class);
                        if (text != null) {
                            if (text.getImage().equalsIgnoreCase("false")) {
                                isUnescaped = true;
                            }
                        }
                        break;
                    case "value":
                        final ASTElExpression elInVal = attr.getFirstDescendantOfType(ASTElExpression.class);
                        if (elInVal != null) {
                            isEL = true;
                        }
                        break;
                    default:
                        break;
                    }

                }
            }

            if (isEL && isUnescaped) {
                addViolation(data, node);
            }
        }

        return super.visit(node, data);
    }

}
