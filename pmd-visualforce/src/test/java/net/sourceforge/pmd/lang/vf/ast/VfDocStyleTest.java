/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test parsing of a VF in document style, by checking the generated AST.
 * Original @author pieter_van_raemdonck - Application Engineers NV/SA -
 * www.ae.be
 *
 * @author sergey.gorbaty - VF adaptation
 *
 */
class VfDocStyleTest extends AbstractVfTest {

    /**
     * Smoke test for VF parser.
     */
    @Test
    void testSimplestVf() {
        List<ASTElement> nodes = vf.getNodes(ASTElement.class, TEST_SIMPLEST_HTML);
        assertEquals(1, nodes.size(), "Exactly " + 1 + " element(s) expected");
    }

    /**
     * Test the information on a Element and Attribute.
     */
    @Test
    void testElementAttributeAndNamespace() {
        ASTCompilationUnit root = vf.parse(TEST_ELEMENT_AND_NAMESPACE);


        List<ASTElement> elementNodes = root.findDescendantsOfType(ASTElement.class);
        assertEquals(1, elementNodes.size(), "One element node expected!");
        ASTElement element = elementNodes.iterator().next();
        assertEquals("h:html", element.getName(), "Correct name expected!");
        assertTrue(element.isHasNamespacePrefix(), "Has namespace prefix!");
        assertTrue(element.isEmpty(), "Element is empty!");
        assertEquals("h", element.getNamespacePrefix(), "Correct namespace prefix of element expected!");
        assertEquals("html", element.getLocalName(), "Correct local name of element expected!");

        List<ASTAttribute> attributeNodes = root.findDescendantsOfType(ASTAttribute.class);
        assertEquals(1, attributeNodes.size(), "One attribute node expected!");
        ASTAttribute attribute = attributeNodes.iterator().next();
        assertEquals("MyNsPrefix:MyAttr", attribute.getName(), "Correct name expected!");
        assertTrue(attribute.isHasNamespacePrefix(), "Has namespace prefix!");
        assertEquals("MyNsPrefix", attribute.getNamespacePrefix(), "Correct namespace prefix of element expected!");
        assertEquals("MyAttr", attribute.getLocalName(), "Correct local name of element expected!");

    }

    /**
     * Test exposing a bug of parsing error when having a hash as last character
     * in an attribute value.
     *
     */
    @Test
    void testAttributeValueContainingHash() {

        List<ASTAttribute> attributes = vf.getNodes(ASTAttribute.class, TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);
        assertEquals(3, attributes.size(), "Three attributes expected!");

        ASTAttribute attr = attributes.get(0);
        assertEquals("something", attr.getName(), "Correct attribute name expected!");
        assertEquals("#yes#", attr.getFirstDescendantOfType(ASTText.class).getImage(),
                "Correct attribute value expected!");

        attr = attributes.get(1);
        assertEquals("foo", attr.getName(), "Correct attribute name expected!");
        assertEquals("CREATE", attr.getFirstDescendantOfType(ASTText.class).getImage(),
                "Correct attribute value expected!");

        attr = attributes.get(2);
        assertEquals("href", attr.getName(), "Correct attribute name expected!");
        assertEquals("#", attr.getFirstDescendantOfType(ASTText.class).getImage(), "Correct attribute value expected!");

    }

    /**
     * Test correct parsing of CDATA.
     */
    @Test
    void testCData() {
        List<ASTCData> cdataNodes = vf.getNodes(ASTCData.class, TEST_CDATA);

        assertEquals(1, cdataNodes.size(), "One CDATA node expected!");
        ASTCData cdata = cdataNodes.iterator().next();
        assertEquals(" some <cdata> ]] ]> ", cdata.getImage(), "Content incorrectly parsed!");
    }

    /**
     * Test parsing of Doctype declaration.
     */
    @Test
    void testDoctype() {
        ASTCompilationUnit root = vf.parse(TEST_DOCTYPE);

        List<ASTDoctypeDeclaration> docTypeDeclarations = root.findDescendantsOfType(ASTDoctypeDeclaration.class);
        assertEquals(1, docTypeDeclarations.size(), "One doctype declaration expected!");
        ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations.iterator().next();
        assertEquals("html", docTypeDecl.getName(), "Correct doctype-name expected!");

        List<ASTDoctypeExternalId> externalIds = root.findDescendantsOfType(ASTDoctypeExternalId.class);
        assertEquals(1, externalIds.size(), "One doctype external id expected!");
        ASTDoctypeExternalId externalId = externalIds.iterator().next();
        assertEquals("-//W3C//DTD XHTML 1.1//EN", externalId.getPublicId(), "Correct external public id expected!");
        assertEquals("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd", externalId.getUri(),
                "Correct external uri expected!");

    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void testHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Script!", text.getImage(), "Correct script content expected!");
    }

