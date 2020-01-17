/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * Test parsing of a VF in document style, by checking the generated AST.
 * Original @author pieter_van_raemdonck - Application Engineers NV/SA -
 * www.ae.be
 *
 * @author sergey.gorbaty - VF adaptation
 *
 */
public class VfDocStyleTest extends AbstractVfNodesTest {

    /**
     * Smoke test for VF parser.
     */
    @Test
    public void testSimplestVf() {
        List<ASTElement> nodes = vf.getNodes(ASTElement.class, TEST_SIMPLEST_HTML);
        assertEquals("Exactly " + 1 + " element(s) expected", 1, nodes.size());
    }

    /**
     * Test the information on a Element and Attribute.
     */
    @Test
    public void testElementAttributeAndNamespace() {
        ASTCompilationUnit root = vf.parse(TEST_ELEMENT_AND_NAMESPACE);


        List<ASTElement> elementNodes = root.findDescendantsOfType(ASTElement.class);
        assertEquals("One element node expected!", 1, elementNodes.size());
        ASTElement element = elementNodes.iterator().next();
        assertEquals("Correct name expected!", "h:html", element.getName());
        assertTrue("Has namespace prefix!", element.isHasNamespacePrefix());
        assertTrue("Element is empty!", element.isEmpty());
        assertEquals("Correct namespace prefix of element expected!", "h", element.getNamespacePrefix());
        assertEquals("Correct local name of element expected!", "html", element.getLocalName());

        List<ASTAttribute> attributeNodes = root.findDescendantsOfType(ASTAttribute.class);
        assertEquals("One attribute node expected!", 1, attributeNodes.size());
        ASTAttribute attribute = attributeNodes.iterator().next();
        assertEquals("Correct name expected!", "MyNsPrefix:MyAttr", attribute.getName());
        assertTrue("Has namespace prefix!", attribute.isHasNamespacePrefix());
        assertEquals("Correct namespace prefix of element expected!", "MyNsPrefix", attribute.getNamespacePrefix());
        assertEquals("Correct local name of element expected!", "MyAttr", attribute.getLocalName());

    }

    /**
     * Test exposing a bug of parsing error when having a hash as last character
     * in an attribute value.
     *
     */
    @Test
    public void testAttributeValueContainingHash() {

        List<ASTAttribute> attributes = vf.getNodes(ASTAttribute.class, TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);
        assertEquals("Three attributes expected!", 3, attributes.size());

        ASTAttribute attr = attributes.get(0);
        assertEquals("Correct attribute name expected!", "something", attr.getName());
        assertEquals("Correct attribute value expected!", "#yes#",
                     attr.getFirstDescendantOfType(ASTText.class).getImage());

        attr = attributes.get(1);
        assertEquals("Correct attribute name expected!", "foo", attr.getName());
        assertEquals("Correct attribute value expected!", "CREATE",
                attr.getFirstDescendantOfType(ASTText.class).getImage());

        attr = attributes.get(2);
        assertEquals("Correct attribute name expected!", "href", attr.getName());
        assertEquals("Correct attribute value expected!", "#", attr.getFirstDescendantOfType(ASTText.class).getImage());

    }

    /**
     * Test correct parsing of CDATA.
     */
    @Test
    public void testCData() {
        List<ASTCData> cdataNodes = vf.getNodes(ASTCData.class, TEST_CDATA);

        assertEquals("One CDATA node expected!", 1, cdataNodes.size());
        ASTCData cdata = cdataNodes.iterator().next();
        assertEquals("Content incorrectly parsed!", " some <cdata> ]] ]> ", cdata.getImage());
    }

