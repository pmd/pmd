/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTUnparsedText;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;

/**
 * @author sergey.gorbaty
 *
 */
public class VfUnescapeElRule extends AbstractVfRule {

    private static final String APEX_PARAM = "apex:param";
    private static final String VALUE = "value";
    private static final String ITEM_VALUE = "itemValue";
    private static final String ESCAPE = "escape";
    private static final String ITEM_ESCAPED = "itemEscaped";
    private static final String APEX_OUTPUT_TEXT = "apex:outputtext";
    private static final String APEX_PAGE_MESSAGE = "apex:pagemessage";
    private static final String APEX_PAGE_MESSAGES = "apex:pagemessages";
    private static final String APEX_SELECT_OPTION = "apex:selectoption ";
    private static final String FALSE = "false";

    @Override
    public Object visit(ASTElement node, Object data) {
        if (doesTagSupportEscaping(node)) {
            final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
            boolean isUnescaped = false;
            boolean isEL = false;
            boolean hasPlaceholders = false;

            for (ASTAttribute attr : attributes) {
                if (attr != null) {
                    String name = attr.getName();
                    switch (name) {
                    case ESCAPE:
                    case ITEM_ESCAPED:
                        final ASTUnparsedText text = attr.getFirstDescendantOfType(ASTUnparsedText.class);
                        if (text != null) {
                            if (text.getImage().equalsIgnoreCase(FALSE)) {
                                isUnescaped = true;
                            }
                        }
                        break;
                    case VALUE:
                    case ITEM_VALUE:
                        final ASTElExpression elInVal = attr.getFirstDescendantOfType(ASTElExpression.class);
                        if (elInVal != null) {
                            isEL = true;
                        }

                        final ASTUnparsedText textValue = attr.getFirstDescendantOfType(ASTUnparsedText.class);
                        if (textValue != null) {
                            if (Pattern.compile("\\{(\\w|,|\\.|'|:|\\s)*\\}").matcher(textValue.getImage()).matches()) {
                                hasPlaceholders = true;
                            }
                        }

                        break;
                    default:
                        break;
                    }

                }
            }

            if (hasPlaceholders && isUnescaped) {
                if (hasAnyEL(node)) {
                    addViolation(data, node);
                }
            }

            if (isEL && isUnescaped) {
                addViolation(data, node);
            }
        }

        return super.visit(node, data);
    }

    private boolean doesTagSupportEscaping(ASTElement node) {
        if (node.getName() == null) {
            return false;
        }

        switch (node.getName().toLowerCase()) { // vf is case insensitive?
        case APEX_OUTPUT_TEXT:
        case APEX_PAGE_MESSAGE:
        case APEX_PAGE_MESSAGES:
        case APEX_SELECT_OPTION:
            return true;
        default:
            return false;
        }

    }

    private boolean hasAnyEL(ASTElement node) {
        final List<ASTElement> innerElements = node.findChildrenOfType(ASTElement.class);
        for (ASTElement element : innerElements) {
            if (element.getName().equalsIgnoreCase(APEX_PARAM)) {
                final List<ASTAttribute> innerAttributes = element.findChildrenOfType(ASTAttribute.class);
                for (ASTAttribute attrib : innerAttributes) {
                    final ASTElExpression elInVal = attrib.getFirstDescendantOfType(ASTElExpression.class);
                    if (elInVal != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
