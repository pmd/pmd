/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.rule.security;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.visualforce.ast.ASTAttribute;
import net.sourceforge.pmd.lang.visualforce.ast.ASTContent;
import net.sourceforge.pmd.lang.visualforce.ast.ASTElExpression;
import net.sourceforge.pmd.lang.visualforce.ast.ASTElement;
import net.sourceforge.pmd.lang.visualforce.ast.ASTExpression;
import net.sourceforge.pmd.lang.visualforce.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.visualforce.ast.ASTLiteral;
import net.sourceforge.pmd.lang.visualforce.ast.ASTText;
import net.sourceforge.pmd.lang.visualforce.rule.AbstractVfRule;
import net.sourceforge.pmd.lang.visualforce.rule.security.internal.ElEscapeDetector;


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
    private static final Set<ElEscapeDetector.Escaping> JSENCODE_JSINHTMLENCODE = EnumSet.of(ElEscapeDetector.Escaping.JSENCODE, ElEscapeDetector.Escaping.JSINHTMLENCODE);
    private static final Set<ElEscapeDetector.Escaping> ANY_ENCODE = EnumSet.of(ElEscapeDetector.Escaping.ANY);

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
            asCtx(data).addViolation(elExpression);
        }
    }

    private boolean properlyEscaped(ASTElExpression el) {
        // Find the first Expression-type child of this top-level node.
        ASTExpression expression = el.firstChild(ASTExpression.class);

        // If no such node was found, then there's nothing to escape, so we're fine.
        return expression == null
                // Otherwise, we should pass the expression node into our recursive checker.
                || ElEscapeDetector.expressionRecursivelyValid(expression, JSENCODE_JSINHTMLENCODE);
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

        final List<ASTAttribute> attributes = node.children(ASTAttribute.class).toList();
        boolean isEL = false;
        final Set<ASTElExpression> toReport = new HashSet<>();

        for (ASTAttribute attr : attributes) {
            String name = attr.getName().toLowerCase(Locale.ROOT);
            // look for onevents

            if (HREF.equalsIgnoreCase(name) || SRC.equalsIgnoreCase(name)) {
                boolean startingWithSlashText = false;

                final ASTText attrText = attr.descendants(ASTText.class).first();
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
                    for (ASTElExpression el : attr.descendants(ASTElExpression.class)) {
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
                asCtx(data).addViolation(expr);
            }
        }

    }

    private void checkAllOnEventTags(ASTElement node, Object data) {
        boolean isEL = false;
        final Set<ASTElExpression> toReport = new HashSet<>();

        for (ASTAttribute attr : node.children(ASTAttribute.class)) {
            String name = attr.getName().toLowerCase(Locale.ROOT);
            // look for onevents

            if (ON_EVENT.matcher(name).matches()) {
                for (ASTElExpression el : attr.descendants(ASTElExpression.class)) {
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
                asCtx(data).addViolation(expr);
            }
        }

    }

    private boolean startsWithSlashLiteral(final ASTElExpression elExpression) {
        final ASTExpression expression = elExpression.firstChild(ASTExpression.class);
        if (expression != null) {
            final ASTLiteral literal = expression.firstChild(ASTLiteral.class);
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
        final Set<ASTElExpression> toReport = new HashSet<>();
        boolean isUnescaped = false;
        boolean isEL = false;
        boolean hasPlaceholders = false;

        for (ASTAttribute attr : node.children(ASTAttribute.class)) {
            String name = attr.getName().toLowerCase(Locale.ROOT);
            switch (name) {
            case ESCAPE:
            case ITEM_ESCAPED:
                final ASTText text = attr.descendants(ASTText.class).first();
                if (text != null) {
                    if (FALSE.equalsIgnoreCase(text.getImage())) {
                        isUnescaped = true;
                    }
                }
                break;
            case VALUE:
            case ITEM_VALUE:

                for (ASTElExpression el : attr.descendants(ASTElExpression.class)) {
                    if (ElEscapeDetector.startsWithSafeResource(el)) {
                        continue;
                    }

                    if (ElEscapeDetector.doesElContainAnyUnescapedIdentifiers(el,
                            ElEscapeDetector.Escaping.HTMLENCODE)) {
                        isEL = true;
                        toReport.add(el);
                    }
                }

                final ASTText textValue = attr.descendants(ASTText.class).first();
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
                asCtx(data).addViolation(expr);
            }
        }

        if (isEL && isUnescaped) {
            for (ASTElExpression expr : toReport) {
                asCtx(data).addViolation(expr);
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
        final ASTContent content = node.firstChild(ASTContent.class);
        if (content != null) {
            for (final ASTElement element : content.children(ASTElement.class)) {
                if (APEX_PARAM.equalsIgnoreCase(element.getName())) {
                    for (ASTAttribute attrib : element.children(ASTAttribute.class)) {
                        for (final ASTElExpression el : attrib.descendants(ASTElExpression.class)) {
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
