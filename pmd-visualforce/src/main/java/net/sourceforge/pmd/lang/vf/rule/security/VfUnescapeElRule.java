/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;

/**
 * @author sergey.gorbaty
 *
 */
public class VfUnescapeElRule extends AbstractVfRule {
    private static final String APEX_PARAM = "apex:param";
    private static final String VALUE = "value";
    private static final String ITEM_VALUE = "itemvalue";
    private static final String ESCAPE = "escape";
    private static final String ITEM_ESCAPED = "itemescaped";
    private static final String APEX_OUTPUT_TEXT = "apex:outputtext";
    private static final String APEX_PAGE_MESSAGE = "apex:pagemessage";
    private static final String APEX_PAGE_MESSAGES = "apex:pagemessages";
    private static final String APEX_SELECT_OPTION = "apex:selectoption ";
    private static final String FALSE = "false";

    @Override
    public Object visit(ASTElement node, Object data) {
        if (doesTagSupportEscaping(node)) {
            checkTagsThatSupportEscaping(node, data);
        } else {
            checkAllOtherTags(node, data);
        }

        return super.visit(node, data);
    }

    private void checkAllOtherTags(ASTElement node, Object data) {
        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        boolean isEL = false;

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase();
            // look for onevents
            if (Pattern.compile("^on(\\w)+$").matcher(name).matches()) {
                final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                for (ASTElExpression el : elsInVal) {
                    if (doesElContainAnyUnescapedIdentifiers(el,
                            Arrays.asList(ESCAPING.JSINHTMLENCODE, ESCAPING.JSENCODE))) {
                        isEL = true;
                    }
                }
            }

            if (name.equalsIgnoreCase("href") || name.equalsIgnoreCase("src")) {
                final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                for (ASTElExpression el : elsInVal) {
                    if (doesElContainAnyUnescapedIdentifiers(el, ESCAPING.URLENCODE)) {
                        isEL = true;
                    }
                }
            }
        }

        if (isEL) {
            addViolation(data, node);
        }

    }

    private void checkTagsThatSupportEscaping(ASTElement node, Object data) {
        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        boolean isUnescaped = false;
        boolean isEL = false;
        boolean hasPlaceholders = false;

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase();
            switch (name) {
            case ESCAPE:
            case ITEM_ESCAPED:
                final ASTText text = attr.getFirstDescendantOfType(ASTText.class);
                if (text != null) {
                    if (text.getImage().equalsIgnoreCase(FALSE)) {
                        isUnescaped = true;
                    }
                }
                break;
            case VALUE:
            case ITEM_VALUE:

                final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                for (ASTElExpression el : elsInVal) {
                    if (doesElContainAnyUnescapedIdentifiers(el, ESCAPING.HTMLENCODE)) {
                        isEL = true;
                    }
                }

                final ASTText textValue = attr.getFirstDescendantOfType(ASTText.class);
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

        if (hasPlaceholders && isUnescaped) {
            if (hasELInInnerElements(node)) {
                addViolation(data, node);
            }
        }

        if (isEL && isUnescaped) {
            addViolation(data, node);
        }
    }

    private boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression, ESCAPING escape) {
        return doesElContainAnyUnescapedIdentifiers(elExpression, Arrays.asList(escape));

    }

    private boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression, List<ESCAPING> escapes) {
        if (elExpression == null) {
            return false;
        }

        boolean isEscaped = false;
        List<ASTIdentifier> ids = elExpression.findDescendantsOfType(ASTIdentifier.class);
        boolean foundAny = !ids.isEmpty();

        for (final ASTIdentifier id : ids) {
            if (id.getImage() != null) {
                for (ESCAPING e : escapes) {
                    if (id.getImage().equalsIgnoreCase(e.toString())) {
                        isEscaped = true;
                        break;
                    }

                    if (e.equals(ESCAPING.ANY)) {
                        for (ESCAPING esc : ESCAPING.values()) {
                            if (id.getImage().equalsIgnoreCase(esc.toString())) {
                                isEscaped = true;
                                break;
                            }
                        }
                    }

                }

            }
        }

        return (foundAny && !isEscaped);
    }

    private boolean doesTagSupportEscaping(final ASTElement node) {
        if (node.getName() == null) {
            return false;
        }

        switch (node.getName().toLowerCase()) { // vf is case insensitive
        case APEX_OUTPUT_TEXT:
        case APEX_PAGE_MESSAGE:
        case APEX_PAGE_MESSAGES:
        case APEX_SELECT_OPTION:
            return true;
        default:
            return false;
        }

    }

    private boolean hasELInInnerElements(final ASTElement node) {
        final ASTContent content = node.getFirstChildOfType(ASTContent.class);
        if (content != null) {
            final List<ASTElement> innerElements = content.findChildrenOfType(ASTElement.class);
            for (final ASTElement element : innerElements) {
                if (element.getName().equalsIgnoreCase(APEX_PARAM)) {
                    final List<ASTAttribute> innerAttributes = element.findChildrenOfType(ASTAttribute.class);
                    for (ASTAttribute attrib : innerAttributes) {
                        final List<ASTElExpression> elsInVal = attrib.findDescendantsOfType(ASTElExpression.class);
                        for (final ASTElExpression el : elsInVal) {
                            if (doesElContainAnyUnescapedIdentifiers(el, ESCAPING.ANY)) {
                                return true;
                            }

                        }
                    }
                }
            }
        }

        return false;
    }

    enum ESCAPING {
        HTMLENCODE("HTMLENCODE"),
        URLENCODE("URLENCODE"),
        JSINHTMLENCODE("JSINHTMLENCODE"),
        JSENCODE("JSENCODE"),
        ANY("ANY");

        private final String text;

        private ESCAPING(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
