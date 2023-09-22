/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test parsing of a JSP in document style, by checking the generated AST.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 */
class JspDocStyleTest extends AbstractJspNodesTst {

    /**
     * Smoke test for JSP parser.
     */
    @Test
    void testSimplestJsp() {
        List<ASTElement> nodes = jsp.getNodes(ASTElement.class, TEST_SIMPLEST_HTML);
        assertEquals(1, nodes.size(), "Exactly " + 1 + " element(s) expected");
    }

    /**
     * Test the information on a Element and Attribute.
     */
    @Test
    void testElementAttributeAndNamespace() {
        ASTCompilationUnit root = jsp.parse(TEST_ELEMENT_AND_NAMESPACE);

        List<ASTElement> elementNodes = root.findDescendantsOfType(ASTElement.class);
        assertEquals(1, elementNodes.size(), "One element node expected!");
        ASTElement element = elementNodes.get(0);
        assertEquals("h:html", element.getName(), "Correct name expected!");
        assertTrue(element.isHasNamespacePrefix(), "Has namespace prefix!");
        assertTrue(element.isEmpty(), "Element is empty!");
        assertEquals("h", element.getNamespacePrefix(), "Correct namespace prefix of element expected!");
        assertEquals("html", element.getLocalName(), "Correct local name of element expected!");

        List<ASTAttribute> attributeNodes = root.findDescendantsOfType(ASTAttribute.class);
        assertEquals(1, attributeNodes.size(), "One attribute node expected!");
        ASTAttribute attribute = attributeNodes.get(0);
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
        ASTCompilationUnit root = jsp.parse(TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);

        List<ASTAttribute> attrsList = root.findDescendantsOfType(ASTAttribute.class);
        assertEquals(3, attrsList.size(), "Three attributes expected!");

        ASTAttribute attr = attrsList.get(0);

        assertEquals("something", attr.getName(), "Correct attribute name expected!");
        assertEquals("#yes#", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage(), "Correct attribute value expected!");

        attr = attrsList.get(1);
        assertEquals("foo", attr.getName(), "Correct attribute name expected!");
        assertEquals("CREATE", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage(), "Correct attribute value expected!");

        attr = attrsList.get(2);
        assertEquals("href", attr.getName(), "Correct attribute name expected!");
        assertEquals("#", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage(), "Correct attribute value expected!");
    }

    /**
     * Test correct parsing of CDATA.
     */
    @Test
    void testCData() {
        List<ASTCData> cdataNodes = jsp.getNodes(ASTCData.class, TEST_CDATA);

        assertEquals(1, cdataNodes.size(), "One CDATA node expected!");
        ASTCData cdata = cdataNodes.get(0);
        assertEquals(" some <cdata> ]] ]> ", cdata.getImage(), "Content incorrectly parsed!");
    }

    /**
     * Test parsing of Doctype declaration.
     */
    @Test
    void testDoctype() {
        ASTCompilationUnit root = jsp.parse(TEST_DOCTYPE);

        List<ASTDoctypeDeclaration> docTypeDeclarations = root.findDescendantsOfType(ASTDoctypeDeclaration.class);
        assertEquals(1, docTypeDeclarations.size(), "One doctype declaration expected!");
        ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations.iterator().next();
        assertEquals("html", docTypeDecl.getName(), "Correct doctype-name expected!");

        List<ASTDoctypeExternalId> externalIds = root.findDescendantsOfType(ASTDoctypeExternalId.class);
        assertEquals(1, externalIds.size(), "One doctype external id expected!");
        ASTDoctypeExternalId externalId = externalIds.iterator().next();
        assertEquals("-//W3C//DTD XHTML 1.1//EN", externalId.getPublicId(), "Correct external public id expected!");
        assertEquals("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd", externalId.getUri(), "Correct external uri expected!");

    }

