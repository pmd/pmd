package net.sourceforge.pmd.lang.jsp.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;

import org.junit.Ignore;
import org.junit.Test;
/**
 * Test parsing of a JSP in document style, by checking the generated AST.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 */
public class JspDocStyleTest extends AbstractJspNodesTst {

	/**
	 * Smoke test for JSP parser.
	 *
	 * @throws Throwable
	 */
    @Test
    public void testSimplestJsp() throws Throwable {
		assertNumberOfNodes(ASTElement.class, TEST_SIMPLEST_HTML, 1);
	}

	/**
	 * Test the information on a Element and Attribute.
	 *
	 * @throws Throwable
	 */
    @Test
	public void testElementAttributeAndNamespace() throws Throwable {
		Set<Object> nodes = getNodes(null, TEST_ELEMENT_AND_NAMESPACE);

		Set<ASTElement> elementNodes = getNodesOfType(ASTElement.class, nodes);
		assertEquals("One element node expected!", 1, elementNodes.size());
		ASTElement element = elementNodes.iterator().next();
		assertEquals("Correct name expected!", "h:html", element.getName());
		assertEquals("Has namespace prefix!", true, element.isHasNamespacePrefix());
		assertEquals("Element is empty!", true, element.isEmpty());
		assertEquals("Correct namespace prefix of element expected!", "h", element
				.getNamespacePrefix());
		assertEquals("Correct local name of element expected!", "html", element
				.getLocalName());

		Set<ASTAttribute> attributeNodes = getNodesOfType(ASTAttribute.class, nodes);
		assertEquals("One attribute node expected!", 1, attributeNodes.size());
		ASTAttribute attribute = (ASTAttribute) attributeNodes.iterator().next();
		assertEquals("Correct name expected!", "MyNsPrefix:MyAttr", attribute
				.getName());
		assertEquals("Has namespace prefix!", true, attribute.isHasNamespacePrefix());
		assertEquals("Correct namespace prefix of element expected!", "MyNsPrefix",
				attribute.getNamespacePrefix());
		assertEquals("Correct local name of element expected!", "MyAttr", attribute
				.getLocalName());

	}

	/**
	 * Test exposing a bug of parsing error when having a hash as last character
	 * in an attribute value.
	 *
	 */
    @Test
    public void testAttributeValueContainingHash()
	{
		Set<Object> nodes = getNodes(null, TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);

		Set<ASTAttribute> attributes = getNodesOfType(ASTAttribute.class, nodes);
		assertEquals("Three attributes expected!", 3, attributes.size());

		List<ASTAttribute> attrsList = new ArrayList<ASTAttribute>(attributes);
		Collections.sort(attrsList, new Comparator<ASTAttribute>() {
			public int compare(ASTAttribute arg0, ASTAttribute arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});

		ASTAttribute attr = attrsList.get(0);
		assertEquals("Correct attribute name expected!",
				"foo", attr.getName());
		assertEquals("Correct attribute value expected!",
				"CREATE", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage());

		attr = attrsList.get(1);
		assertEquals("Correct attribute name expected!",
				"href", attr.getName());
		assertEquals("Correct attribute value expected!",
				"#", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage());

		attr = attrsList.get(2);
		assertEquals("Correct attribute name expected!",
				"something", attr.getName());
		assertEquals("Correct attribute value expected!",
				"#yes#", attr.getFirstDescendantOfType(ASTAttributeValue.class).getImage());
	}

	/**
	 * Test correct parsing of CDATA.
	 */
    @Test
    public void testCData() {
		Set<ASTCData> cdataNodes = getNodes(ASTCData.class, TEST_CDATA);

		assertEquals("One CDATA node expected!", 1, cdataNodes.size());
		ASTCData cdata = (ASTCData) cdataNodes.iterator().next();
		assertEquals("Content incorrectly parsed!", " some <cdata> ]] ]> ", cdata
				.getImage());
	}

	/**
	 * Test parsing of Doctype declaration.
	 */
    @Test
    public void testDoctype() {
		Set<Object> nodes = getNodes(null, TEST_DOCTYPE);

		Set<ASTDoctypeDeclaration> docTypeDeclarations = getNodesOfType(ASTDoctypeDeclaration.class, nodes);
		assertEquals("One doctype declaration expected!", 1, docTypeDeclarations
				.size());
		ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations
				.iterator().next();
		assertEquals("Correct doctype-name expected!", "html", docTypeDecl.getName());

		Set<ASTDoctypeExternalId> externalIds = getNodesOfType(ASTDoctypeExternalId.class, nodes);
		assertEquals("One doctype external id expected!", 1, externalIds
				.size());
		ASTDoctypeExternalId externalId = (ASTDoctypeExternalId) externalIds
				.iterator().next();
		assertEquals("Correct external public id expected!", "-//W3C//DTD XHTML 1.1//EN",
				externalId.getPublicId());
		assertEquals("Correct external uri expected!", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd",
				externalId.getUri());

	}

	/**
	 * Test parsing of a XML comment.
	 *
	 */
    @Test
    public void testComment() {
		Set<ASTCommentTag> comments = getNodes(ASTCommentTag.class, TEST_COMMENT);
		assertEquals("One comment expected!", 1, comments.size());
		ASTCommentTag comment = (ASTCommentTag) comments.iterator().next();
		assertEquals("Correct comment content expected!", "comment", comment.getImage());
	}

    /**
     * Test parsing of HTML <script> element.
     */
    @Test
    public void testHtmlScript() {
		Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT);
		assertEquals("One script expected!", 1, scripts.size());
		ASTHtmlScript script = (ASTHtmlScript) scripts.iterator().next();
		assertEquals("Correct script content expected!", "Script!", script.getImage());
    }
    
