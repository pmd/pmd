/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.rule.security;

import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.visualforce.ast.ASTAttribute;
import net.sourceforge.pmd.lang.visualforce.ast.ASTElExpression;
import net.sourceforge.pmd.lang.visualforce.ast.ASTElement;
import net.sourceforge.pmd.lang.visualforce.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.visualforce.rule.AbstractVfRule;

/**
 * @author sergey.gorbaty
 *
 */
public class VfCsrfRule extends AbstractVfRule {

    private static final String APEX_PAGE = "apex:page";

    @Override
    public Object visit(ASTElement node, Object data) {
        if (APEX_PAGE.equalsIgnoreCase(node.getName())) {
            List<ASTAttribute> attribs = node.children(ASTAttribute.class).toList();
            boolean controller = false;
            boolean isEl = false;
            ASTElExpression valToReport = null;

            for (ASTAttribute attr : attribs) {
                switch (attr.getName().toLowerCase(Locale.ROOT)) {
                case "action":
                    ASTElExpression value = attr.descendants(ASTElExpression.class).first();
                    if (value != null) {
                        if (doesElContainIdentifiers(value)) {
                            isEl = true;
                            valToReport = value;
                        }
                    }
                    break;
                case "controller":
                    controller = true;
                    break;
                default:
                    break;

                }

            }

            if (controller && isEl && valToReport != null) {
                asCtx(data).addViolation(valToReport);
            }

        }
        return super.visit(node, data);
    }

    private boolean doesElContainIdentifiers(ASTElExpression value) {
        return value.descendants(ASTIdentifier.class).first() != null;
    }
}
