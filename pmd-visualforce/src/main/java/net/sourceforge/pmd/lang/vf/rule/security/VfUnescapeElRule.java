/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTDotExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTLiteral;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.ast.AbstractVFNode;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;

/**
 * @author sergey.gorbaty February 2017
 *
 */
public class VfUnescapeElRule extends AbstractVfRule {
    private static final String HREF = "href";
    private static final String SRC = "src";
    private static final String APEX_PARAM = "apex:param";
    private static final String VALUE = "value";
    private static final String ITEM_VALUE = "itemvalue";
    private static final String ESCAPE = "escape";
    private static final String ITEM_ESCAPED = "itemescaped";
    private static final String APEX_OUTPUT_TEXT = "apex:outputtext";
    private static final String APEX_PAGE_MESSAGE = "apex:pagemessage";
    private static final String APEX_PAGE_MESSAGES = "apex:pagemessages";
    private static final String APEX_SELECT_OPTION = "apex:selectoption";
    private static final String FALSE = "false";
    private static final Pattern ON_EVENT = Pattern.compile("^on(\\w)+$");
    private static final Pattern PLACEHOLDERS = Pattern.compile("\\{(\\w|,|\\.|'|:|\\s)*\\}");

    @Override
    public Object visit(ASTHtmlScript node, Object data) {
        checkIfCorrectlyEscaped(node, data);

        return super.visit(node, data);
    }

