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
import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.vf.ast.ASTLiteral;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;
import net.sourceforge.pmd.lang.vf.rule.security.internal.ElEscapeDetector;


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
    private static final EnumSet<ElEscapeDetector.Escaping> JSENCODE_JSINHTMLENCODE = EnumSet.of(ElEscapeDetector.Escaping.JSENCODE, ElEscapeDetector.Escaping.JSINHTMLENCODE);
    private static final EnumSet<ElEscapeDetector.Escaping> ANY_ENCODE = EnumSet.of(ElEscapeDetector.Escaping.ANY);

    @Override
    public Object visit(ASTHtmlScript node, Object data) {
        checkIfCorrectlyEscaped(node, data);

        return super.visit(node, data);
    }

    private void checkIfCorrectlyEscaped(ASTHtmlScript node, Object data) {
        // churn thru every child just once instead of twice
        for (int i = 0; i < node.getNumChildren(); i++) {
            Node n = node.getChild(i);

            if (n instanceof ASTElExpression) {
                processElInScriptContext((ASTElExpression) n, data);
            }
        }
    }

    private void processElInScriptContext(ASTElExpression elExpression, Object data) {
        if (!properlyEscaped(elExpression)) {
            addViolation(data, elExpression);
        }
    }

    private boolean properlyEscaped(ASTElExpression el) {
        // Find the first Expression-type child of this top-level node.
        ASTExpression expression = el.getFirstChildOfType(ASTExpression.class);

        // If no such node was found, then there's nothing to escape, so we're fine.
        if (expression == null) {
            return true;
        }

        // Otherwise, we should pass the expression node into our recursive checker.
        return ElEscapeDetector.expressionRecursivelyValid(expression, JSENCODE_JSINHTMLENCODE);
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

                        if (ElEscapeDetector.startsWithSafeResource(el)) {
                            break;
                        }

                        if (ElEscapeDetector.doesElContainAnyUnescapedIdentifiers(el, ElEscapeDetector.Escaping.URLENCODE)) {
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
                    if (ElEscapeDetector.startsWithSafeResource(el)) {
                        continue;
                    }

                    if (ElEscapeDetector.doesElContainAnyUnescapedIdentifiers(el, ANY_ENCODE)) {
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
                    if (ElEscapeDetector.startsWithSafeResource(el)) {
                        continue;
                    }

                    if (ElEscapeDetector.doesElContainAnyUnescapedIdentifiers(el,
                            ElEscapeDetector.Escaping.HTMLENCODE)) {
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
                            if (ElEscapeDetector.startsWithSafeResource(el)) {
                                continue;
                            }

                            if (ElEscapeDetector.doesElContainAnyUnescapedIdentifiers(el,
                                    ElEscapeDetector.Escaping.HTMLENCODE)) {
                                toReturn.add(el);
                            }

                        }
                    }
                }
            }
        }

        return toReturn;
    }
}