    /**
     * Test parsing of HTML <script src="x"/> element. It might not be valid html
     * but it is likely to appear in .JSP files.
     */
    @Test
    public void testImportHtmlScript() {
		Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, TEST_IMPORT_JAVASCRIPT);
		assertEquals("One script expected!", 1, scripts.size());
		ASTHtmlScript script = (ASTHtmlScript) scripts.iterator().next();
		List<ASTAttributeValue> value = script.findDescendantsOfType(ASTAttributeValue.class);
		assertEquals("filename.js",value.get(0).getImage());
    }
    
    /**
     * Test parsing of HTML <script> element.
     */
    @Test
    public void testHtmlScriptWithAttribute() {
		Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, TEST_HTML_SCRIPT_WITH_ATTRIBUTE);
		assertEquals("One script expected!", 1, scripts.size());
		ASTHtmlScript script = (ASTHtmlScript) scripts.iterator().next();
		assertEquals("Correct script content expected!", "Script!", script.getImage());
		List<ASTAttributeValue> attrs = script.findDescendantsOfType(ASTAttributeValue.class);
		assertTrue("text/javascript".equals(attrs.get(0).getImage()));
    }
    
    /**
     * A complex script containing HTML comments, escapes, quotes, etc.
     */
    @Test
    public void testComplexHtmlScript(){
    	Set<ASTHtmlScript> script = getNodes(ASTHtmlScript.class, TEST_COMPLEX_SCRIPT);
    	assertEquals("One script expected!", 1, script.size());
    	ASTHtmlScript next = script.iterator().next();
		assertTrue(next.getImage().contains("<!--"));
    	Set<ASTCommentTag> comments = getNodes(ASTCommentTag.class, TEST_COMPLEX_SCRIPT);
    	assertEquals("One comment expected!", 1, comments.size());
    }
    
    /**
     * Test parsing of HTML <script> element.
     */
    @Test
    public void testInlineCss() {
		Set<ASTElement> scripts = getNodes(ASTElement.class, TEST_INLINE_STYLE);
		assertEquals("Three elements expected!", 3, scripts.size());
    }
    
    /**
     * Test parsing of HTML text within element.
     */
    @Test
    public void testTextInTag() {
		Set<ASTText> scripts = getNodes(ASTText.class, TEST_TEXT_IN_TAG);
		assertEquals("One text chunk expected!", 1, scripts.size());
		ASTText script = (ASTText) scripts.iterator().next();
		assertEquals("Correct content expected!", " some text ", script.getImage());
    }
    
    /**
     * Test parsing of HTML with no spaces between tags. Parser is likely 
     * in this scenario.
     */
    @Test
    public void noSpacesBetweenTags() {
		Set<ASTElement> scripts = getNodes(ASTElement.class, TEST_TAGS_NO_SPACE);
		assertEquals("Two tags expected!", 2, scripts.size());
		List<ASTElement> elmts = sortNodesByName(scripts);
		Iterator<ASTElement> iterator = elmts.iterator();
		ASTElement script = (ASTElement) iterator.next();
		assertEquals("Correct content expected!", "a", script.getName());
		script = (ASTElement) iterator.next();
		assertEquals("Correct content expected!", "b", script.getName());
    }
    
    /**
     * the $ sign might trick the parser into thinking an EL is next.
     * He should be able to treat it as plain text
     */
    @Test
    public void unclosedTagsWithDollar(){
    	Set<ASTText> scripts = getNodes(ASTText.class, TEST_TAGS_WITH_DOLLAR);
		assertEquals("Two text chunks expected!", 2, scripts.size());
		ASTText script = (ASTText) scripts.iterator().next();
		assertEquals("Correct content expected!", " $ ", script.getImage());
    }
    
    /**
     * Make sure EL expressions aren't treated as plain text when they
     * are around unclosed tags.
     */
    @Test
    public void unclosedTagsWithELWithin(){
    	Set<ASTElExpression> scripts = getNodes(ASTElExpression.class, TEST_TAGS_WITH_EL_WITHIN);
		assertEquals("Two EL expressions expected!", 2, scripts.size());
		List<ASTElExpression> exprs = sortByImage(scripts);
		Iterator<ASTElExpression> iterator = exprs.iterator();
		ASTElExpression script = (ASTElExpression) iterator.next();
		assertEquals("Correct content expected!", "expr1", script.getImage());
		script = (ASTElExpression) iterator.next();
		assertEquals("Correct content expected!", "expr2", script.getImage());
    }
    
    /**
     * Make sure mixed expressions don't confuse the parser
     */
    @Test
    public void mixedExpressions(){
    	Set<ASTJspExpression> exprs = getNodes(ASTJspExpression.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
    	assertEquals("One JSP expression expected!", 1, exprs.size());
    	assertEquals("Image of expression should be \"expr\"",
    			"expr",exprs.iterator().next().getImage());
    	Set<ASTElExpression> els = getNodes(ASTElExpression.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
		assertEquals("Two EL expression expected!", 2, els.size());
		assertEquals("Image of el should be \"expr\"",
				"expr",els.iterator().next().getImage());
		
		Set<ASTUnparsedText> unparsedtexts = getNodes(ASTUnparsedText.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
		List<ASTUnparsedText> sortedUnparsedTxts = sortByImage(unparsedtexts);
		assertEquals("Two unparsed texts expected!", 2, sortedUnparsedTxts.size());
		Iterator<ASTUnparsedText> iterator = sortedUnparsedTxts.iterator();
		assertEquals("Image of text should be \"\\${expr}\"",
				" \\${expr} ",iterator.next().getImage());
		assertEquals("Image of text should be \" aaa \"",
				" aaa ",iterator.next().getImage());
		
		// ASTText should contain the text between two tags.
		Set<ASTText> texts = getNodes(ASTText.class, TEST_TAGS_WITH_MIXED_EXPRESSIONS);
		List<ASTText> sortedTxts = sortByImage(texts);
		assertEquals("Two regular texts expected!", 2, sortedTxts.size());
		Iterator<ASTText> iterator2 = sortedTxts.iterator();
		assertEquals("Image of text should be \"\\${expr}\"",
				" \\${expr} ",iterator2.next().getImage());
		assertEquals("Image of text should be all text between two nodes" +
				" \"  aaa ${expr}#{expr} \"",
				" aaa ${expr}#{expr}",iterator2.next().getImage());
    }
    
    
    /**
     * Make sure JSP expressions are properly detected when they are next 
     * to unclosed tags.
     */
    @Test
    public void unclosedTagsWithJspExpressionWithin(){
    	Set<ASTJspExpression> scripts = getNodes(ASTJspExpression.class, TEST_TAGS_WITH_EXPRESSION_WITHIN);
		assertEquals("Two JSP expressions expected!", 2, scripts.size());
		ASTJspExpression script = (ASTJspExpression) scripts.iterator().next();
		assertEquals("Correct content expected!", "expr", script.getImage());
    }
    
    
    /**
     * A dangling unopened ( just </closed> )  tag should not influence the parsing. 
     */
    @Test
    @Ignore // sadly the number of 
    // <opening> tags has to be >= then the number of </closing> tags
    public void textBetweenUnopenedTag(){
    	Set<ASTText> scripts = getNodes(ASTText.class, TEST_TEXT_WITH_UNOPENED_TAG);
    	assertEquals("Two text chunks expected!", 2, scripts.size());
		ASTText script = (ASTText) scripts.iterator().next();
		assertEquals("Correct content expected!", "$", script.getImage());
    }
    
    /**
     * Parser should be able to handle documents which start or end with unparsed text 
     */
    @Test
    @Ignore// sadly the number of 
    // <opening> tags has to be >= then the number of </closing> tags
    public void textMultipleClosingTags(){
    	Set<ASTText> scripts = getNodes(ASTText.class, TEST_MULTIPLE_CLOSING_TAGS);
    	assertEquals("Four text chunks expected!", 4, scripts.size());
		ASTText script = (ASTText) scripts.iterator().next();
		assertEquals("Correct content expected!", " some text ", script.getImage());
    }
    
    
    /**
     * Test parsing of HTML <script> element.
     */
    @Test 
    public void textAfterOpenAndClosedTag() {
		Set<ASTElement> nodes = getNodes(ASTElement.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
		assertEquals("Two elements expected!", 2, nodes.size());
		List<ASTElement> elmts = sortNodesByName(nodes);
		assertEquals("First element should be a","a",elmts.get(0).getName());
		assertFalse("first element should be closed",elmts.get(0).isUnclosed());
		assertEquals("Second element should be b","b",elmts.get(1).getName());
		assertTrue("Second element should not be closed",elmts.get(1).isUnclosed());
		
		Set<ASTText> text = getNodes(ASTText.class, TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
		assertEquals("Two text chunks expected!", 2, text.size());
    }
    
    @Test
    public void  quoteEL(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_QUOTE_EL);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "${something}", attr.getImage());
    }
    
    @Test
    public void quoteExpression(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_QUOTE_EXPRESSION);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<%=something%>", attr.getImage());
    }
    
    
    @Test
    @Ignore // tags contain quotes and break attribute parsing 
    public void quoteTagInAttribute(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_QUOTE_TAG_IN_ATTR);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", 
				"<bean:write name=\"x\" property=\"z\">", attr.getImage());
    }
    /**
     * smoke test for a non-quoted attribute value
     */
    @Test
    public void noQuoteAttrValue() {
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "yes|", attr.getImage());
    }
    
    
    /**
     * tests whether JSP el is properly detected as attribute value
     */
    @Test
    public void noQuoteAttrWithJspEL(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EL);
		assertEquals("two attributes expected!", 2, attributes.size());
		Iterator<ASTAttributeValue> iterator = attributes.iterator();
		ASTAttributeValue attr2 =  iterator.next();
		if ("url".equals(attr2.getImage())){
			// we have to employ this nasty work-around 
			// in order to ensure that we check the proper attribute
			attr2 = iterator.next();
		}
		assertEquals("Expected to detect proper value for EL in attribute!", "${something}", attr2.getImage());
    }
    
    /**
     * tests whether parse correctly detects presence of JSP expression <%= %>
     * within an non-quoted attribute value
     */
    @Test
    public void noQuoteAttrWithJspExpression(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_EXPRESSION);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<%=something%>", attr.getImage());
    }
    
    /**
     * tests whether parse correctly interprets empty non quote attribute
     */
    @Test
    public void noQuoteAttrEmpty(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_EMPTY_ATTR);
		assertEquals("two attributes expected!", 2, attributes.size());
		Iterator<ASTAttributeValue> iterator = attributes.iterator();
		ASTAttributeValue attr =  iterator.next();
		if ("http://someHost:/some_URL".equals(attr.getImage())){
			// we have to employ this nasty work-around 
			// in order to ensure that we check the proper attribute
			attr = iterator.next();
		}
		assertEquals("Expected to detect proper value for attribute!", "", attr.getImage());
    }
    
    /**
     * tests whether parse correctly interprets an cr lf instead of an attribute
     */
    @Test
    public void noQuoteAttrCrLf(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_CR_LF_ATTR);
		assertEquals("One attribute expected!", 2, attributes.size());
		Iterator<ASTAttributeValue> iterator = attributes.iterator();
		ASTAttributeValue attr =  iterator.next();
		if ("http://someHost:/some_URL".equals(attr.getImage())){
			// we have to employ this nasty work-around 
			// in order to ensure that we check the proper attribute
			attr = iterator.next();
		}
		assertEquals("Expected to detect proper value for attribute!", "\r\n", attr.getImage());
		
    }
   
    /**
     * tests whether parse correctly interprets an tab instead of an attribute
     */
    @Test
    public void noQuoteAttrTab(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAB_ATTR);
		assertEquals("One attribute expected!", 1, attributes.size());
		Iterator<ASTAttributeValue> iterator = attributes.iterator();
		ASTAttributeValue attr =  iterator.next();
		assertEquals("Expected to detect proper value for attribute!", "\t", attr.getImage());
		
    }
    
    /**
     * tests whether parse does not fail in the presence of unclosed JSP expression <%= 
     * within an non-quoted attribute value
     */
    @Test
    public void noQuoteAttrWithMalformedJspExpression(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_MALFORMED_EXPR);
		assertEquals("One attribute expected!",1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<%=something", attr.getImage());
    }
    
    /**
     * test a no quote attribute value which contains a scriptlet <% %> 
     * within its value
     */
    @Test
    @Ignore// nice test for future development
    public void noQuoteAttrWithScriptletInValue(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_ATTR_WITH_SCRIPTLET);
		assertEquals("One attribute expected!", 1, attributes.size());
		
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<% String a = \"1\";%>", attr.getImage());
    }
    
    /**
     * test a no quote attribute value can contain a tag (e.g. attr=<bean:write property="value" />)
     * 
     */
    @Test
    @Ignore// nice test for future development
    public void noQuoteAttrWithBeanWriteTagAsValue(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAG_IN_ATTR);
		assertEquals("One attribute expected!", 1, attributes.size());
		
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<% String a = \"1\";%>", attr.getImage());
    }
    
    /**
     * test a quote attribute value can contain a tag (e.g. attr="<bean:write property="value" />" )
     * Not sure if it's legal JSP code but most JSP engine accept and properly treat 
     * this value at runtime
     */
    @Test
    @Ignore// nice test for future development
    public void quoteAttrWithBeanWriteTagAsValue(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, TEST_NO_QUOTE_TAG_IN_ATTR);
		assertEquals("One attribute expected!", 1, attributes.size());
		
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "<% String a = \"1\";%>", attr.getImage());
    }
    
    /**
     * test a no quote attribute value which contains the EL dollar sign $
     * within its value
     */
    @Test
    @Ignore// nice test for future development
    public void noQuoteAttrWithDollarSignInValue(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, 
    			TEST_NO_QUOTE_ATTR_WITH_DOLLAR);
		assertEquals("One attribute expected!", 2, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "${something", attr.getImage());
    }
    
    /**
     * test a no quote attribute value which contains the EL sharp sign #
     * within its value
     */
    @Test
    @Ignore// nice test for future development
    public void noQuoteAttrWithSharpSymbolInValue(){
    	Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class,
    			TEST_NO_QUOTE_ATTR_WITH_HASH);
		assertEquals("One attribute expected!", 1, attributes.size());
		ASTAttributeValue attr =  attributes.iterator().next();
		assertEquals("Expected to detect proper value for attribute!", "#{something", attr.getImage());
    }
    
    @Test
    public void unclosedTag(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, TEST_UNCLOSED_SIMPLE);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("2 tags expected",2,elements.size());
		assertEquals("First element should be sorted tag:if",
				"tag:if",sortedElmnts.get(0).getName());
		assertEquals("Second element should be tag:someTag",
				"tag:someTag",sortedElmnts.get(1).getName());
		
		assertTrue(sortedElmnts.get(0).isEmpty());
		assertTrue(sortedElmnts.get(0).isUnclosed());
		assertFalse(sortedElmnts.get(1).isEmpty());
		assertFalse(sortedElmnts.get(1).isUnclosed());
    }
    
    @Test
    public void unclosedTagAndNoQuotesForAttribute(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, TEST_UNCLOSED_NO_QUOTE_ATTR);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("2 tags expected",2,elements.size());
		assertEquals("First element should be sorted tag:if",
				"tag:if",sortedElmnts.get(0).getName());
		assertEquals("Second element should be tag:someTag",
				"tag:someTag",sortedElmnts.get(1).getName());
		
		assertTrue(sortedElmnts.get(0).isEmpty());
		assertTrue(sortedElmnts.get(0).isUnclosed());
		assertFalse(sortedElmnts.get(1).isEmpty());
		assertFalse(sortedElmnts.get(1).isUnclosed());
    }
    
    @Test
    public void unclosedTagMultipleLevels(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, TEST_UNCLOSED_MULTIPLE_LEVELS);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("3 tags expected",3,elements.size());
		assertEquals("First element should be sorted tag:someTag",
				"tag:someTag",sortedElmnts.get(0).getName());
		assertEquals("Second element should be tag:someTag",
				"tag:someTag",sortedElmnts.get(1).getName());
		assertEquals("Third element should be tag:x",
				"tag:x",sortedElmnts.get(2).getName());
		
		assertFalse(sortedElmnts.get(0).isEmpty());
		assertFalse(sortedElmnts.get(0).isUnclosed());
		
		assertTrue(sortedElmnts.get(1).isEmpty());
		assertTrue(sortedElmnts.get(1).isUnclosed());
		
		assertFalse(sortedElmnts.get(2).isEmpty());
		assertFalse(sortedElmnts.get(2).isUnclosed());
    }
    
    /**
     * <html> <a1> <a2/> <b/> </a1> </html>
     */
    @Test
    public void nestedEmptyTags(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, TEST_MULTIPLE_EMPTY_TAGS);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("4 tags expected",4,elements.size());
		assertEquals("First element should a1",
				"a1",sortedElmnts.get(0).getName());
		assertEquals("Second element should be a2",
				"a2",sortedElmnts.get(1).getName());
		assertEquals("Third element should be b",
				"b",sortedElmnts.get(2).getName());
		assertEquals("Third element should be html",
				"html",sortedElmnts.get(3).getName());
		
		
		//  a1 
		assertFalse(sortedElmnts.get(0).isEmpty());
		assertFalse(sortedElmnts.get(0).isUnclosed());
		
		//  a2
		assertTrue(sortedElmnts.get(1).isEmpty());
		assertFalse(sortedElmnts.get(1).isUnclosed());
		
		// b
		assertTrue(sortedElmnts.get(2).isEmpty());
		assertFalse(sortedElmnts.get(2).isUnclosed());
		
		//html
		assertFalse(sortedElmnts.get(3).isEmpty());
		assertFalse(sortedElmnts.get(3).isUnclosed());
		
    }
    
    /**
     * <html> <a1> <a2> <a3> </a2> </a1> <b/> <a4/> </html>
     */
    @Test
    public void nestedMultipleTags(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, TEST_MULTIPLE_NESTED_TAGS);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("4 tags expected",6,elements.size());
		assertEquals("First element should a1",
				"a1",sortedElmnts.get(0).getName());
		assertEquals("Second element should be a2",
				"a2",sortedElmnts.get(1).getName());
		assertEquals("Third element should be a3",
				"a3",sortedElmnts.get(2).getName());
		assertEquals("Forth element should be a4",
				"a4",sortedElmnts.get(3).getName());
		assertEquals("Fifth element should be b",
				"b",sortedElmnts.get(4).getName());
		assertEquals("Sixth element should be html",
				"html",sortedElmnts.get(5).getName());
		
		
		//  a1 not empty and closed
		assertFalse(sortedElmnts.get(0).isEmpty());
		assertFalse(sortedElmnts.get(0).isUnclosed());
		
		//  a2  not empty and closed
		assertFalse(sortedElmnts.get(1).isEmpty());
		assertFalse(sortedElmnts.get(1).isUnclosed());
		
		//  a3 empty and not closed
		assertTrue(sortedElmnts.get(2).isEmpty());
		assertTrue(sortedElmnts.get(2).isUnclosed());
		
		//  a4 empty but closed
		assertTrue(sortedElmnts.get(3).isEmpty());
		assertFalse(sortedElmnts.get(3).isUnclosed());
		
		//  b empty but closed
		assertTrue(sortedElmnts.get(4).isEmpty());
		assertFalse(sortedElmnts.get(4).isUnclosed());
			
		//html not empty and closed
		assertFalse(sortedElmnts.get(5).isEmpty());
		assertFalse(sortedElmnts.get(5).isUnclosed());
		
    }
    
    /**
     * will test <x> <a> <b> <b> </x> </a> </x> . 
     * Here x is the first tag to be closed thus rendering the next close of a (</a>)
     * to be disregarded. 
     */
    @Test
    public void unclosedParentTagClosedBeforeChild(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, 
    			TEST_UNCLOSED_END_AFTER_PARENT_CLOSE);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("4 tags expected",4,elements.size());
		assertEquals("First element should be 'a'",
				"a",sortedElmnts.get(0).getName());
		assertEquals("Second element should be b",
				"b",sortedElmnts.get(1).getName());
		assertEquals("Third element should be b",
				"b",sortedElmnts.get(2).getName());
		assertEquals("Forth element should be x",
				"x",sortedElmnts.get(3).getName());
		
		//a
		assertTrue(sortedElmnts.get(0).isEmpty());
		assertTrue(sortedElmnts.get(0).isUnclosed());
		
		//b
		assertTrue(sortedElmnts.get(1).isEmpty());
		assertTrue(sortedElmnts.get(1).isUnclosed());
		
		//b
		assertTrue(sortedElmnts.get(2).isEmpty());
		assertTrue(sortedElmnts.get(2).isUnclosed());
		
		//x
		assertFalse(sortedElmnts.get(3).isEmpty());
		assertFalse(sortedElmnts.get(3).isUnclosed());
    }
    
    
    /**
     * <x> <a> <b> <b> </z> </a> </x>
     * An unmatched closing of 'z' appears randomly in the document. This
     * should be disregarded and structure of children and parents should not be influenced.
     * in other words </a> should close the first <a> tag , </x> should close the first
     *  <x>, etc.
     */
    @Test
    public void unmatchedTagDoesNotInfluenceStructure(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, 
    			TEST_UNCLOSED_UNMATCHED_CLOSING_TAG);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("4 tags expected",4,elements.size());
		assertEquals("First element should be 'a'",
				"a",sortedElmnts.get(0).getName());
		assertEquals("Second element should be b",
				"b",sortedElmnts.get(1).getName());
		assertEquals("Third element should be b",
				"b",sortedElmnts.get(2).getName());
		assertEquals("Forth element should be x",
				"x",sortedElmnts.get(3).getName());
		
		//a is not empty and closed
		assertFalse(sortedElmnts.get(0).isEmpty());
		assertFalse(sortedElmnts.get(0).isUnclosed());
		
		//b empty and unclosed
		assertTrue(sortedElmnts.get(1).isEmpty());
		assertTrue(sortedElmnts.get(1).isUnclosed());
		
		//b empty and unclosed
		assertTrue(sortedElmnts.get(2).isEmpty());
		assertTrue(sortedElmnts.get(2).isUnclosed());
		
		//x not empty and closed
		assertFalse(sortedElmnts.get(3).isEmpty());
		assertFalse(sortedElmnts.get(3).isUnclosed());
    }
    
    /**
     * <a> <x> <a> <b> <b> </z> </a> </x>
     * An unmatched closing of 'z' appears randomly in the document. This
     * should be disregarded and structure of children and parents should not be influenced.
     * Also un unclosed <a> tag appears at the start of the document
     */
    @Test
    public void unclosedStartTagWithUnmatchedCloseOfDifferentTag(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, 
    			TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("5 tags expected",5,elements.size());
		assertEquals("First element should be 'a'",
				"a",sortedElmnts.get(0).getName());
		assertEquals("Second element should be a",
				"a",sortedElmnts.get(1).getName());
		assertEquals("Third element should be b",
				"b",sortedElmnts.get(2).getName());
		assertEquals("Forth element should be b",
				"b",sortedElmnts.get(3).getName());
		assertEquals("Fifth element should be x",
				"x",sortedElmnts.get(4).getName());
		
		//first a is empty and unclosed
		assertTrue(sortedElmnts.get(0).isEmpty());
		assertTrue(sortedElmnts.get(0).isUnclosed());
		
		//second a not empty and closed
		assertFalse(sortedElmnts.get(1).isEmpty());
		assertFalse(sortedElmnts.get(1).isUnclosed());
		
		//b empty and unclosed
		assertTrue(sortedElmnts.get(2).isEmpty());
		assertTrue(sortedElmnts.get(2).isUnclosed());
		
		//b empty and unclosed
		assertTrue(sortedElmnts.get(3).isEmpty());
		assertTrue(sortedElmnts.get(3).isUnclosed());
				
		//x not empty and closed
		assertFalse(sortedElmnts.get(4).isEmpty());
		assertFalse(sortedElmnts.get(4).isUnclosed());
    }
    
    /**
     * {@link #TEST_UNCLOSED_END_OF_DOC}
     * <tag:x> <tag:y> 
     * Tests whether parser breaks on no closed tags at all
     */
    //This is yet to be improved. If a closing tag does not 
    // exist no tags will be marked as empty :(
    @Ignore
    @Test
    public void unclosedEndOfDoc(){
    	Set<ASTElement> elements = getNodes(ASTElement.class, 
    			TEST_UNCLOSED_END_OF_DOC);
    	List<ASTElement> sortedElmnts = sortNodesByName(elements);
		assertEquals("2 tags expected",2,elements.size());
		assertEquals("First element should be 'tag:x'",
				"tag:x",sortedElmnts.get(0).getName());
		assertEquals("Second element should be tag:y",
				"tag:y",sortedElmnts.get(1).getName());
		
		//b
		//assertTrue(sortedElmnts.get(0).isEmpty());
		assertTrue(sortedElmnts.get(0).isUnclosed());
				
		//b
		assertTrue(sortedElmnts.get(1).isEmpty());
		assertTrue(sortedElmnts.get(1).isUnclosed());
    }
    
    /**
     * will sort the AST element in list in alphabetical order and if tag name 
     * is the same it will sort against o1.getBeginColumn() +""+ o1.getBeginLine().
     * so first criteria is the name, then the second is the column +""+line string.
     * @param elements
     * @return
     */
    private List<ASTElement> sortNodesByName(Set<ASTElement> elements){
    	List<ASTElement> list = new ArrayList<ASTElement>();
    	list.addAll(elements);
    	Collections.sort(list, new Comparator<ASTElement>() {
    		public int compare(ASTElement o1, ASTElement o2) {
    			if (o1.getName() == null)
    				return Integer.MIN_VALUE;
    			if (o2.getName() == null)
    				return Integer.MAX_VALUE;
    			if (o1.getName().equals(o2.getName())){
    				String o1Value = o1.getBeginColumn() +""+ o1.getBeginLine();
    				String o2Value = o2.getBeginColumn() +""+ o2.getBeginLine();
    				return o1Value.compareTo(o2Value);
    			}
    			return o1.getName().compareTo(o2.getName());
    		}
		});
    	return list;
    }
    
    /**
     * will sort the AST node by the image name.
     * @param elements
     * @return
     */
    private <T extends Node> List<T> sortByImage(Set<T> elements){
    	List<T> list = new ArrayList<T>();
    	list.addAll(elements);
    	Collections.sort(list, new Comparator<Node>() {
    		public int compare(Node o1, Node o2) {
    			if (o1.getImage() == null)
    				return Integer.MIN_VALUE;
    			if (o2.getImage() == null)
    				return Integer.MAX_VALUE;
    			if (o1.getImage().equals(o2.getImage())){
    				String o1Value = o1.getBeginColumn() +""+ o1.getBeginLine();
    				String o2Value = o2.getBeginColumn() +""+ o2.getBeginLine();
    				return o1Value.compareTo(o2Value);
    			}
    			return o1.getImage().compareTo(o2.getImage());
    		}
		});
    	return list;
    }
    

	private static final String TEST_SIMPLEST_HTML = "<html/>";

	private static final String TEST_ELEMENT_AND_NAMESPACE = "<h:html MyNsPrefix:MyAttr='MyValue'/>";

	private static final String TEST_CDATA = "<html><![CDATA[ some <cdata> ]] ]> ]]></html>";

	private static final String TEST_DOCTYPE = "<?xml version=\"1.0\" standalone='yes'?>\n"
			+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" "
			+ "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"
			+ "<greeting>Hello, world!</greeting>";

	private static final String TEST_COMMENT = "<html><!-- comment --></html>";

	private static final String TEST_ATTRIBUTE_VALUE_CONTAINING_HASH =
		"<tag:if something=\"#yes#\" foo=\"CREATE\">  <a href=\"#\">foo</a> </tag:if>";

	private static final String TEST_HTML_SCRIPT =
			"<html><head><script>Script!</script></head></html>";

	private static final String TEST_IMPORT_JAVASCRIPT =
			"<html><head><script src=\"filename.js\" type=\"text/javascript\"/></head></html>";

	private static final String TEST_HTML_SCRIPT_WITH_ATTRIBUTE =
			"<html><head><script type=\"text/javascript\">Script!</script></head></html>";
	
	private static final String TEST_COMPLEX_SCRIPT = 
			"<HTML><BODY><!--Java Script-->" +
			"<SCRIPT language='JavaScript' type='text/javascript'>" +
			"<!--function calcDays(){" +
			" date1 = date1.split(\"-\");  date2 = date2.split(\"-\");" +
			" var sDate = new Date(date1[0]+\"/\"+date1[1]+\"/\"+date1[2]);" +
			" var eDate = new Date(date2[0]+\"/\"+date2[1]+\"/\"+date2[2]);" +
			" onload=calcDays;//-->" +
			"</SCRIPT></BODY></HTML>;";
	
	private static final String TEST_INLINE_STYLE =
			"<html><head><style> div { color:red; } </style></head></html>";
	
	private static final String TEST_TEXT_IN_TAG=
			"<a> some text </a>";
	
	private static final String TEST_TAGS_NO_SPACE =
			"<a><b></a>";
	
	private static final String TEST_TAGS_WITH_DOLLAR =
			"<a> $ <b> $ </a>";
	
	private static final String TEST_TAGS_WITH_EL_WITHIN =
			"<a>#{expr1}<b>${expr2}</a>";
	
	private static final String TEST_TAGS_WITH_MIXED_EXPRESSIONS =
			"<a> aaa ${expr} #{expr} <%=expr%> <b> \\${expr} </a>";
	
	private static final String TEST_TAGS_WITH_EXPRESSION_WITHIN =
			"<a> <%=expr%> <b> <%=expr%> </a>";
	
	private static final String TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG =
			"<a> some text <b> some text </a>";
	
	private static final String TEST_TEXT_WITH_UNOPENED_TAG =
			"<a> some text </b> some text </a>";
	
	private static final String TEST_MULTIPLE_CLOSING_TAGS =
			"<a> some text </b> </b> </b> some text </a>";
	
	private static final String TEST_QUOTE_EL = 
			"<tag:if something=\"${something}\" > </tag:if>";
	
	private static final String TEST_QUOTE_EXPRESSION = 
			"<tag:if something=\"<%=something%>\" >  </tag:if>";
	
	private static final String TEST_QUOTE_TAG_IN_ATTR =
			"<tag:if something=\"<bean:write name=\"x\" property=\"z\">\" >  " +
			"<a href=http://someHost:/some_URL >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_ATTR = 
