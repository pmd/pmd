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
    private static final String APEX_PREFIX = "apex";

    private final ElEscapeDetector escapeDetector = new ElEscapeDetector();

    public VfHtmlStyleTagXssRule() {
        addRuleChainVisit(ASTElExpression.class);
    }

    /**
     * We are looking for an ASTElExpression node that is
     * placed inside an ASTContent, which in turn is placed inside
     * an ASTElement, where the element is not an inbuilt vf tag.
     *
     * <ASTElement>
     *     <ASTContent>
     *         <ASTElExpression></ASTElExpression>
     *     </ASTContent>
     * </ASTElement>
     *
     */
    @Override
    public Object visit(ASTElExpression node, Object data) {
        final VfNode nodeParent = node.getParent();
        if (!(nodeParent instanceof ASTContent)) {
            // nothing to do here.
            // We care only if parent is available and is an ASTContent
            return data;
        }
        final ASTContent contentNode = (ASTContent) nodeParent;

        final VfNode nodeGrandParent = contentNode.getParent();
        if (!(nodeGrandParent instanceof ASTElement)) {
            // nothing to do here.
            // We care only if grandparent is available and is an ASTElement
            return data;
        }
        final ASTElement elementNode = (ASTElement) nodeGrandParent;

        // make sure elementNode does not have an "apex:" prefix
        if (isApexPrefixed(elementNode)) {
            // nothing to do here.
            // This rule does not deal with inbuilt-visualforce tags
            return data;
        }

        verifyEncoding(node, contentNode, elementNode, data);

        return data;
    }

    /**
     * Examining encoding of ElExpression - we apply different rules
     * for plain HTML tags and <style></style> content.
     */
    private void verifyEncoding(
            ASTElExpression node,
            ASTContent contentNode,
            ASTElement elementNode,
            Object data) {
        final String previousText = getPreviousText(contentNode, node);

        if (isStyleTag(elementNode)) {
            final boolean isWithinSafeResource = escapeDetector.startsWithSafeResource(node);
            // check if we are within a URL expression
            if (isWithinUrlMethod(previousText)) {
                verifyEncodingWithinUrl(node, isWithinSafeResource, data);
            } else {
                verifyEncodingWithoutUrl(node, isWithinSafeResource, data);
            }
        }
    }

    private boolean isStyleTag(ASTElement elementNode) {
        // are we dealing with HTML <style></style> tag?
        return STYLE_TAG.equalsIgnoreCase(elementNode.getLocalName());
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

    private boolean isApexPrefixed(ASTElement node) {
        return node.isHasNamespacePrefix()
                && APEX_PREFIX.equalsIgnoreCase(node.getNamespacePrefix());
    }

    /**
     * Get text content within style tag that leads upto the ElExpression.
     * For example, in this snippet:
     * <style>
     *  div {
     *   background: url('{!HTMLENCODE(XSSHere)}');
     * }
     * </style>
     *
     * getPreviousText(...) would return "\n div {\n background: url("
     *
     */
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