    /**
     * Test parsing of a XML comment.
     *
     */
    @Test
    void testComment() {
        List<ASTCommentTag> comments = jsp.getNodes(ASTCommentTag.class, TEST_COMMENT);
        assertEquals(1, comments.size(), "One comment expected!");
        ASTCommentTag comment = comments.iterator().next();
        assertEquals("comment", comment.getImage(), "Correct comment content expected!");
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void testHtmlScript() {
        List<ASTHtmlScript> scripts = jsp.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        assertEquals("Script!", script.getImage(), "Correct script content expected!");
    }

    /**
     * Test parsing of HTML &lt;script src="x"/&gt; element. It might not be valid
     * html but it is likely to appear in .JSP files.
     */
    @Test
    void testImportHtmlScript() {
        List<ASTHtmlScript> scripts = jsp.getNodes(ASTHtmlScript.class, TEST_IMPORT_JAVASCRIPT);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        List<ASTAttributeValue> value = script.findDescendantsOfType(ASTAttributeValue.class);
        assertEquals("filename.js", value.get(0).getImage());
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void testHtmlScriptWithAttribute() {
        List<ASTHtmlScript> scripts = jsp.getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT_WITH_ATTRIBUTE);
        assertEquals(1, scripts.size(), "One script expected!");
        ASTHtmlScript script = scripts.iterator().next();
        assertEquals("Script!", script.getImage(), "Correct script content expected!");
        List<ASTAttributeValue> attrs = script.findDescendantsOfType(ASTAttributeValue.class);
        assertEquals("text/javascript", attrs.get(0).getImage());
    }

    /**
     * A complex script containing HTML comments, escapes, quotes, etc.
     */
    @Test
    void testComplexHtmlScript() {
        List<ASTHtmlScript> script = jsp.getNodes(ASTHtmlScript.class, TEST_COMPLEX_SCRIPT);
        assertEquals(1, script.size(), "One script expected!");
        ASTHtmlScript next = script.iterator().next();
        assertThat(next.getImage(), containsString("<!--"));
        List<ASTCommentTag> comments = jsp.getNodes(ASTCommentTag.class, TEST_COMPLEX_SCRIPT);
        assertEquals(1, comments.size(), "One comment expected!");
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void testInlineCss() {
        List<ASTElement> scripts = jsp.getNodes(ASTElement.class, TEST_INLINE_STYLE);
        assertEquals(3, scripts.size(), "Three elements expected!");
    }

    /**
     * Test parsing of HTML text within element.
     */
    @Test
    void testTextInTag() {
        List<ASTText> scripts = jsp.getNodes(ASTText.class, TEST_TEXT_IN_TAG);
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
        List<ASTElement> elmts = jsp.getNodes(ASTElement.class, TEST_TAGS_NO_SPACE);
        assertEquals(2, elmts.size(), "Two tags expected!");
        assertEquals("a", elmts.get(0).getName(), "Correct content expected!");
        assertEquals("b", elmts.get(1).getName(), "Correct content expected!");
    }

    /**
     * the $ sign might trick the parser into thinking an EL is next. He should
     * be able to treat it as plain text
     */
    @Test
    void unclosedTagsWithDollar() {
        List<ASTText> scripts = jsp.getNodes(ASTText.class, TEST_TAGS_WITH_DOLLAR);
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
        List<ASTElExpression> scripts = jsp.getNodes(ASTElExpression.class, TEST_TAGS_WITH_EL_WITHIN);
        assertEquals(2, scripts.size(), "Two EL expressions expected!");
        assertEquals("expr1", scripts.get(0).getImage(), "Correct content expected!");
        assertEquals("expr2", scripts.get(1).getImage(), "Correct content expected!");
    }

    /**
     * Make sure mixed expressions don't confuse the parser
     */
    @Test
    void mixedExpressions() {
        List<ASTJspExpression> exprs = jsp.getNodes(ASTJspExpression.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
        assertEquals(1, exprs.size(), "One JSP expression expected!");
        assertEquals("expr", exprs.iterator().next().getImage(), "Image of expression should be \"expr\"");
        List<ASTElExpression> els = jsp.getNodes(ASTElExpression.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
        assertEquals(2, els.size(), "Two EL expression expected!");
        assertEquals("expr", els.iterator().next().getImage(), "Image of el should be \"expr\"");

        List<ASTUnparsedText> unparsedtexts = jsp.getNodes(ASTUnparsedText.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
        assertEquals(2, unparsedtexts.size(), "Two unparsed texts expected!");
        assertEquals(" aaa ", unparsedtexts.get(0).getImage(), "Image of text should be \" aaa \"");
        assertEquals(" \\${expr} ", unparsedtexts.get(1).getImage(), "Image of text should be \"\\${expr}\"");

        // ASTText should contain the text between two tags.
        List<ASTText> texts = jsp.getNodes(ASTText.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
        assertEquals(2, texts.size(), "Two regular texts expected!");
        assertEquals(" \\${expr} ", texts.get(1).getImage(), "Image of text should be \"\\${expr}\"");
        assertEquals(" aaa ${expr}#{expr}", texts.get(0).getImage(), "Image of text should be all text between two nodes"
                + " \"  aaa ${expr}#{expr} \"");
    }

    /**
     * Make sure JSP expressions are properly detected when they are next to
     * unclosed tags.
     */
    @Test
    void unclosedTagsWithJspExpressionWithin() {
        List<ASTJspExpression> scripts = jsp.getNodes(ASTJspExpression.class, TEST_TAGS_WITH_EXPRESSION_WITHIN);
        assertEquals(2, scripts.size(), "Two JSP expressions expected!");
        ASTJspExpression script = scripts.iterator().next();
        assertEquals("expr", script.getImage(), "Correct content expected!");
    }

    /**
     * A dangling unopened ( just &lt;/closed&gt; ) tag should not influence the
     * parsing.
     */
    @Test
    @Disabled // sadly the number of
    // <opening> tags has to be >= then the number of </closing> tags
    void textBetweenUnopenedTag() {
        List<ASTText> scripts = jsp.getNodes(ASTText.class, TEST_TEXT_WITH_UNOPENED_TAG);
        assertEquals(2, scripts.size(), "Two text chunks expected!");
        ASTText script = scripts.iterator().next();
        assertEquals("$", script.getImage(), "Correct content expected!");
    }

    /**
     * Parser should be able to handle documents which start or end with
     * unparsed text
     */
    @Test
    @Disabled // sadly the number of
    // <opening> tags has to be >= then the number of </closing> tags
    void textMultipleClosingTags() {
        List<ASTText> scripts = jsp.getNodes(ASTText.class, TEST_MULTIPLE_CLOSING_TAGS);
        assertEquals(4, scripts.size(), "Four text chunks expected!");
        ASTText script = scripts.iterator().next();
        assertEquals(" some text ", script.getImage(), "Correct content expected!");
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    void textAfterOpenAndClosedTag() {
        List<ASTElement> nodes = jsp.getNodes(ASTElement.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals(2, nodes.size(), "Two elements expected!");
        assertEquals("a", nodes.get(0).getName(), "First element should be a");
        assertFalse(nodes.get(0).isUnclosed(), "first element should be closed");
        assertEquals("b", nodes.get(1).getName(), "Second element should be b");
        assertTrue(nodes.get(1).isUnclosed(), "Second element should not be closed");

        List<ASTText> text = jsp.getNodes(ASTText.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        assertEquals(2, text.size(), "Two text chunks expected!");
    }

    @Test
    void quoteEL() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_QUOTE_EL);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("${something}", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    @Test
    void quoteExpression() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_QUOTE_EXPRESSION);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<%=something%>", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    @Test
    @Disabled // tags contain quotes and break attribute parsing
    void quoteTagInAttribute() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_QUOTE_TAG_IN_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<bean:write name=\"x\" property=\"z\">",
                attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * smoke test for a non-quoted attribute value
     */
    @Test
    void noQuoteAttrValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("yes|", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * tests whether JSP el is properly detected as attribute value
     */
    @Test
    void noQuoteAttrWithJspEL() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EL);
        assertEquals(2, attributes.size(), "two attributes expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr2 = iterator.next();
        if ("url".equals(attr2.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr2 = iterator.next();
        }
        assertEquals("${something}", attr2.getImage(), "Expected to detect proper value for EL in attribute!");
    }

    /**
     * tests whether parse correctly detects presence of JSP expression &lt;%= %&gt;
     * within an non-quoted attribute value
     */
    @Test
    void noQuoteAttrWithJspExpression() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EXPRESSION);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<%=something%>", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * tests whether parse correctly interprets empty non quote attribute
     */
    @Test
    void noQuoteAttrEmpty() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_EMPTY_ATTR);
        assertEquals(2, attributes.size(), "two attributes expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        if ("http://someHost:/some_URL".equals(attr.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr = iterator.next();
        }
        assertEquals("", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * tests whether parse correctly interprets an cr lf instead of an attribute
     */
    @Test
    void noQuoteAttrCrLf() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_CR_LF_ATTR);
        assertEquals(2, attributes.size(), "One attribute expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        if ("http://someHost:/some_URL".equals(attr.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr = iterator.next();
        }
        assertEquals("\n", attr.getImage(), "Expected to detect proper value for attribute!");

    }

    /**
     * tests whether parse correctly interprets an tab instead of an attribute
     */
    @Test
    void noQuoteAttrTab() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAB_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        assertEquals("\t", attr.getImage(), "Expected to detect proper value for attribute!");

    }

    /**
     * tests whether parse does not fail in the presence of unclosed JSP
     * expression &lt;%= within an non-quoted attribute value
     */
    @Test
    void noQuoteAttrWithMalformedJspExpression() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_MALFORMED_EXPR);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<%=something", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * test a no quote attribute value which contains a scriptlet &lt;% %&gt; within
     * its value
     */
    @Test
    @Disabled // nice test for future development
    void noQuoteAttrWithScriptletInValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_SCRIPTLET);
        assertEquals(1, attributes.size(), "One attribute expected!");

        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<% String a = \"1\";%>", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * test a no quote attribute value can contain a tag (e.g.
     * attr=&lt;bean:write property="value" /&gt;)
     *
     */
    @Test
    @Disabled // nice test for future development
    void noQuoteAttrWithBeanWriteTagAsValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAG_IN_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");

        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<% String a = \"1\";%>", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * test a quote attribute value can contain a tag (e.g.
     * attr="&lt;bean:write property="value" /&gt;" ) Not sure if it's legal JSP code
     * but most JSP engine accept and properly treat this value at runtime
     */
    @Test
    @Disabled // nice test for future development
    void quoteAttrWithBeanWriteTagAsValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAG_IN_ATTR);
        assertEquals(1, attributes.size(), "One attribute expected!");

        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("<% String a = \"1\";%>", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * test a no quote attribute value which contains the EL dollar sign $
     * within its value
     */
    @Test
    @Disabled // nice test for future development
    void noQuoteAttrWithDollarSignInValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_DOLLAR);
        assertEquals(2, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("${something", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    /**
     * test a no quote attribute value which contains the EL sharp sign # within
     * its value
     */
    @Test
    @Disabled // nice test for future development
    void noQuoteAttrWithSharpSymbolInValue() {
        List<ASTAttributeValue> attributes = jsp.getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_HASH);
        assertEquals(1, attributes.size(), "One attribute expected!");
        ASTAttributeValue attr = attributes.iterator().next();
        assertEquals("#{something", attr.getImage(), "Expected to detect proper value for attribute!");
    }

    @Test
    void unclosedTag() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_SIMPLE);
        assertEquals(2, elements.size(), "2 tags expected");
        assertEquals("tag:someTag", elements.get(0).getName(), "First element should be tag:someTag");
        assertEquals("tag:if", elements.get(1).getName(), "Second element should be sorted tag:if");

        assertFalse(elements.get(0).isEmpty());
        assertFalse(elements.get(0).isUnclosed());
        assertTrue(elements.get(1).isEmpty());
        assertTrue(elements.get(1).isUnclosed());
    }

    @Test
    void unclosedTagAndNoQuotesForAttribute() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_NO_QUOTE_ATTR);
        assertEquals(2, elements.size(), "2 tags expected");
        ASTElement ifTag = elements.get(1);
        ASTElement someTag = elements.get(0);