    /**
     * Test parsing of EL in attribute of an element.
     */
    @Test
    void testELInTagValue() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE);
        assertEquals(1, elements.size(), "One element expected!");
        ASTElement element = elements.iterator().next();
        ASTAttributeValue attribute = element.getFirstDescendantOfType(ASTAttributeValue.class);
        ASTIdentifier id = attribute.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("foo", id.getImage(), "Correct identifier expected");

    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    void testELInTagValueWithCommentDQ() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT);
        assertEquals(1, elements.size(), "One element expected!");
        ASTElement element = elements.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("init", id.getImage(), "Correct identifier expected");
    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    void testELInTagValueWithCommentSQ() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT_SQ);
        assertEquals(1, elements.size(), "One element expected!");
        ASTElement element = elements.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("init", id.getImage(), "Correct identifier expected");

    }

    /**
     * Test parsing of EL in HTML &lt;script&gt; element.
     */
    @Test
    void testELInHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_EL_IN_HTML_SCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("vartext=", text.getImage(), "Correct script content expected!");
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("elInScript", id.getImage(), "Correct EL content expected!");
    }

    /**
     * Test parsing of inline comment in EL.
     */
    @Test
    void testInlineCommentInEL() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_EL_IN_HTML_SCRIPT_WITH_COMMENT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("vartext=", text.getImage(), "Correct script content expected!");
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("elInScript", id.getImage(), "Correct EL content expected!");
    }

    /**
     * Test parsing of quoted EL in HTML &lt;script&gt; element.
     */
    @Test
    void testQuotedELInHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_QUOTED_EL_IN_HTML_SCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("vartext='textHere", text.getImage(), "Correct script content expected!");
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("elInScript", id.getImage(), "Correct EL content expected!");
    }

    /**
     * Test parsing of HTML &lt;script src="x"/&gt; element. It might not be
     * valid html but it is likely to appear in .page files.
     */
    @Test
    void testImportHtmlScript() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_IMPORT_JAVASCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        List<ASTAttribute> attr = script.findDescendantsOfType(ASTAttribute.class);
        assertEquals(1, attr.size(), "One script expected!");
        ASTAttribute att = attr.iterator().next();
        ASTAttributeValue val = att.getFirstChildOfType(ASTAttributeValue.class);
        ASTText text = val.getFirstChildOfType(ASTText.class);
        assertEquals("filename.js", text.getImage());
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void testHtmlScriptWithAttribute() {
        List<ASTHtmlScript> scripts = vf.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT_WITH_ATTRIBUTE);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        assertEquals("Script!", text.getImage(), "Correct script content expected!");
        List<ASTText> attrs = script.findDescendantsOfType(ASTText.class);
        assertEquals("text/javascript", attrs.get(0).getImage());
    }

    /**
     * A complex script containing HTML comments, escapes, quotes, etc.
     */
    @Test
    void testComplexHtmlScript() {
        List<ASTHtmlScript> script = vf.getNodes(ASTHtmlScript.class, TEST_COMPLEX_SCRIPT);
        assertEquals(1, script.size(), "One script expected!");
        ASTHtmlScript next = script.iterator().next();
        ASTText text = next.getFirstChildOfType(ASTText.class);
        assertTrue(text.getImage().contains("<!--"));

    }

    /**
     * Test parsing of HTML &lt;style&gt; element.
     */
    @Test
    void testInlineCss() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_INLINE_STYLE);
        assertEquals(3, elements.size(), "Two elements expected!");
    }

    /**
     * Test parsing of HTML text within element.
     */
    @Test
    void testTextInTag() {
        List<ASTText> scripts = vf.getNodes(ASTText.class, TEST_TEXT_IN_TAG);
        assertEquals(1, scripts.size(), "One text chunk expected!");
        ASTText script = scripts.iterator().next();
        assertEquals(" some text ", script.getImage(), "Correct content expected!");
    }

    /**
     * Test parsing of HTML with no spaces between tags. Parser is likely in
     * this scenario.
     */
    @Test
    void noSpacesBetweenTags() {
        List<ASTElement> scripts = vf.getNodes(ASTElement.class, TEST_TAGS_NO_SPACE);
        assertEquals(2, scripts.size(), "Two tags expected!");
        Iterator<ASTElement> iterator = scripts.iterator();
        ASTElement script = iterator.next();
        assertEquals("a", script.getName(), "Correct content expected!");
        script = iterator.next();
        assertEquals("b", script.getName(), "Correct content expected!");
    }

    /**
     * the $ sign might trick the parser into thinking an EL is next. He should
     * be able to treat it as plain text
     */
    @Test
    void unclosedTagsWithDollar() {
        List<ASTText> scripts = vf.getNodes(ASTText.class, TEST_TAGS_WITH_DOLLAR);
        assertEquals(2, scripts.size(), "Two text chunks expected!");
        ASTText script = scripts.iterator().next();
        assertEquals(" $ ", script.getImage(), "Correct content expected!");
    }

    /**
     * Make sure EL expressions aren't treated as plain text when they are
     * around unclosed tags.
     */
    @Test
    void unclosedTagsWithELWithin() {
        List<ASTElement> element = vf.getNodes(ASTElement.class, TEST_TAGS_WITH_EL_WITHIN);
        assertEquals(1, element.size(), "One element expected!");

        for (ASTElement elem : element) {
            ASTContent content = elem.getFirstChildOfType(ASTContent.class);
            List<ASTElExpression> els = content.findChildrenOfType(ASTElExpression.class);
            assertEquals(2, els.size(), "Two EL expressions expected!");

            ASTElExpression node = (ASTElExpression) content.getChild(0);
            ASTIdentifier id = node.getFirstDescendantOfType(ASTIdentifier.class);
            assertEquals("expr1", id.getImage(), "Correct content expected!");
            node = (ASTElExpression) content.getChild(1);
            id = node.getFirstDescendantOfType(ASTIdentifier.class);
            assertEquals("expr2", id.getImage(), "Correct content expected!");
        }

    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void textAfterOpenAndClosedTag() {
        List<ASTElement> nodes = vf.getNodes(ASTElement.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals(2, nodes.size(), "Two elements expected!");
        assertEquals("a", nodes.get(0).getName(), "First element should be a");
        assertFalse(nodes.get(0).isUnclosed(), "first element should be closed");
        assertEquals("b", nodes.get(1).getName(), "Second element should be b");
        assertTrue(nodes.get(1).isUnclosed(), "Second element should not be closed");

        List<ASTText> text = vf.getNodes(ASTText.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals(2, text.size(), "Two text chunks expected!");
    }

    @Test
    void quoteEL() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_QUOTE_EL);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        List<ASTElExpression> els = attr.findChildrenOfType(ASTElExpression.class);
        assertEquals(1, els.size(), "Must be 1!");
        ASTExpression expr = els.get(0).getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = expr.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("something", id.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * smoke test for a non-quoted attribute value
     */
    @Test
    void quoteAttrValue() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        assertEquals("yes|", text.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * tests whether parse correctly interprets empty non quote attribute
     */
    @Test
    void noQuoteAttrEmpty() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_EMPTY_ATTR);
        assertEquals(2, attributes.size(), "two attributes expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        if ("http://someHost:/some_URL".equals(attr.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr = iterator.next();
        }
        assertNull(attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * tests whether parse correctly interprets an tab instead of an attribute
     */
    @Test
    void singleQuoteAttrTab() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_TAB_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        assertEquals("\t", text.getImage(), "Expected to detect proper value for attribute!");

    }

    @Test
    void unclosedTag() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_SIMPLE);
        assertEquals(2, elements.size(), "2 tags expected");
        assertEquals("tag:someTag", elements.get(0).getName(), "Second element should be tag:someTag");
        assertEquals("tag:if", elements.get(1).getName(), "First element should be sorted tag:if");

        assertTrue(elements.get(1).isEmpty());
        assertTrue(elements.get(1).isUnclosed());
        assertFalse(elements.get(0).isEmpty());
        assertFalse(elements.get(0).isUnclosed());
    }

    @Test
    void unclosedTagAndNoQuotesForAttribute() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_ATTR);
        assertEquals(2, elements.size(), "2 tags expected");
        assertEquals("tag:someTag", elements.get(0).getName(), "Second element should be tag:someTag");
        assertEquals("tag:if", elements.get(1).getName(), "First element should be sorted tag:if");

        assertTrue(elements.get(1).isEmpty());
        assertTrue(elements.get(1).isUnclosed());
        assertFalse(elements.get(0).isEmpty());
        assertFalse(elements.get(0).isUnclosed());
    }

    @Test
    void unclosedTagMultipleLevels() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_MULTIPLE_LEVELS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(3, elements.size(), "3 tags expected");
        assertEquals("tag:someTag", sortedElmnts.get(0).getName(), "First element should be sorted tag:someTag");
        assertEquals("tag:someTag", sortedElmnts.get(1).getName(), "Second element should be tag:someTag");
        assertEquals("tag:x", sortedElmnts.get(2).getName(), "Third element should be tag:x");

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
    void nestedEmptyTags() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_MULTIPLE_EMPTY_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(4, elements.size(), "4 tags expected");
        assertEquals("a1", sortedElmnts.get(0).getName(), "First element should a1");
        assertEquals("a2", sortedElmnts.get(1).getName(), "Second element should be a2");
        assertEquals("b", sortedElmnts.get(2).getName(), "Third element should be b");
        assertEquals("html", sortedElmnts.get(3).getName(), "Third element should be html");

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
    void nestedMultipleTags() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_MULTIPLE_NESTED_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(6, elements.size(), "4 tags expected");
        assertEquals("a1", sortedElmnts.get(0).getName(), "First element should a1");
        assertEquals("a2", sortedElmnts.get(1).getName(), "Second element should be a2");
        assertEquals("a3", sortedElmnts.get(2).getName(), "Third element should be a3");
        assertEquals("a4", sortedElmnts.get(3).getName(), "Forth element should be a4");
        assertEquals("b", sortedElmnts.get(4).getName(), "Fifth element should be b");
        assertEquals("html", sortedElmnts.get(5).getName(), "Sixth element should be html");

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
    void unclosedParentTagClosedBeforeChild() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_END_AFTER_PARENT_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(4, elements.size(), "4 tags expected");
        assertEquals("a", sortedElmnts.get(0).getName(), "First element should be 'a'");
        assertEquals("b", sortedElmnts.get(1).getName(), "Second element should be b");
        assertEquals("b", sortedElmnts.get(2).getName(), "Third element should be b");
        assertEquals("x", sortedElmnts.get(3).getName(), "Forth element should be x");

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
    void unmatchedTagDoesNotInfluenceStructure() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_UNMATCHED_CLOSING_TAG);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(4, elements.size(), "4 tags expected");
        assertEquals("a", sortedElmnts.get(0).getName(), "First element should be 'a'");
        assertEquals("b", sortedElmnts.get(1).getName(), "Second element should be b");
        assertEquals("b", sortedElmnts.get(2).getName(), "Third element should be b");
        assertEquals("x", sortedElmnts.get(3).getName(), "Forth element should be x");

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
    void unclosedStartTagWithUnmatchedCloseOfDifferentTag() {
        List<ASTElement> elements = vf.getNodes(ASTElement.class, TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        assertEquals(5, elements.size(), "5 tags expected");
        assertEquals("a", sortedElmnts.get(0).getName(), "First element should be 'a'");
        assertEquals("a", sortedElmnts.get(1).getName(), "Second element should be a");
        assertEquals("b", sortedElmnts.get(2).getName(), "Third element should be b");
        assertEquals("b", sortedElmnts.get(3).getName(), "Forth element should be b");
        assertEquals("x", sortedElmnts.get(4).getName(), "Fifth element should be x");

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
    void noQuoteAttrWithJspEL() {
        List<ASTAttributeValue> attributes = vf.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EL);
        assertEquals(1, attributes.size(), "One attribute expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTIdentifier id = attr.getFirstDescendantOfType(ASTIdentifier.class);
        assertEquals("something", id.getImage(), "Expected to detect proper value for EL in attribute!");
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

    private static final String TEST_UNCLOSED_SIMPLE = "<tag:someTag> <tag:if something=\"x\" > </tag:someTag>";

    /**
     * someTag is closed just once
     */
    private static final String TEST_UNCLOSED_MULTIPLE_LEVELS = "<tag:x> <tag:someTag> <tag:someTag something=\"x\" > </tag:someTag> </tag:x>";

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

    private static final String TEST_UNCLOSED_ATTR = "<tag:someTag> <tag:if something='x' > </tag:someTag>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_EL = "<apex:someTag something={!something} > foo </apex:someTag>";
}