    /**
     * Test parsing of Doctype declaration.
     */
    @Test
    public void testDoctype() {
        ASTCompilationUnit root = vf.parse(TEST_DOCTYPE);

        List<ASTDoctypeDeclaration> docTypeDeclarations = root.findDescendantsOfType(ASTDoctypeDeclaration.class);
        assertEquals("One doctype declaration expected!", 1, docTypeDeclarations.size());
        ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations.iterator().next();
        assertEquals("Correct doctype-name expected!", "html", docTypeDecl.getName());

        List<ASTDoctypeExternalId> externalIds = root.findDescendantsOfType(ASTDoctypeExternalId.class);
        assertEquals("One doctype external id expected!", 1, externalIds.size());
        ASTDoctypeExternalId externalId = externalIds.iterator().next();
        assertEquals("Correct external public id expected!", "-//W3C//DTD XHTML 1.1//EN", externalId.getPublicId());
        assertEquals("Correct external uri expected!", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd",
                externalId.getUri());

    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void testHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Correct script content expected!", "Script!", text.getImage());
    }

    /**
     * Test parsing of EL in attribute of an element.
     */
    @Test
    public void testELInTagValue() {
        List<ASTElement> elememts = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE);
        assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTAttributeValue attribute = element.getFirstDescendantOfType(ASTAttributeValue.class);
        ASTIdentifier id = attribute.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct identifier expected", "foo", id.getImage());

    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    public void testELInTagValueWithCommentDQ() {
        List<ASTElement> elememts = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT);
        assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct identifier expected", "init", id.getImage());
    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    public void testELInTagValueWithCommentSQ() {
        List<ASTElement> elememts = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT_SQ);
        assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct identifier expected", "init", id.getImage());

    }

    /**
     * Test parsing of EL in HTML &lt;script&gt; element.
     */
    @Test
    public void testELInHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_EL_IN_HTML_SCRIPT);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Correct script content expected!", "vartext=", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of inline comment in EL.
     */
    @Test
    public void testInlineCommentInEL() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_EL_IN_HTML_SCRIPT_WITH_COMMENT);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Correct script content expected!", "vartext=", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of quoted EL in HTML &lt;script&gt; element.
     */
    @Test
    public void testQuotedELInHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_QUOTED_EL_IN_HTML_SCRIPT);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Correct script content expected!", "vartext='textHere", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of HTML &lt;script src="x"/&gt; element. It might not be
     * valid html but it is likely to appear in .page files.
     */
    @Test
    public void testImportHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_IMPORT_JAVASCRIPT);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        List<ASTAttribute> attr = script.findDescendantsOfType(ASTAttribute.class);
        assertEquals("One script expected!", 1, attr.size());
        ASTAttribute att = attr.iterator().next();
        ASTAttributeValue val = att.getFirstChildOfType(ASTAttributeValue.class);
        ASTText text = val.getFirstChildOfType(ASTText.class);
        assertEquals("filename.js", text.getImage());
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void testHtmlScriptWithAttribute() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT_WITH_ATTRIBUTE);
        assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Correct script content expected!", "Script!", text.getImage());
        List<ASTText> attrs = script.findDescendantsOfType(ASTText.class);
        assertEquals("text/javascript", attrs.get(0).getImage());
    }

    /**
     * A complex script containing HTML comments, escapes, quotes, etc.
     */
    @Test
    public void testComplexHtmlScript() {
        List<ASTHtmlScript> script = vf.getNodes(ASTHtmlScript.class, TEST_COMPLEX_SCRIPT);
        assertEquals("One script expected!", 1, script.size());
        ASTHtmlScript next = script.iterator().next();
        ASTText text = next.getFirstChildOfType(ASTText.class);
        assertTrue(text.getImage().contains("<!--"));

    }

    /**
     * Test parsing of HTML &lt;style&gt; element.
     */
    @Test
    public void testInlineCss() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_INLINE_STYLE);
        assertEquals("Two elements expected!", 3, elements.size());
    }

    /**
     * Test parsing of HTML text within element.
     */
    @Test
    public void testTextInTag() {
        List<ASTText> scripts = vf.getNodes(ASTText.class, TEST_TEXT_IN_TAG);
        assertEquals("One text chunk expected!", 1, scripts.size());
        ASTText script = scripts.iterator().next();
        assertEquals("Correct content expected!", " some text ", script.getImage());
    }

    /**
     * Test parsing of HTML with no spaces between tags. Parser is likely in
     * this scenario.
     */
    @Test
    public void noSpacesBetweenTags() {
        List<ASTElement> scripts = vf.getNodes(ASTElement.class, TEST_TAGS_NO_SPACE);
        assertEquals("Two tags expected!", 2, scripts.size());
        Iterator<ASTElement> iterator = scripts.iterator();
        ASTElement script = iterator.next();
        assertEquals("Correct content expected!", "a", script.getName());
        script = iterator.next();
        assertEquals("Correct content expected!", "b", script.getName());
    }

    /**
     * the $ sign might trick the parser into thinking an EL is next. He should
     * be able to treat it as plain text
     */
    @Test
    public void unclosedTagsWithDollar() {
        List<ASTText> scripts = vf.getNodes(ASTText.class, TEST_TAGS_WITH_DOLLAR);
        assertEquals("Two text chunks expected!", 2, scripts.size());
        ASTText script = scripts.iterator().next();
        assertEquals("Correct content expected!", " $ ", script.getImage());
    }

    /**
     * Make sure EL expressions aren't treated as plain text when they are
     * around unclosed tags.
     */
    @Test
    public void unclosedTagsWithELWithin() {
        List<ASTElement> element = vf.getNodes(ASTElement.class, TEST_TAGS_WITH_EL_WITHIN);
        assertEquals("One element expected!", 1, element.size());

        for (ASTElement elem : element) {
            ASTContent content = elem.getFirstChildOfType(ASTContent.class);
            List<ASTElExpression> els = content.findChildrenOfType(ASTElExpression.class);
            assertEquals("Two EL expressions expected!", 2, els.size());

            ASTElExpression node = (ASTElExpression) content.getChild(0);
            ASTIdentifier id = node.getFirstDescendantOfType(ASTIdentifier.class);
            assertEquals("Correct content expected!", "expr1", id.getImage());
            node = (ASTElExpression) content.getChild(1);
            id = node.getFirstDescendantOfType(ASTIdentifier.class);
            assertEquals("Correct content expected!", "expr2", id.getImage());
        }

    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void textAfterOpenAndClosedTag() {
        List<ASTElement> nodes = vf.getNodes(ASTElement.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals("Two elements expected!", 2, nodes.size());
        assertEquals("First element should be a", "a", nodes.get(0).getName());
        assertFalse("first element should be closed", nodes.get(0).isUnclosed());
        assertEquals("Second element should be b", "b", nodes.get(1).getName());
        assertTrue("Second element should not be closed", nodes.get(1).isUnclosed());

        List<ASTText> text = vf.getNodes(ASTText.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals("Two text chunks expected!", 2, text.size());
    }

    @Test
    public void quoteEL() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_QUOTE_EL);
        assertEquals("One attribute expected!", 1, attributes.size());
        ASTAttributeValue attr = attributes.iterator().next();
        List<ASTElExpression> els = attr.findChildrenOfType(ASTElExpression.class);
        assertEquals("Must be 1!", 1, els.size());
        ASTExpression expr = els.get(0).getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = expr.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("Expected to detect proper value for attribute!", "something", id.getImage());
    }

    /**
     * smoke test for a non-quoted attribute value
     */
    @Test
    public void quoteAttrValue() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_ATTR);
        assertEquals("One attribute expected!", 1, attributes.size());
        ASTAttributeValue attr = attributes.iterator().next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        assertEquals("Expected to detect proper value for attribute!", "yes|", text.getImage());
    }

    /**
     * tests whether parse correctly interprets empty non quote attribute
     */
    @Test
    public void noQuoteAttrEmpty() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_EMPTY_ATTR);
        assertEquals("two attributes expected!", 2, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        if ("http://someHost:/some_URL".equals(attr.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr = iterator.next();
        }
        assertNull("Expected to detect proper value for attribute!", attr.getImage());
    }

    /**
     * tests whether parse correctly interprets an tab instead of an attribute
     */
    @Test
    public void singleQuoteAttrTab() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_TAB_ATTR);
        assertEquals("One attribute expected!", 1, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        assertEquals("Expected to detect proper value for attribute!", "\t", text.getImage());

    }

    @Test
    public void unclosedTag() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_SIMPLE);
        assertEquals("2 tags expected", 2, elements.size());
        assertEquals("Second element should be tag:someTag", "tag:someTag", elements.get(0).getName());
        assertEquals("First element should be sorted tag:if", "tag:if", elements.get(1).getName());

        assertTrue(elements.get(1).isEmpty());
        assertTrue(elements.get(1).isUnclosed());
        assertFalse(elements.get(0).isEmpty());
        assertFalse(elements.get(0).isUnclosed());
    }

    @Test
    public void unclosedTagAndNoQuotesForAttribute() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_ATTR);
        assertEquals("2 tags expected", 2, elements.size());
        assertEquals("Second element should be tag:someTag", "tag:someTag", elements.get(0).getName());
        assertEquals("First element should be sorted tag:if", "tag:if", elements.get(1).getName());

        assertTrue(elements.get(1).isEmpty());
        assertTrue(elements.get(1).isUnclosed());
        assertFalse(elements.get(0).isEmpty());
        assertFalse(elements.get(0).isUnclosed());
    }

    @Test
    public void unclosedTagMultipleLevels() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_MULTIPLE_LEVELS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("3 tags expected", 3, elements.size());
        assertEquals("First element should be sorted tag:someTag", "tag:someTag", sortedElmnts.get(0).getName());
        assertEquals("Second element should be tag:someTag", "tag:someTag", sortedElmnts.get(1).getName());
        assertEquals("Third element should be tag:x", "tag:x", sortedElmnts.get(2).getName());

        assertFalse(sortedElmnts.get(0).isEmpty());
        assertFalse(sortedElmnts.get(0).isUnclosed());

        assertTrue(sortedElmnts.get(1).isEmpty());
        assertTrue(sortedElmnts.get(1).isUnclosed());

        assertFalse(sortedElmnts.get(2).isEmpty());
        assertFalse(sortedElmnts.get(2).isUnclosed());
    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2/&gt; &lt;b/&gt; &lt;/a1&gt; &lt;/html&gt;
     */
    @Test
    public void nestedEmptyTags() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_MULTIPLE_EMPTY_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("4 tags expected", 4, elements.size());
        assertEquals("First element should a1", "a1", sortedElmnts.get(0).getName());
        assertEquals("Second element should be a2", "a2", sortedElmnts.get(1).getName());
        assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        assertEquals("Third element should be html", "html", sortedElmnts.get(3).getName());

        // a1
        assertFalse(sortedElmnts.get(0).isEmpty());
        assertFalse(sortedElmnts.get(0).isUnclosed());

        // a2
        assertTrue(sortedElmnts.get(1).isEmpty());
        assertFalse(sortedElmnts.get(1).isUnclosed());

        // b
        assertTrue(sortedElmnts.get(2).isEmpty());
        assertFalse(sortedElmnts.get(2).isUnclosed());

        // html
        assertFalse(sortedElmnts.get(3).isEmpty());
        assertFalse(sortedElmnts.get(3).isUnclosed());

    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2&gt; &lt;a3&gt; &lt;/a2&gt; &lt;/a1&gt;
     * &lt;b/&gt; &lt;a4/&gt; &lt;/html&gt;
     */
    @Test
    public void nestedMultipleTags() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_MULTIPLE_NESTED_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("4 tags expected", 6, elements.size());
        assertEquals("First element should a1", "a1", sortedElmnts.get(0).getName());
        assertEquals("Second element should be a2", "a2", sortedElmnts.get(1).getName());
        assertEquals("Third element should be a3", "a3", sortedElmnts.get(2).getName());
        assertEquals("Forth element should be a4", "a4", sortedElmnts.get(3).getName());
        assertEquals("Fifth element should be b", "b", sortedElmnts.get(4).getName());
        assertEquals("Sixth element should be html", "html", sortedElmnts.get(5).getName());

        // a1 not empty and closed
        assertFalse(sortedElmnts.get(0).isEmpty());
        assertFalse(sortedElmnts.get(0).isUnclosed());

        // a2 not empty and closed
        assertFalse(sortedElmnts.get(1).isEmpty());
        assertFalse(sortedElmnts.get(1).isUnclosed());

        // a3 empty and not closed
        assertTrue(sortedElmnts.get(2).isEmpty());
        assertTrue(sortedElmnts.get(2).isUnclosed());

        // a4 empty but closed
        assertTrue(sortedElmnts.get(3).isEmpty());
        assertFalse(sortedElmnts.get(3).isUnclosed());

        // b empty but closed
        assertTrue(sortedElmnts.get(4).isEmpty());
        assertFalse(sortedElmnts.get(4).isUnclosed());

        // html not empty and closed
        assertFalse(sortedElmnts.get(5).isEmpty());
        assertFalse(sortedElmnts.get(5).isUnclosed());

    }

    /**
     * will test &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/x&gt; &lt;/a&gt;
     * &lt;/x&gt; . Here x is the first tag to be closed thus rendering the next
     * close of a (&lt;/a&gt;) to be disregarded.
     */
    @Test
    public void unclosedParentTagClosedBeforeChild() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_END_AFTER_PARENT_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("4 tags expected", 4, elements.size());
        assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        assertEquals("Second element should be b", "b", sortedElmnts.get(1).getName());
        assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        assertEquals("Forth element should be x", "x", sortedElmnts.get(3).getName());

        // a
        assertTrue(sortedElmnts.get(0).isEmpty());
        assertTrue(sortedElmnts.get(0).isUnclosed());

        // b
        assertTrue(sortedElmnts.get(1).isEmpty());
        assertTrue(sortedElmnts.get(1).isUnclosed());

        // b
        assertTrue(sortedElmnts.get(2).isEmpty());
        assertTrue(sortedElmnts.get(2).isUnclosed());

        // x
        assertFalse(sortedElmnts.get(3).isEmpty());
        assertFalse(sortedElmnts.get(3).isUnclosed());
    }

    /**
     * &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt; &lt;/x&gt;
     * An unmatched closing of 'z' appears randomly in the document. This should
     * be disregarded and structure of children and parents should not be
     * influenced. in other words &lt;/a&gt; should close the first &lt;a&gt;
     * tag , &lt;/x&gt; should close the first &lt;x&gt;, etc.
     */
    @Test
    public void unmatchedTagDoesNotInfluenceStructure() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_UNMATCHED_CLOSING_TAG);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("4 tags expected", 4, elements.size());
        assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        assertEquals("Second element should be b", "b", sortedElmnts.get(1).getName());
        assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        assertEquals("Forth element should be x", "x", sortedElmnts.get(3).getName());

        // a is not empty and closed
        assertFalse(sortedElmnts.get(0).isEmpty());
        assertFalse(sortedElmnts.get(0).isUnclosed());

        // b empty and unclosed
        assertTrue(sortedElmnts.get(1).isEmpty());
        assertTrue(sortedElmnts.get(1).isUnclosed());

        // b empty and unclosed
        assertTrue(sortedElmnts.get(2).isEmpty());
        assertTrue(sortedElmnts.get(2).isUnclosed());

        // x not empty and closed
        assertFalse(sortedElmnts.get(3).isEmpty());
        assertFalse(sortedElmnts.get(3).isUnclosed());
    }

    /**
     * &lt;a&gt; &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt;
     * &lt;/x&gt; An unmatched closing of 'z' appears randomly in the document.
     * This should be disregarded and structure of children and parents should
     * not be influenced. Also un unclosed &lt;a&gt; tag appears at the start of
     * the document
     */
    @Test
    public void unclosedStartTagWithUnmatchedCloseOfDifferentTag() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals("5 tags expected", 5, elements.size());
        assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        assertEquals("Second element should be a", "a", sortedElmnts.get(1).getName());
        assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        assertEquals("Forth element should be b", "b", sortedElmnts.get(3).getName());
        assertEquals("Fifth element should be x", "x", sortedElmnts.get(4).getName());

        // first a is empty and unclosed
        assertTrue(sortedElmnts.get(0).isEmpty());
        assertTrue(sortedElmnts.get(0).isUnclosed());

        // second a not empty and closed
        assertFalse(sortedElmnts.get(1).isEmpty());
        assertFalse(sortedElmnts.get(1).isUnclosed());

        // b empty and unclosed
        assertTrue(sortedElmnts.get(2).isEmpty());
        assertTrue(sortedElmnts.get(2).isUnclosed());

        // b empty and unclosed
        assertTrue(sortedElmnts.get(3).isEmpty());
        assertTrue(sortedElmnts.get(3).isUnclosed());

        // x not empty and closed
        assertFalse(sortedElmnts.get(4).isEmpty());
        assertFalse(sortedElmnts.get(4).isUnclosed());
    }

    /**
     * will sort the AST element in list in alphabetical order and if tag name
     * is the same it will sort against o1.getBeginColumn() +""+
     * o1.getBeginLine(). so first criteria is the name, then the second is the
     * column +""+line string.
     *
     * @param elements
     * @return
     */
    private List<ASTElement> sortNodesByName(List<ASTElement> elements) {
        Collections.sort(elements, new Comparator<ASTElement>() {
            @Override
            public int compare(ASTElement o1, ASTElement o2) {
                if (o1.getName() == null) {
                    return Integer.MIN_VALUE;
                }
                if (o2.getName() == null) {
                    return Integer.MAX_VALUE;
                }
                if (o1.getName().equals(o2.getName())) {
                    String o1Value = o1.getBeginColumn() + "" + o1.getBeginLine();
                    String o2Value = o2.getBeginColumn() + "" + o2.getBeginLine();
                    return o1Value.compareTo(o2Value);
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return elements;
    }

    @Test
    public void noQuoteAttrWithJspEL() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EL);
        assertEquals("One attribute expected!", 1, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTIdentifier id = attr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("Expected to detect proper value for EL in attribute!", "something", id.getImage());
    }

    private static final String TEST_SIMPLEST_HTML = "<html/>";

    private static final String TEST_ELEMENT_AND_NAMESPACE = "<h:html MyNsPrefix:MyAttr='MyValue'/>";

    private static final String TEST_CDATA = "<html><![CDATA[ some <cdata> ]] ]> ]]></html>";

    private static final String TEST_DOCTYPE = "<?xml version=\"1.0\" standalone='yes'?>\n"
            + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" "
            + "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" + "<greeting>Hello, world!</greeting>";

    private static final String TEST_ATTRIBUTE_VALUE_CONTAINING_HASH = "<tag:if something=\"#yes#\" foo=\"CREATE\">  <a href=\"#\">foo</a> </tag:if>";

    private static final String TEST_HTML_SCRIPT = "<html><head><script>Script!</script></head></html>";

    private static final String TEST_EL_IN_TAG_ATTRIBUTE = "<apex:page action=\"{!foo}\">text</apex:page>";
    private static final String TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT = "<apex:page action=\"{!/*comment here*/init}\">text</apex:page>";
    private static final String TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT_SQ = "<apex:page action='{!/*comment here*/init}'>text</apex:page>";

    private static final String TEST_EL_IN_HTML_SCRIPT = "<html><head><script>var text={!elInScript};</script></head></html>";
    private static final String TEST_EL_IN_HTML_SCRIPT_WITH_COMMENT = "<html><head><script>var text={!/*junk1*/elInScript/*junk2*/};</script></head></html>";

    private static final String TEST_QUOTED_EL_IN_HTML_SCRIPT = "<html><head><script>var text='textHere{!elInScript}';</script></head></html>";

    private static final String TEST_IMPORT_JAVASCRIPT = "<html><head><script src=\"filename.js\" /></head></html>";

    private static final String TEST_HTML_SCRIPT_WITH_ATTRIBUTE = "<html><head><script type=\"text/javascript\">Script!</script></head></html>";

    private static final String TEST_COMPLEX_SCRIPT = "<HTML><BODY><!--Java Script-->"
            + "<SCRIPT language='JavaScript' type='text/javascript'>" + "<!--function calcDays(){"
            + " date1 = date1.split(\"-\");  date2 = date2.split(\"-\");"
            + " var sDate = new Date(date1[0]+\"/\"+date1[1]+\"/\"+date1[2]);"
            + " var eDate = new Date(date2[0]+\"/\"+date2[1]+\"/\"+date2[2]);" + " onload=calcDays;//-->"
            + "</SCRIPT></BODY></HTML>;";

    private static final String TEST_INLINE_STYLE = "<html><head><style> div { color:red; } </style></head></html>";

    private static final String TEST_TEXT_IN_TAG = "<a> some text </a>";

    private static final String TEST_TAGS_NO_SPACE = "<a><b></a>";

    private static final String TEST_TAGS_WITH_DOLLAR = "<a> $ <b> $ </a>";

    private static final String TEST_TAGS_WITH_EL_WITHIN = "<a>{!expr1}{!expr2}</a>";

    private static final String TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG = "<a> some text <b> some text </a>";

    private static final String TEST_QUOTE_EL = "<tag:if something=\"{!something}\" > </tag:if>";

    private static final String TEST_ATTR = "<tag:if something=\"yes|\" > </tag:if>";

    private static final String TEST_EMPTY_ATTR = "<tag:if something= >  <a href=\"http://someHost:/some_URL\" >foo</a> </tag:if>";

    private static final String TEST_TAB_ATTR = "<tag:if something='\t' >   </tag:if>";

    private static final String TEST_UNCLOSED_SIMPLE = "<tag:someTag> <tag:if someting=\"x\" > </tag:someTag>";

    /**
     * someTag is closed just once
     */
    private static final String TEST_UNCLOSED_MULTIPLE_LEVELS = "<tag:x> <tag:someTag> <tag:someTag someting=\"x\" > </tag:someTag> </tag:x>";

    /**
     * nested empty tags
     */
    private static final String TEST_MULTIPLE_EMPTY_TAGS = "<html> <a1> <a2/> <b/> </a1> </html>";

    /**
     * multiple nested tags with some tags unclosed
     */
    private static final String TEST_MULTIPLE_NESTED_TAGS = "<html> <a1> <a2> <a3> </a2> </a1> <b/> <a4/> </html>";

    /**
     * </x> will close before </a>, thus leaving <a> to remain unclosed
     */
    private static final String TEST_UNCLOSED_END_AFTER_PARENT_CLOSE = "<x> <a> <b> <b> </x> </a> aa </x> bb </x>";

    /**
     * </z> is just a dangling closing tag not matching any parent. The parser
     * should disregard it
     */
    private static final String TEST_UNCLOSED_UNMATCHED_CLOSING_TAG = "<x> <a> <b> <b> </z> </a> </x>";

    /**
     * First <a> tag does not close. The first closing of </a> will match the
     * second opening of a. Another rogue </z> is there for testing compliance
     */
    private static final String TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE = "<a> <x> <a> <b> <b> </z> </a> </x>";

    private static final String TEST_UNCLOSED_ATTR = "<tag:someTag> <tag:if someting='x' > </tag:someTag>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_EL = "<apex:someTag something={!something} > foo </apex:someTag>";
}