"<tag:if something=yes| > </tag:if>";
	
	private static final String TEST_NO_QUOTE_EMPTY_ATTR = 
			"<tag:if something= >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_TAG_IN_ATTR =
			"<tag:if something=<bean:write name=\"x\" property=\"z\"> >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_CR_LF_ATTR = 
			"<tag:if something=\r\n >  <a href=http://someHost:/some_URL >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_TAB_ATTR = 
			"<tag:if something=\t >   </tag:if>";
	 
	private static final String TEST_NO_QUOTE_ATTR_WITH_EL = 
			"<tag:if something=${something} >  <a href=url >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_ATTR_WITH_EXPRESSION = 
			"<tag:if something=<%=something%> >  </tag:if>";
	
	/**
	 * same as {@link #TEST_NO_QUOTE_ATTR_WITH_EXPRESSION} only expression is not 
	 * properly closed
	 */
	private static final String TEST_NO_QUOTE_ATTR_WITH_MALFORMED_EXPR = 
			"<tag:if something=<%=something >  </tag:if>";
	
	private static final String TEST_NO_QUOTE_ATTR_WITH_SCRIPTLET = 
			"<tag:if something=<% String a = \"1\";%>x >  </tag:if>";
	
	
	private static final String TEST_NO_QUOTE_ATTR_WITH_DOLLAR =
			"<tag:if something=${something >  <a href=${ >foo</a> </tag:if>";
	
	private static final String TEST_NO_QUOTE_ATTR_WITH_HASH =
			"<tag:if something=#{something >  <a href=#{url} >foo</a> </tag:if>";
	
	private static final String TEST_UNCLOSED_SIMPLE =
			"<tag:someTag> <tag:if someting=\"x\" > </tag:someTag>";
	
	/**
	 * someTag is closed just once
	 */
	private static final String TEST_UNCLOSED_MULTIPLE_LEVELS = 
			"<tag:x> <tag:someTag> <tag:someTag someting=\"x\" > </tag:someTag> </tag:x>";
	
	/**
	 * nested empty tags
	 */
	private static final String TEST_MULTIPLE_EMPTY_TAGS = 
			"<html> <a1> <a2/> <b/> </a1> </html>"; 
	
	
	/**
	 * multiple nested tags with some tags unclosed
	 */
	private static final String TEST_MULTIPLE_NESTED_TAGS = 
			"<html> <a1> <a2> <a3> </a2> </a1> <b/> <a4/> </html>"; 
	
	/**
	 * </x> will close before </a>, thus leaving <a> to remain unclosed 
	 */
	private static final String TEST_UNCLOSED_END_AFTER_PARENT_CLOSE = 
			"<x> <a> <b> <b> </x> </a> aa </x> bb </x>";
	
	/**
	 * </z> is just a dangling closing tag not matching any parent. The parser should
	 * disregard it
	 */
	private static final String TEST_UNCLOSED_UNMATCHED_CLOSING_TAG = 
			"<x> <a> <b> <b> </z> </a> </x>";
	
	/**
	 * First <a> tag does not close. The first closing of </a> will match the 
	 * second opening of a. Another rogue </z> is there for testing compliance
	 */
	private static final String TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE =
			"<a> <x> <a> <b> <b> </z> </a> </x>";
	
	private static final String TEST_UNCLOSED_END_OF_DOC = 
			"<tag:x> <tag:y>";
	
	private static final String TEST_UNCLOSED_NO_QUOTE_ATTR =
			"<tag:someTag> <tag:if someting=x > </tag:someTag>";
	
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JspDocStyleTest.class);
    }
}
