/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.ast.ASTArguments;
import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTDotExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTLiteral;
import net.sourceforge.pmd.lang.vf.ast.ASTNegationExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.ast.AbstractVFNode;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;

/**
 * @author sergey.gorbaty February 2017
 *
 */
public class VfUnescapeElRule extends AbstractVfRule {
    private static final String A_CONST = "a";
    private static final String APEXIFRAME_CONST = "apex:iframe";
    private static final String IFRAME_CONST = "iframe";
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
        for (int i = 0; i < node.getNumChildren(); i++) {
            Node n = node.getChild(i);

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
        boolean jsonParse = false;

        if (prevText != null) {
            jsonParse = isJsonParse(prevText);
            if (isUnbalanced(prevText.getImage(), '\'') || isUnbalanced(prevText.getImage(), '\"')) {
                quoted = true;
            }
        }
        if (quoted) {
            // check escaping too
            if (!(jsonParse || startsWithSafeResource(elExpression) || containsSafeFields(elExpression))) {
                if (doesElContainAnyUnescapedIdentifiers(elExpression,
                        EnumSet.of(Escaping.JSENCODE, Escaping.JSINHTMLENCODE))) {
                    addViolation(data, elExpression);
                }
            }
        } else {
            if (!(startsWithSafeResource(elExpression) || containsSafeFields(elExpression))) {
                final boolean hasUnscaped = doesElContainAnyUnescapedIdentifiers(elExpression,
                        EnumSet.of(Escaping.JSENCODE, Escaping.JSINHTMLENCODE));
                if (!(jsonParse && !hasUnscaped)) {
                    addViolation(data, elExpression);
                }
            }
        }
    }

    private boolean isJsonParse(ASTText prevText) {
        final String text = prevText.getImage().endsWith("'")
                ? prevText.getImage().substring(0, prevText.getImage().length() - 1) : prevText.getImage();

        return text.endsWith("JSON.parse(") || text.endsWith("jQuery.parseJSON(") || text.endsWith("$.parseJSON(");
    }

    private boolean isUnbalanced(String image, char pattern) {
        char[] array = image.toCharArray();

        boolean foundPattern = false;

        for (int i = array.length - 1; i > 0; i--) {
            if (array[i] == pattern) {
                foundPattern = true;
            }

            if (array[i] == ';') {
                return foundPattern;
            }
        }

        return foundPattern;
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
        switch (node.getName().toLowerCase(Locale.ROOT)) {
        case IFRAME_CONST:
        case APEXIFRAME_CONST:
        case A_CONST:
            break;
        default:
            return;
        }

        final List<ASTAttribute> attributes = node.findChildrenOfType(ASTAttribute.class);
        boolean isEL = false;
        final Set<ASTElExpression> toReport = new HashSet<>();

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase(Locale.ROOT);
            // look for onevents

            if (HREF.equalsIgnoreCase(name) || SRC.equalsIgnoreCase(name)) {
                boolean startingWithSlashText = false;

                final ASTText attrText = attr.getFirstDescendantOfType(ASTText.class);
                if (attrText != null) {
                    if (0 == attrText.getIndexInParent()) {
                        String lowerCaseImage = attrText.getImage().toLowerCase(Locale.ROOT);
                        if (lowerCaseImage.startsWith("/") || lowerCaseImage.startsWith("http")
                                || lowerCaseImage.startsWith("mailto")) {
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
            String name = attr.getName().toLowerCase(Locale.ROOT);
            // look for onevents

            if (ON_EVENT.matcher(name).matches()) {
                final List<ASTElExpression> elsInVal = attr.findDescendantsOfType(ASTElExpression.class);
                for (ASTElExpression el : elsInVal) {
                    if (startsWithSafeResource(el)) {
                        continue;
                    }

                    if (doesElContainAnyUnescapedIdentifiers(el,
                            EnumSet.of(Escaping.ANY))) {
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
            final ASTNegationExpression negation = expression.getFirstChildOfType(ASTNegationExpression.class);
            if (negation != null) {
                return true;
            }

            final ASTIdentifier id = expression.getFirstChildOfType(ASTIdentifier.class);
            if (id != null) {
                String lowerCaseId = id.getImage().toLowerCase(Locale.ROOT);
                List<ASTArguments> args = expression.findChildrenOfType(ASTArguments.class);
                if (!args.isEmpty()) {
                    switch (lowerCaseId) {
                    case "urlfor":
                    case "casesafeid":
                    case "begins":
                    case "contains":
                    case "len":
                    case "getrecordids":
                    case "linkto":
                    case "sqrt":
                    case "round":
                    case "mod":
                    case "log":
                    case "ln":
                    case "exp":
                    case "abs":
                    case "floor":
                    case "ceiling":
                    case "nullvalue":
                    case "isnumber":
                    case "isnull":
                    case "isnew":
                    case "isblank":
                    case "isclone":
                    case "year":
                    case "month":
                    case "day":
                    case "datetimevalue":
                    case "datevalue":
                    case "date":
                    case "now":
                    case "today":
                        return true;

                    default:
                    }
                } else {
                    // has no arguments
                    switch (lowerCaseId) {
                    case "$action":
                    case "$page":
                    case "$site":
                    case "$resource":
                    case "$label":
                    case "$objecttype":
                    case "$component":
                    case "$remoteaction":
                        return true;

                    default:
                    }
                }
            }

        }

        return false;
    }

    private boolean startsWithSlashLiteral(final ASTElExpression elExpression) {
        final ASTExpression expression = elExpression.getFirstChildOfType(ASTExpression.class);
        if (expression != null) {
            final ASTLiteral literal = expression.getFirstChildOfType(ASTLiteral.class);
            if (literal != null && literal.getIndexInParent() == 0) {
                String lowerCaseLiteral = literal.getImage().toLowerCase(Locale.ROOT);
                if (lowerCaseLiteral.startsWith("'/") || lowerCaseLiteral.startsWith("\"/")
                        || lowerCaseLiteral.startsWith("'http")
                        || lowerCaseLiteral.startsWith("\"http")) {
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
            String name = attr.getName().toLowerCase(Locale.ROOT);
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

            if (innerContainsSafeFields(expr)) {
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
        final ASTExpression ex = expression.getFirstChildOfType(ASTExpression.class);

        return ex != null && innerContainsSafeFields(ex);

    }

    private boolean innerContainsSafeFields(final AbstractVFNode expression) {
        for (int i = 0; i < expression.getNumChildren(); i++) {
            Node child = expression.getChild(i);

            if (child instanceof ASTIdentifier) {
                switch (child.getImage().toLowerCase(Locale.ROOT)) {
                case "id":
                case "size":
                case "caseNumber":
                    return true;
                default:
                }
            }

            if (child instanceof ASTArguments) {
                if (containsSafeFields((ASTArguments) child)) {
                    return true;
                }
            }

            if (child instanceof ASTDotExpression) {
                if (innerContainsSafeFields((ASTDotExpression) child)) {
                    return true;
                }
            }

        }

        return false;
    }

    private boolean doesTagSupportEscaping(final ASTElement node) {
        if (node.getName() == null) {
            return false;
        }

        switch (node.getName().toLowerCase(Locale.ROOT)) { // vf is case insensitive
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
