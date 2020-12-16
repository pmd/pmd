/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import java.util.EnumSet;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.ast.VfNode;
import net.sourceforge.pmd.lang.vf.rule.AbstractVfRule;
import net.sourceforge.pmd.lang.vf.rule.security.lib.ElEscapeDetector;


public class VfHtmlStyleTagXssRule extends AbstractVfRule {
    private static final String STYLE_TAG = "style";
    private static final String APEX_PREFIX = "apex:";

    private final ElEscapeDetector escapeDetector = new ElEscapeDetector();

    @Override
    public Object visit(ASTElExpression node, Object data) {
        final VfNode nodeParent = node.getParent();
        if (!(nodeParent instanceof ASTContent)) {
            // nothing to do here.
            // We care only if parent is available and is an ASTContent
            return super.visit(node, data);
        }
        final ASTContent contentNode = (ASTContent) nodeParent;

        final VfNode nodeGrandParent = contentNode.getParent();
        if (!(nodeGrandParent instanceof ASTElement)) {
            // nothing to do here.
            // We care only if grandparent is available and is an ASTElement
            return super.visit(node, data);
        }
        final ASTElement elementNode = (ASTElement) nodeGrandParent;

        // make sure elementNode does not have an "apex:" prefix
        if (isApexPrefixed(elementNode)) {
            // nothing to do here.
            // This rule does not deal with inbuilt-visualforce tags
            return super.visit(node, data);
        }

        verifyEncoding(node, contentNode, elementNode, data);

        return super.visit(node, data);
    }

    private void verifyEncoding(
            ASTElExpression node,
            ASTContent contentNode,
            ASTElement elementNode,
            Object data) {
        final String previousText = getPreviousText(contentNode, node);

        if (isStyleTag(elementNode)) {
            verifyStyleEncoding(node, previousText, data);
        } else {
            // El expression in any other plain HTML tag should be encoded
            verifyGeneralHtmlEncoding(node, data);
        }
    }

    private boolean isStyleTag(ASTElement elementNode) {
        // are we dealing with HTML <style></style> tag?
        return STYLE_TAG.equalsIgnoreCase(elementNode.getLocalName());
    }

    private void verifyStyleEncoding(ASTElExpression elExpressionNode, String previousText, Object data) {
        final boolean isWithinSafeResource = escapeDetector.startsWithSafeResource(elExpressionNode);
        // check if we are within a URL expression
        if (isWithinUrlMethod(previousText)) {
            verifyEncodingWithinUrl(elExpressionNode, isWithinSafeResource, data);
        } else {
            verifyEncodingWithoutUrl(elExpressionNode, isWithinSafeResource, data);
        }
    }

    private void verifyEncodingWithinUrl(ASTElExpression elExpressionNode, boolean isWithinSafeResource, Object data) {

        // only allow URLENCODING or JSINHTMLENCODING
        if (escapeDetector.doesElContainAnyUnescapedIdentifiers(
                elExpressionNode,
                EnumSet.of(ElEscapeDetector.Escaping.URLENCODE, ElEscapeDetector.Escaping.JSINHTMLENCODE))
                && !isWithinSafeResource) {
            addViolationWithMessage(
                    data,
                    elExpressionNode,
                    "Dynamic EL content within URL in style tag should be URLENCODED or JSINHTMLENCODED as appropriate");
        }

    }

    private void verifyEncodingWithoutUrl(ASTElExpression elExpressionNode, boolean isWithinSafeResource, Object data) {
        if (escapeDetector.doesElContainAnyUnescapedIdentifiers(
                elExpressionNode,
                EnumSet.of(ElEscapeDetector.Escaping.ANY))
                && !isWithinSafeResource) {
            addViolationWithMessage(
                    data,
                    elExpressionNode,
                    "Dynamic EL content in style tag should be appropriately encoded");
        }
    }

    private void verifyGeneralHtmlEncoding(ASTElExpression elExpressionNode, Object data) {
        if (escapeDetector.doesElContainAnyUnescapedIdentifiers(
                elExpressionNode,
                ElEscapeDetector.Escaping.HTMLENCODE)
                && !escapeDetector.startsWithSafeResource(elExpressionNode)) {
            addViolationWithMessage(
                    data,
                    elExpressionNode,
                    "Dynamic content in plain HTML tags should be HTMLENCODED");
        }
    }

    private boolean isApexPrefixed(ASTElement node) {
        return node.isHasNamespacePrefix()
                && APEX_PREFIX.equalsIgnoreCase(node.getNamespacePrefix());
    }

    private String getPreviousText(ASTContent content, ASTElExpression elExpressionNode) {
        final int indexInParent = elExpressionNode.getIndexInParent();
        final VfNode previous = indexInParent > 0 ? content.getChild(indexInParent - 1) : null;
        return previous instanceof ASTText ? previous.getImage() : "";
    }

    // visible for unit testing
    boolean isWithinUrlMethod(String previousText) {
        // match for a pattern that
        // 1. contains "url" (case insensitive),
        // 2. followed by any number of whitespaces,
        // 3. a starting bracket "("
        // 4. and anything else but an ending bracket ")"
        // For example:
        // Matches: "div { background: url('", "div { background: Url  ( blah"
        // Does not match: "div { background: url('myUrl')", "div { background: myStyle('"

        final String urlMethodPattern = "url\\s*\\([^)]*$";
        return Pattern.compile(urlMethodPattern, Pattern.CASE_INSENSITIVE)
                .matcher(previousText)
                .find();
    }

}