    private void checkIfCorrectlyEscaped(ASTHtmlScript node, Object data) {
        ASTText prevText = null;

        // churn thru every child just once instead of twice
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node n = node.jjtGetChild(i);

            if (n instanceof ASTText) {
                prevText = (ASTText) n;
                continue;
            }

            if (n instanceof ASTElExpression) {
                processElInScriptContext((ASTElExpression) n, prevText, data);
            }
        }
    }

    private void processElInScriptContext(ASTElExpression elExpression, ASTText prevText, Object data) {
        boolean quoted = false;

        if (prevText != null) {
            if (isUnbalanced(prevText.getImage(), "'") || isUnbalanced(prevText.getImage(), "\"")) {
                quoted = true;
            }
        }
        if (quoted) {
            // check escaping too
            if (!(startsWithSafeResource(elExpression) || containsSafeFields(elExpression))) {
                if (doesElContainAnyUnescapedIdentifiers(elExpression, Escaping.JSENCODE)) {
                    addViolation(data, elExpression);
                }
            }
        } else {
            if (!(startsWithSafeResource(elExpression) || containsSafeFields(elExpression))) {
                addViolation(data, elExpression);
            }
        }
    }

    private boolean isUnbalanced(String image, String pattern) {
        int occurance = 0;
        int index = image.indexOf("=");
        if (index < 0) {
            index = image.indexOf(":");
        }
        
        index = image.indexOf(pattern, index + 1);
        while (index >= 0) {
            occurance++;
            index = image.indexOf(pattern, index + 1);
        }

        if ((occurance % 2) != 0) {
            return true;
        }

        return false;
    }

    @Override
    public Object visit(ASTElement node, Object data) {
        if (doesTagSupportEscaping(node)) {
            checkApexTagsThatSupportEscaping(node, data);
        } else {
            checkLimitedFlags(node, data);
            checkAllOnEventTags(node, data);
        }

        return super.visit(node, data);
    }

    private void checkLimitedFlags(ASTElement node, Object data) {
        switch (node.getName().toLowerCase()) {
        case "iframe":
        case "a":
            break;
        default:
            return;
        }

        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        boolean isEL = false;
        final Set<ASTElExpression> toReport = new HashSet<>();

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase();
            // look for onevents

            if (HREF.equalsIgnoreCase(name) || SRC.equalsIgnoreCase(name)) {
                boolean startingWithSlashText = false;

                final ASTText attrText = attr.getFirstDescendantOfType(ASTText.class);
                if (attrText != null) {
                    if (0 == attrText.jjtGetChildIndex()) {
                        if (attrText.getImage().startsWith("/")
                                || attrText.getImage().toLowerCase().startsWith("http") || attrText.getImage().toLowerCase().startsWith("mailto")) {
                            startingWithSlashText = true;
                        }
                    }
                }

                if (!startingWithSlashText) {
                    final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                    for (ASTElExpression el : elsInVal) {
                        if (startsWithSlashLiteral(el)) {
                            break;
                        }

                        if (startsWithSafeResource(el)) {
                            break;
                        }

                        if (doesElContainAnyUnescapedIdentifiers(el, Escaping.URLENCODE)) {
                            isEL = true;
                            toReport.add(el);
                        }
                    }
                }

            }

        }

        if (isEL) {
            for (ASTElExpression expr : toReport) {
                addViolation(data, expr);
            }
        }

    }

    private void checkAllOnEventTags(ASTElement node, Object data) {
        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        boolean isEL = false;
        final Set<ASTElExpression> toReport = new HashSet<>();

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase();
            // look for onevents

            if (ON_EVENT.matcher(name).matches()) {
                final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                for (ASTElExpression el : elsInVal) {
                    if (startsWithSafeResource(el)) {
                        continue;
                    }

                    if (doesElContainAnyUnescapedIdentifiers(el,
                            EnumSet.of(Escaping.JSINHTMLENCODE, Escaping.JSENCODE))) {
                        isEL = true;
                        toReport.add(el);
                    }
                }
            }

        }

        if (isEL) {
            for (ASTElExpression expr : toReport) {
                addViolation(data, expr);
            }
        }

    }

    private boolean startsWithSafeResource(final ASTElExpression el) {
        final ASTExpression expression = el.getFirstChildOfType(ASTExpression.class);
        if (expression != null) {
            final ASTIdentifier id = expression.getFirstChildOfType(ASTIdentifier.class);
            if (id != null) {
                switch (id.getImage().toLowerCase()) {
                case "$component":
                case "$objecttype":
                case "$label":
                case "$resource":
                case "urlfor":
                case "$site":
                case "$page":
                case "$action":
                case "casesafeid":
                case "$remoteaction":
                    return true;

                default:
                }
            }

        }

        return false;
    }

    private boolean startsWithSlashLiteral(final ASTElExpression elExpression) {
        final ASTExpression expression = elExpression.getFirstChildOfType(ASTExpression.class);
        if (expression != null) {
            final ASTLiteral literal = expression.getFirstChildOfType(ASTLiteral.class);
            if (literal != null && literal.jjtGetChildIndex() == 0) {
                if (literal.getImage().startsWith("'/") || literal.getImage().startsWith("\"/")
                        || literal.getImage().toLowerCase().startsWith("'http")
                        || literal.getImage().toLowerCase().startsWith("\"http")) {
                    return true;
                }
            }

        }

        return false;
    }

    private void checkApexTagsThatSupportEscaping(ASTElement node, Object data) {
        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        final Set<ASTElExpression> toReport = new HashSet<>();
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
                    if (startsWithSafeResource(el)) {
                        continue;
                    }

                    if (doesElContainAnyUnescapedIdentifiers(el, Escaping.HTMLENCODE)) {
                        isEL = true;
                        toReport.add(el);
                    }
                }

                final ASTText textValue = attr.getFirstDescendantOfType(ASTText.class);
                if (textValue != null) {

                    if (PLACEHOLDERS.matcher(textValue.getImage()).matches()) {
                        hasPlaceholders = true;
                    }
                }

                break;
            default:
                break;
            }

        }

        if (hasPlaceholders && isUnescaped) {
            for (ASTElExpression expr : hasELInInnerElements(node)) {
                addViolation(data, expr);
            }
        }

        if (isEL && isUnescaped) {
            for (ASTElExpression expr : toReport) {
                addViolation(data, expr);
            }
        }
    }

    private boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression, Escaping escape) {
        return doesElContainAnyUnescapedIdentifiers(elExpression, EnumSet.of(escape));

    }

    private boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression,
            EnumSet<Escaping> escapes) {
        if (elExpression == null) {
            return false;
        }

        final Set<ASTIdentifier> nonEscapedIds = new HashSet<>();

        final List<ASTExpression> exprs = elExpression.findChildrenOfType(ASTExpression.class);
        for (final ASTExpression expr : exprs) {

            if (containsSafeFields(expr)) {
                continue;
            }

            final List<ASTIdentifier> ids = expr.findChildrenOfType(ASTIdentifier.class);

            for (final ASTIdentifier id : ids) {
                boolean isEscaped = false;

                for (Escaping e : escapes) {

                    if (id.getImage().equalsIgnoreCase(e.toString())) {
                        isEscaped = true;
                        break;
                    }

                    if (e.equals(Escaping.ANY)) {
                        for (Escaping esc : Escaping.values()) {
                            if (id.getImage().equalsIgnoreCase(esc.toString())) {
                                isEscaped = true;
                                break;
                            }
                        }
                    }

                }

                if (!isEscaped) {
                    nonEscapedIds.add(id);
                }
            }

        }

        return !nonEscapedIds.isEmpty();
    }

    private boolean containsSafeFields(final AbstractVFNode expression) {

        for (int i = 0; i < expression.jjtGetNumChildren(); i++) {
            Node child = expression.jjtGetChild(i);

            if (child instanceof ASTIdentifier) {
                switch (child.getImage().toLowerCase()) {
                case "id":
                case "size":
                case "caseNumber":
                    return true;                
                default:
                }
            }

            if (child instanceof ASTDotExpression) {
                return containsSafeFields((ASTDotExpression) child);
            }

        }

        return false;
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

    private Set<ASTElExpression> hasELInInnerElements(final ASTElement node) {
        final Set<ASTElExpression> toReturn = new HashSet<>();
        final ASTContent content = node.getFirstChildOfType(ASTContent.class);
        if (content != null) {
            final List<ASTElement> innerElements = content.findChildrenOfType(ASTElement.class);
            for (final ASTElement element : innerElements) {
                if (element.getName().equalsIgnoreCase(APEX_PARAM)) {
                    final List<ASTAttribute> innerAttributes = element.findChildrenOfType(ASTAttribute.class);
                    for (ASTAttribute attrib : innerAttributes) {
                        final List<ASTElExpression> elsInVal = attrib.findDescendantsOfType(ASTElExpression.class);
                        for (final ASTElExpression el : elsInVal) {
                            if (startsWithSafeResource(el)) {
                                continue;
                            }

                            if (doesElContainAnyUnescapedIdentifiers(el, Escaping.HTMLENCODE)) {
                                toReturn.add(el);
                            }

                        }
                    }
                }
            }
        }

        return toReturn;
    }

    enum Escaping {
        HTMLENCODE("HTMLENCODE"),
        URLENCODE("URLENCODE"),
        JSINHTMLENCODE("JSINHTMLENCODE"),
        JSENCODE("JSENCODE"),
        ANY("ANY");

        private final String text;

        Escaping(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