        assertEquals("tag:if", ifTag.getName());
        assertEquals("tag:someTag", someTag.getName());

        assertTrue(ifTag.isEmpty());
        assertTrue(ifTag.isUnclosed());
        assertFalse(someTag.isEmpty());
        assertFalse(someTag.isUnclosed());
    }

    @Test
    void unclosedTagMultipleLevels() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_MULTIPLE_LEVELS);
        assertEquals(3, elements.size(), "3 tags expected");
        ASTElement xtag = elements.get(0);
        ASTElement outerTag = elements.get(1);
        ASTElement innerTag = elements.get(2);

        assertEquals("tag:someTag", innerTag.getName());
        assertEquals("tag:someTag", outerTag.getName());
        assertEquals("tag:x", xtag.getName());

        assertFalse(innerTag.isEmpty());
        assertFalse(innerTag.isUnclosed());

        assertTrue(outerTag.isEmpty());
        assertTrue(outerTag.isUnclosed());

        assertFalse(xtag.isEmpty());
        assertFalse(xtag.isUnclosed());
    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2/&gt; &lt;b/&gt; &lt;/a1&gt; &lt;/html&gt;
     */
    @Test
    void nestedEmptyTags() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_MULTIPLE_EMPTY_TAGS);
        assertEquals(4, elements.size(), "4 tags expected");
        ASTElement a1Tag = elements.get(1);
        ASTElement a2Tag = elements.get(2);
        ASTElement bTag = elements.get(3);
        ASTElement htmlTag = elements.get(0);

        assertEquals("a1", a1Tag.getName());
        assertEquals("a2", a2Tag.getName());
        assertEquals("b", bTag.getName());
        assertEquals("html", htmlTag.getName());

        // a1
        assertFalse(a1Tag.isEmpty());
        assertFalse(a1Tag.isUnclosed());

        // a2
        assertTrue(a2Tag.isEmpty());
        assertFalse(a2Tag.isUnclosed());

        // b
        assertTrue(bTag.isEmpty());
        assertFalse(bTag.isUnclosed());

        // html
        assertFalse(htmlTag.isEmpty());
        assertFalse(htmlTag.isUnclosed());

    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2&gt; &lt;a3&gt; &lt;/a2&gt; &lt;/a1&gt; &lt;b/&gt; &lt;a4/&gt; &lt;/html&gt;
     */
    @Test
    void nestedMultipleTags() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_MULTIPLE_NESTED_TAGS);
        ASTElement html = elements.get(0);
        ASTElement a1 = elements.get(1);
        ASTElement a2 = elements.get(2);
        ASTElement a3 = elements.get(3);
        ASTElement b = elements.get(4);
        ASTElement a4 = elements.get(5);


        assertEquals(6, elements.size(), "6 tags expected");
        assertEquals("a1", a1.getName());
        assertEquals("a2", a2.getName());
        assertEquals("a3", a3.getName());
        assertEquals("a4", a4.getName());
        assertEquals("b", b.getName());
        assertEquals("html", html.getName());

        // a1 not empty and closed
        assertFalse(a1.isEmpty());
        assertFalse(a1.isUnclosed());

        // a2 not empty and closed
        assertFalse(a2.isEmpty());
        assertFalse(a2.isUnclosed());

        // a3 empty and not closed
        assertTrue(a3.isEmpty());
        assertTrue(a3.isUnclosed());

        // a4 empty but closed
        assertTrue(a4.isEmpty());
        assertFalse(a4.isUnclosed());

        // b empty but closed
        assertTrue(b.isEmpty());
        assertFalse(b.isUnclosed());

        // html not empty and closed
        assertFalse(html.isEmpty());
        assertFalse(html.isUnclosed());

    }

    /**
     * will test &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/x&gt; &lt;/a&gt; &lt;/x&gt; .
     * Here x is the first tag to be closed thus rendering the next close of a (&lt;/a&gt;)
     * to be disregarded.
     */
    @Test
    void unclosedParentTagClosedBeforeChild() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_END_AFTER_PARENT_CLOSE);
        assertEquals(4, elements.size(), "4 tags expected");
        ASTElement x = elements.get(0);
        ASTElement a = elements.get(1);
        ASTElement b = elements.get(2);
        ASTElement b2 = elements.get(3);


        assertEquals("a", a.getName());
        assertEquals("b", b.getName());
        assertEquals("b", b2.getName());
        assertEquals("x", x.getName());

        // a
        assertTrue(a.isEmpty());
        assertTrue(a.isUnclosed());

        // b
        assertTrue(b.isEmpty());
        assertTrue(b.isUnclosed());

        // b
        assertTrue(b2.isEmpty());
        assertTrue(b2.isUnclosed());

        // x
        assertFalse(x.isEmpty());
        assertFalse(x.isUnclosed());
    }

    /**
     * &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt; &lt;/x&gt; An unmatched closing of 'z' appears
     * randomly in the document. This should be disregarded and structure of
     * children and parents should not be influenced. in other words &lt;/a&gt; should
     * close the first &lt;a&gt; tag , &lt;/x&gt; should close the first &lt;x&gt;, etc.
     */
    @Test
    void unmatchedTagDoesNotInfluenceStructure() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_UNMATCHED_CLOSING_TAG);
        assertEquals(4, elements.size(), "4 tags expected");
        ASTElement x = elements.get(0);
        ASTElement a = elements.get(1);
        ASTElement b1 = elements.get(2);
        ASTElement b2 = elements.get(3);

        assertEquals("a", a.getName());
        assertEquals("b", b1.getName());
        assertEquals("b", b2.getName());
        assertEquals("x", x.getName());

        // a is not empty and closed
        assertFalse(a.isEmpty());
        assertFalse(a.isUnclosed());

        // b empty and unclosed
        assertTrue(b1.isEmpty());
        assertTrue(b1.isUnclosed());

        // b empty and unclosed
        assertTrue(b2.isEmpty());
        assertTrue(b2.isUnclosed());

        // x not empty and closed
        assertFalse(x.isEmpty());
        assertFalse(x.isUnclosed());
    }

    /**
     * &lt;a&gt; &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt; &lt;/x&gt;
     * An unmatched closing of 'z' appears randomly in the document. This
     * should be disregarded and structure of children and parents should not be influenced.
     * Also un unclosed &lt;a&gt; tag appears at the start of the document
     */
    @Test
    void unclosedStartTagWithUnmatchedCloseOfDifferentTag() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE);
        assertEquals(5, elements.size(), "5 tags expected");
        ASTElement a1 = elements.get(0);
        ASTElement x = elements.get(1);
        ASTElement a2 = elements.get(2);
        ASTElement b1 = elements.get(3);
        ASTElement b2 = elements.get(4);

        assertEquals("a", a1.getName());
        assertEquals("a", a2.getName());
        assertEquals("b", b1.getName());
        assertEquals("b", b2.getName());
        assertEquals("x", x.getName());

        // first a is empty and unclosed
        assertTrue(a1.isEmpty());
        assertTrue(a1.isUnclosed());

        // second a not empty and closed
        assertFalse(a2.isEmpty());
        assertFalse(a2.isUnclosed());

        // b empty and unclosed
        assertTrue(b1.isEmpty());
        assertTrue(b1.isUnclosed());

        // b empty and unclosed
        assertTrue(b2.isEmpty());
        assertTrue(b2.isUnclosed());

        // x not empty and closed
        assertFalse(x.isEmpty());
        assertFalse(x.isUnclosed());
    }

    /**
     * {@link #TEST_UNCLOSED_END_OF_DOC}
     * &lt;tag:x&gt; &lt;tag:y&gt;
     * Tests whether parser breaks on no closed tags at all
     */
    @Test
    // This is yet to be improved. If a closing tag does not
    // exist no tags will be marked as empty :(
    @Disabled
    void unclosedEndOfDoc() {
        List<ASTElement> elements = jsp.getNodes(ASTElement.class, TEST_UNCLOSED_END_OF_DOC);
        assertEquals(2, elements.size(), "2 tags expected");
        ASTElement x = elements.get(0);
        ASTElement y = elements.get(1);

        assertEquals("tag:x", x.getName());
        assertEquals("tag:y", y.getName());

        // b
        // assertTrue(sortedElmnts.get(0).isEmpty());
        assertTrue(x.isUnclosed());

        // b
        assertTrue(y.isEmpty());
        assertTrue(y.isUnclosed());
    }

    private static final String TEST_SIMPLEST_HTML = "<html/>";

    private static final String TEST_ELEMENT_AND_NAMESPACE = "<h:html MyNsPrefix:MyAttr='MyValue'/>";

    private static final String TEST_CDATA = "<html><![CDATA[ some <cdata> ]] ]> ]]></html>";

    private static final String TEST_DOCTYPE = "<?xml version=\"1.0\" standalone='yes'?>\n"
            + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" "
            + "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" + "<greeting>Hello, world!</greeting>";

    private static final String TEST_COMMENT = "<html><!-- comment --></html>";

    private static final String TEST_ATTRIBUTE_VALUE_CONTAINING_HASH = "<tag:if something=\"#yes#\" foo=\"CREATE\">  <a href=\"#\">foo</a> </tag:if>";

    private static final String TEST_HTML_SCRIPT = "<html><head><script>Script!</script></head></html>";

    private static final String TEST_IMPORT_JAVASCRIPT = "<html><head><script src=\"filename.js\" type=\"text/javascript\"/></head></html>";

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

    private static final String TEST_TAGS_WITH_EL_WITHIN = "<a>#{expr1}<b>${expr2}</a>";

    private static final String TEST_TAGS_WITH_MIXED_EXPRESSIONS = "<a> aaa ${expr} #{expr} <%=expr%> <b> \\${expr} </a>";

    private static final String TEST_TAGS_WITH_EXPRESSION_WITHIN = "<a> <%=expr%> <b> <%=expr%> </a>";

    private static final String TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG = "<a> some text <b> some text </a>";

    private static final String TEST_TEXT_WITH_UNOPENED_TAG = "<a> some text </b> some text </a>";

    private static final String TEST_MULTIPLE_CLOSING_TAGS = "<a> some text </b> </b> </b> some text </a>";

    private static final String TEST_QUOTE_EL = "<tag:if something=\"${something}\" > </tag:if>";

    private static final String TEST_QUOTE_EXPRESSION = "<tag:if something=\"<%=something%>\" >  </tag:if>";

    private static final String TEST_QUOTE_TAG_IN_ATTR = "<tag:if something=\"<bean:write name=\"x\" property=\"z\">\" >  "
            + "<a href=http://someHost:/some_URL >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR = "<tag:if something=yes| > </tag:if>";

    private static final String TEST_NO_QUOTE_EMPTY_ATTR = "<tag:if something= >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_TAG_IN_ATTR = "<tag:if something=<bean:write name=\"x\" property=\"z\"> >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_CR_LF_ATTR = "<tag:if something=\r\n >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_TAB_ATTR = "<tag:if something=\t >   </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_EL = "<tag:if something=${something} >  <a href=url >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_EXPRESSION = "<tag:if something=<%=something%> >  </tag:if>";

    /**
     * same as {@link #TEST_NO_QUOTE_ATTR_WITH_EXPRESSION} only expression is
     * not properly closed
     */
    private static final String TEST_NO_QUOTE_ATTR_WITH_MALFORMED_EXPR = "<tag:if something=<%=something >  </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_SCRIPTLET = "<tag:if something=<% String a = \"1\";%>x >  </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_DOLLAR = "<tag:if something=${something >  <a href=${ >foo</a> </tag:if>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_HASH = "<tag:if something=#{something >  <a href=#{url} >foo</a> </tag:if>";

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

    private static final String TEST_UNCLOSED_END_OF_DOC = "<tag:x> <tag:y>";

    private static final String TEST_UNCLOSED_NO_QUOTE_ATTR = "<tag:someTag> <tag:if something=x > </tag:someTag>";
}
