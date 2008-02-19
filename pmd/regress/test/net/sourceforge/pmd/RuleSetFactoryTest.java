/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
 (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
 by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleReference;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;
import net.sourceforge.pmd.util.ResourceLoader;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class RuleSetFactoryTest {

	private boolean isJdk14;

	@Before
	public void setUp() {
		try {
			Class.forName("java.lang.Appendable");
		} catch (Throwable t) {
			isJdk14 = true;
		}
	}

	@Test
	public void testRuleSetFileName() throws RuleSetNotFoundException {
		RuleSet rs = loadRuleSet(EMPTY_RULESET);
		assertNull("RuleSet file name not expected", rs.getFileName());

		RuleSetFactory rsf = new RuleSetFactory();
		rs = rsf.createSingleRuleSet("rulesets/basic.xml");
		assertEquals("wrong RuleSet file name", rs.getFileName(), "rulesets/basic.xml");
	}

	@Test
	public void testNoRuleSetFileName() {
		RuleSet rs = loadRuleSet(EMPTY_RULESET);
		assertNull("RuleSet file name not expected", rs.getFileName());
	}

	@Test
	public void testRefs() throws Throwable {
		InputStream in = ResourceLoader.loadResourceAsStream("rulesets/favorites.xml", this.getClass().getClassLoader());
		if (in == null) {
			throw new RuleSetNotFoundException(
					"Can't find resource   Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
							+ System.getProperty("java.class.path"));
		}
		RuleSetFactory rsf = new RuleSetFactory();
		RuleSet rs = rsf.createSingleRuleSet("rulesets/favorites.xml");
		assertNotNull(rs.getRuleByName("WhileLoopsMustUseBraces"));
	}

	@Test(expected = RuleSetNotFoundException.class)
	public void testRuleSetNotFound() throws RuleSetNotFoundException {
		RuleSetFactory rsf = new RuleSetFactory();
		rsf.createSingleRuleSet("fooooo");
	}

	@Test
	public void testCreateEmptyRuleSet() {
		RuleSet rs = loadRuleSet(EMPTY_RULESET);
		assertEquals("test", rs.getName());
		assertEquals(0, rs.size());
	}

	@Test
	public void testSingleRule() {
		RuleSet rs = loadRuleSet(SINGLE_RULE);
		assertEquals(1, rs.size());
		Rule r = rs.getRules().iterator().next();
		assertEquals("MockRuleName", r.getName());
		assertEquals("net.sourceforge.pmd.MockRule", r.getRuleClass());
		assertEquals("avoid the mock rule", r.getMessage());
	}

	@Test
	public void testMultipleRules() {
		RuleSet rs = loadRuleSet(MULTIPLE_RULES);
		assertEquals(2, rs.size());
		Set<String> expected = new HashSet<String>();
		expected.add("MockRuleName1");
		expected.add("MockRuleName2");
		for (Iterator<Rule> i = rs.getRules().iterator(); i.hasNext();) {
			assertTrue(expected.contains(i.next().getName()));
		}
	}

	@Test
	public void testSingleRuleWithPriority() {
		assertEquals(3, loadFirstRule(PRIORITY).getPriority());
	}

	@Test
	public void testProps() {
		Rule r = loadFirstRule(PROPERTIES);
		assertTrue(r.hasProperty("foo"));
		assertEquals("bar", r.getStringProperty("foo"));
		assertEquals(2, r.getIntProperty("fooint"));
		assertTrue(r.hasProperty("fooBoolean"));
		assertTrue(r.getBooleanProperty("fooBoolean"));
		assertTrue(r.hasProperty("fooDouble"));
		assertEquals(1.0, r.getDoubleProperty("fooDouble"), 0.05);
		assertTrue(!r.hasProperty("BuggleFish"));
		assertTrue(r.getDescription().indexOf("testdesc2") != -1);
	}

	@Test
	public void testXPathPluginnameProperty() {
		Rule r = loadFirstRule(XPATH_PLUGINNAME);
		assertTrue(r.hasProperty("pluginname"));
	}

	@Test
	public void testXPath() {
		Rule r = loadFirstRule(XPATH);
		assertTrue(r.hasProperty("xpath"));
		assertTrue(r.getStringProperty("xpath").indexOf(" //Block ") != -1);
	}

	@Test
	public void testFacadesOffByDefault() {
		Rule r = loadFirstRule(XPATH);
		assertFalse(r.usesDFA());
	}

	@Test
	public void testDFAFlag() {
		assertTrue(loadFirstRule(DFA).usesDFA());
	}

	@Test
	public void testExternalReferenceOverride() {
		Rule r = loadFirstRule(REF_OVERRIDE);
		assertEquals("TestNameOverride", r.getName());
		assertEquals("Test message override", r.getMessage());
		assertEquals("Test description override", r.getDescription());
		assertEquals("Test example override", r.getExample());
		assertEquals("Test that both example are stored", 2, r.getExamples().size());
		assertEquals(3, r.getPriority());
		assertTrue(r.hasProperty("test2"));
		assertEquals("override2", r.getStringProperty("test2"));
		assertTrue(r.hasProperty("test3"));
		assertEquals("override3", r.getStringProperty("test3"));
		assertTrue(r.hasProperty("test4"));
		assertEquals("new property", r.getStringProperty("test4"));
	}

	@Test
	public void testOverrideMessage() {
		Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME);
		assertEquals("TestMessageOverride", r.getMessage());
	}

	@Test
	public void testOverrideMessageOneElem() {
		Rule r = loadFirstRule(REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM);
		assertEquals("TestMessageOverride", r.getMessage());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExternalRef() throws IllegalArgumentException {
		loadFirstRule(REF_MISPELLED_XREF);
	}

	@Test
	public void testSetPriority() {
		RuleSetFactory rsf = new RuleSetFactory();
		rsf.setMinimumPriority(2);
		assertEquals(0, rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE.getBytes())).size());
		rsf.setMinimumPriority(4);
		assertEquals(1, rsf.createRuleSet(new ByteArrayInputStream(SINGLE_RULE.getBytes())).size());
	}

	@Test
	public void testIncludeExcludePatterns() {
		RuleSet ruleSet = loadRuleSet(INCLUDE_EXCLUDE_RULESET);

		assertNotNull("Include patterns", ruleSet.getIncludePatterns());
		assertEquals("Include patterns size", 2, ruleSet.getIncludePatterns().size());
		assertEquals("Include pattern #1", "include1", ruleSet.getIncludePatterns().get(0));
		assertEquals("Include pattern #2", "include2", ruleSet.getIncludePatterns().get(1));

		assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
		assertEquals("Exclude patterns size", 3, ruleSet.getExcludePatterns().size());
		assertEquals("Exclude pattern #1", "exclude1", ruleSet.getExcludePatterns().get(0));
		assertEquals("Exclude pattern #2", "exclude2", ruleSet.getExcludePatterns().get(1));
		assertEquals("Exclude pattern #3", "exclude3", ruleSet.getExcludePatterns().get(2));
	}

	@Test
	public void testAllPMDBuiltInRulesNeedSince() throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		boolean allValid = true;
		List<String> ruleSetFileNames = getRuleSetFileNames();
		for (String fileName : ruleSetFileNames) {
			RuleSet ruleSet = loadRuleSetByFileName(fileName);
			for (Rule rule : ruleSet.getRules()) {
				if (rule.getSince() == null) {
					allValid = false;
					System.err.println("Rule " + fileName + "/" + rule.getName() + " is missing 'since' attribute");
				}
			}
		}
		assertTrue("All built-in PMD rules need 'since' attribute.", allValid);
	}

	// FUTURE Enable this test when we're ready to rename rules
	/*
	@Test
	public void testAllPMDBuiltInRulesShouldEndWithRule() throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		boolean allValid = true;
		List<String> ruleSetFileNames = getRuleSetFileNames();
		for (String fileName : ruleSetFileNames) {
			RuleSet ruleSet = loadRuleSetByFileName(fileName);
			for (Rule rule : ruleSet.getRules()) {
				if (!rule.getRuleClass().endsWith("Rule")) {
					allValid = false;
					System.err.println("Rule " + fileName + "/" + rule.getName()
							+ " does not have 'ruleClass' that ends with 'Rule': " + rule.getRuleClass());
				}
			}
		}
		assertTrue("All built-in PMD rule classes should end with 'Rule'.", allValid);
	}
	*/

	@Test
	public void testXmlSchema() throws IOException, RuleSetNotFoundException, ParserConfigurationException,
			SAXException {
		if (isJdk14) {
			// ignore failure with jdk 1.4
			return;
		}

		boolean allValid = true;
		List<String> ruleSetFileNames = getRuleSetFileNames();
		for (String fileName : ruleSetFileNames) {
			boolean valid = validateAgainstSchema(fileName);
			allValid = allValid && valid;
		}
		assertTrue("All XML must parse without producing validation messages.", allValid);
	}

	@Test
	public void testDtd() throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {
		boolean allValid = true;
		List<String> ruleSetFileNames = getRuleSetFileNames();
		for (String fileName : ruleSetFileNames) {
			boolean valid = validateAgainstDtd(fileName);
			allValid = allValid && valid;
		}
		assertTrue("All XML must parse without producing validation messages.", allValid);
	}

	@Test
	public void testReadWriteRoundTrip() throws IOException, RuleSetNotFoundException, ParserConfigurationException,
			SAXException {
		boolean allValid = true;
		List<String> ruleSetFileNames = getRuleSetFileNames();
		for (String fileName : ruleSetFileNames) {

			// Load original XML
			String xml1 = readFullyToString(ResourceLoader.loadResourceAsStream(fileName));

			// Load the original RuleSet
			RuleSet ruleSet1 = loadRuleSetByFileName(fileName);

			// Write to XML, first time
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			RuleSetWriter writer1 = new RuleSetWriter(outputStream1);
			writer1.write(ruleSet1);
			writer1.close();
			String xml2 = new String(outputStream1.toByteArray());

			// Read RuleSet from XML, first time
			RuleSetFactory ruleSetFactory = new RuleSetFactory();
			RuleSet ruleSet2 = ruleSetFactory.createRuleSet(new ByteArrayInputStream(outputStream1.toByteArray()));

			// Do write/read a 2nd time, just to be sure

			// Write to XML, second time
			ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
			RuleSetWriter writer2 = new RuleSetWriter(outputStream2);
			writer2.write(ruleSet2);
			writer2.close();
			String xml3 = new String(outputStream2.toByteArray());

			// System.out.println("xml1: " + xml1);
			// System.out.println("xml2: " + xml2);
			// System.out.println("xml3: " + xml3);

			// Read RuleSet from XML, second time
			RuleSet ruleSet3 = ruleSetFactory.createRuleSet(new ByteArrayInputStream(outputStream2.toByteArray()));

			// The 2 written XMLs should all be valid w.r.t Schema/DTD
			if (!isJdk14) {
				assertTrue("1st roundtrip RuleSet XML is not valid against Schema",
						validateAgainstSchema(new ByteArrayInputStream(xml2.getBytes())));
				assertTrue("2nd roundtrip RuleSet XML is not valid against Schema",
						validateAgainstSchema(new ByteArrayInputStream(xml3.getBytes())));
			}
			assertTrue("1st roundtrip RuleSet XML is not valid against DTD",
					validateAgainstDtd(new ByteArrayInputStream(xml2.getBytes())));
			assertTrue("2nd roundtrip RuleSet XML is not valid against DTD",
					validateAgainstDtd(new ByteArrayInputStream(xml3.getBytes())));

			// All 3 versions of the RuleSet should be the same
			assertEqualsRuleSet("Original RuleSet and 1st roundtrip Ruleset not the same", ruleSet1, ruleSet2);
			assertEqualsRuleSet("1st roundtrip Ruleset and 2nd roundtrip RuleSet not the same", ruleSet2, ruleSet3);

			// It's hard to compare the XML DOMs.  At least the roundtrip ones should textually be the same.
			assertEquals("1st roundtrip RuleSet XML and 2nd roundtrip RuleSet XML", xml2, xml3);

		}
		assertTrue("All XML must parse without producing validation messages.", allValid);
	}

	private void assertEqualsRuleSet(String message, RuleSet ruleSet1, RuleSet ruleSet2) {
		assertEquals("RuleSet name", ruleSet1.getName(), ruleSet2.getName());
		assertEquals("RuleSet description", ruleSet1.getDescription(), ruleSet2.getDescription());
		assertEquals("RuleSet language", ruleSet1.getLanguage(), ruleSet2.getLanguage());
		assertEquals("RuleSet exclude patterns", ruleSet1.getExcludePatterns(), ruleSet2.getExcludePatterns());
		assertEquals("RuleSet include patterns", ruleSet1.getIncludePatterns(), ruleSet2.getIncludePatterns());
		assertEquals("RuleSet rule count", ruleSet1.getRules().size(), ruleSet2.getRules().size());

		for (int i = 0; i < ruleSet1.getRules().size(); i++) {
			Rule rule1 = ((List<Rule>)ruleSet1.getRules()).get(i);
			Rule rule2 = ((List<Rule>)ruleSet2.getRules()).get(i);

			assertFalse("Different RuleReference",
					((rule1 instanceof RuleReference) && !(rule2 instanceof RuleReference))
							|| (!(rule1 instanceof RuleReference) && (rule2 instanceof RuleReference)));

			if (rule1 instanceof RuleReference) {
				RuleReference ruleReference1 = (RuleReference)rule1;
				RuleReference ruleReference2 = (RuleReference)rule2;
				assertEquals("RuleReference overridden name", ruleReference1.getOverriddenName(),
						ruleReference2.getOverriddenName());
				assertEquals("RuleReference overridden description", ruleReference1.getOverriddenDescription(),
						ruleReference2.getOverriddenDescription());
				assertEquals("RuleReference overridden message", ruleReference1.getOverriddenMessage(),
						ruleReference2.getOverriddenMessage());
				assertEquals("RuleReference overridden external info url",
						ruleReference1.getOverriddenExternalInfoUrl(), ruleReference2.getOverriddenExternalInfoUrl());
				assertEquals("RuleReference overridden priority", ruleReference1.getOverriddenPriority(),
						ruleReference2.getOverriddenPriority());
				assertEquals("RuleReference overridden examples", ruleReference1.getOverriddenExamples(),
						ruleReference2.getOverriddenExamples());
				assertEquals("RuleReference overridden properties", ruleReference1.getOverriddenProperties(),
						ruleReference2.getOverriddenProperties());
			}

			assertEquals("Rule name", rule1.getName(), rule2.getName());
			assertEquals("Rule class", rule1.getRuleClass(), rule2.getRuleClass());
			assertEquals("Rule description", rule1.getDescription(), rule2.getDescription());
			assertEquals("Rule message", rule1.getMessage(), rule2.getMessage());
			assertEquals("Rule external info url", rule1.getExternalInfoUrl(), rule2.getExternalInfoUrl());
			assertEquals("Rule priority", rule1.getPriority(), rule2.getPriority());
			assertEquals("Rule examples", rule1.getExamples(), rule2.getExamples());
			assertEquals("Rule properties", rule1.getProperties(), rule2.getProperties());
		}
	}

	private boolean validateAgainstSchema(String fileName) throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		InputStream inputStream = loadResourceAsStream(fileName);
		boolean valid = validateAgainstSchema(inputStream);
		if (!valid) {
			System.err.println("Validation against XML Schema failed for: " + fileName);
		}
		return valid;
	}

	private boolean validateAgainstSchema(InputStream inputStream) throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(true);

		// Hope we're using Xerces, or this may not work!
		// Note: Features are listed here http://xerces.apache.org/xerces2-j/features.html
		saxParserFactory.setFeature("http://xml.org/sax/features/validation", true);
		saxParserFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
		saxParserFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

		SAXParser saxParser = saxParserFactory.newSAXParser();
		ValidateDefaultHandler validateDefaultHandler = new ValidateDefaultHandler("etc/ruleset_xml_schema.xsd");
		saxParser.parse(inputStream, validateDefaultHandler);
		inputStream.close();
		return validateDefaultHandler.isValid();
	}

	private boolean validateAgainstDtd(String fileName) throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		InputStream inputStream = loadResourceAsStream(fileName);
		boolean valid = validateAgainstDtd(inputStream);
		if (!valid) {
			System.err.println("Validation against DTD failed for: " + fileName);
		}
		return valid;
	}

	private boolean validateAgainstDtd(InputStream inputStream) throws IOException, RuleSetNotFoundException,
			ParserConfigurationException, SAXException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(true);

		// Read file into memory
		String file = readFullyToString(inputStream);

		// Remove XML Schema stuff, replace with DTD
		file = file.replaceAll("<\\?xml [ a-zA-Z0-9=\".-]*\\?>", "");
		file = file.replaceAll("xmlns=\"http://pmd.sf.net/ruleset/1.0.0\"", "");
		file = file.replaceAll("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
		file = file.replaceAll(
				"xsi:schemaLocation=\"http://pmd.sf.net/ruleset/1.0.0 http://pmd.sf.net/ruleset_xml_schema.xsd\"", "");
		file = file.replaceAll("xsi:noNamespaceSchemaLocation=\"http://pmd.sf.net/ruleset_xml_schema.xsd\"", "");

		file = "<?xml version=\"1.0\"?>" + PMD.EOL + "<!DOCTYPE ruleset SYSTEM \"file://"
				+ System.getProperty("user.dir") + "/etc/ruleset.dtd\">" + PMD.EOL + file;

		inputStream = new ByteArrayInputStream(file.getBytes());

		SAXParser saxParser = saxParserFactory.newSAXParser();
		ValidateDefaultHandler validateDefaultHandler = new ValidateDefaultHandler("etc/ruleset.dtd");
		saxParser.parse(inputStream, validateDefaultHandler);
		inputStream.close();
		return validateDefaultHandler.isValid();
	}

	private String readFullyToString(InputStream inputStream) throws IOException {
		StringBuffer buf = new StringBuffer(64 * 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
			buf.append(PMD.EOL);
		}
		reader.close();
		return buf.toString();
	}

	// Gets all test PMD Ruleset XML files
	private List<String> getRuleSetFileNames() throws IOException, RuleSetNotFoundException {
		Properties properties = new Properties();
		properties.load(ResourceLoader.loadResourceAsStream("rulesets/rulesets.properties"));
		String fileNames = properties.getProperty("rulesets.testnames");
		StringTokenizer st = new StringTokenizer(fileNames, ",");
		List<String> ruleSetFileNames = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			ruleSetFileNames.add(st.nextToken());
		}
		return ruleSetFileNames;
	}

	private class ValidateDefaultHandler extends DefaultHandler {
		private final String validateDocument;
		private boolean valid = true;

		public ValidateDefaultHandler(String validateDocument) {
			this.validateDocument = validateDocument;
		}

		public boolean isValid() {
			return valid;
		}

		public void error(SAXParseException e) throws SAXException {
			log("Error", e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			log("FatalError", e);
		}

		public void warning(SAXParseException e) throws SAXException {
			log("Warning", e);
		}

		private void log(String prefix, SAXParseException e) {
			String message = prefix + " at (" + e.getLineNumber() + ", " + e.getColumnNumber() + "): " + e.getMessage();
			System.err.println(message);
			valid = false;
		}

		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			if ("http://pmd.sf.net/ruleset_xml_schema.xsd".equals(systemId) || systemId.endsWith("ruleset.dtd")) {
				try {
					InputStream inputStream = loadResourceAsStream(validateDocument);
					return new InputSource(inputStream);
				} catch (RuleSetNotFoundException e) {
					System.err.println(e.getMessage());
					throw new IOException(e.getMessage());
				}
			} else {
				throw new IllegalArgumentException("No clue how to handle: publicId=" + publicId + ", systemId="
						+ systemId);
			}
		}
	}

	private InputStream loadResourceAsStream(String resource) throws RuleSetNotFoundException {
		InputStream inputStream = ResourceLoader.loadResourceAsStream(resource, this.getClass().getClassLoader());
		if (inputStream == null) {
			throw new RuleSetNotFoundException(
					"Can't find resource "
							+ resource
							+ "  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
							+ System.getProperty("java.class.path"));
		}
		return inputStream;
	}

	private static final String REF_OVERRIDE_ORIGINAL_NAME = "<?xml version=\"1.0\"?>" + PMD.EOL
			+ "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL + " <rule "
			+ PMD.EOL + "  ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" message=\"TestMessageOverride\"> "
			+ PMD.EOL + " </rule>" + PMD.EOL + "</ruleset>";

	private static final String REF_MISPELLED_XREF = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
			+ PMD.EOL + " <description>testdesc</description>" + PMD.EOL + " <rule " + PMD.EOL
			+ "  ref=\"rulesets/unusedcode.xml/FooUnusedLocalVariable\"> " + PMD.EOL + " </rule>" + PMD.EOL
			+ "</ruleset>";

	private static final String REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM = "<?xml version=\"1.0\"?>" + PMD.EOL
			+ "<ruleset name=\"test\">" + PMD.EOL + " <description>testdesc</description>" + PMD.EOL
			+ " <rule ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" message=\"TestMessageOverride\"/> " + PMD.EOL
			+ "</ruleset>";

	private static final String REF_OVERRIDE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
			+ PMD.EOL + " <description>testdesc</description>" + PMD.EOL + " <rule " + PMD.EOL
			+ "  ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\" " + PMD.EOL + "  name=\"TestNameOverride\" "
			+ PMD.EOL + "  message=\"Test message override\"> " + PMD.EOL
			+ "  <description>Test description override</description>" + PMD.EOL
			+ "  <example>Test example override</example>" + PMD.EOL + "  <priority>3</priority>" + PMD.EOL
			+ "  <properties>" + PMD.EOL + "   <property name=\"test2\" value=\"override2\"/>" + PMD.EOL
			+ "   <property name=\"test3\"><value>override3</value></property>" + PMD.EOL
			+ "   <property name=\"test4\" value=\"new property\"/>" + PMD.EOL + "  </properties>" + PMD.EOL
			+ " </rule>" + PMD.EOL + "</ruleset>";

	private static final String EMPTY_RULESET = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
			+ PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "</ruleset>";

	private static final String SINGLE_RULE = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
			+ "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
			+ "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.MockRule\">"
			+ "<priority>3</priority>" + PMD.EOL + "</rule></ruleset>";

	private static final String MULTIPLE_RULES = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
			+ PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<rule name=\"MockRuleName1\" " + PMD.EOL
			+ "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.MockRule\">" + PMD.EOL
			+ "</rule>" + PMD.EOL + "<rule name=\"MockRuleName2\" " + PMD.EOL + "message=\"avoid the mock rule\" "
			+ PMD.EOL + "class=\"net.sourceforge.pmd.MockRule\">" + PMD.EOL + "</rule></ruleset>";

	private static final String PROPERTIES = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
			+ "<description>testdesc</description>" + PMD.EOL + "<rule name=\"MockRuleName\" " + PMD.EOL
			+ "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.MockRule\">" + PMD.EOL
			+ "<description>testdesc2</description>" + PMD.EOL + "<properties>" + PMD.EOL
			+ "<property name=\"fooBoolean\" value=\"true\"/>" + PMD.EOL
			+ "<property name=\"fooDouble\" value=\"1.0\" />" + PMD.EOL + "<property name=\"foo\" value=\"bar\"/>"
			+ PMD.EOL + "<property name=\"fooint\" value=\"2\"/>" + PMD.EOL + "</properties>" + PMD.EOL
			+ "</rule></ruleset>";

	private static final String XPATH = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
			+ "<description>testdesc</description>" + PMD.EOL + "<priority>3</priority>" + PMD.EOL
			+ "<rule name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
			+ "class=\"net.sourceforge.pmd.MockRule\">" + PMD.EOL + "<description>testdesc2</description>" + PMD.EOL
			+ "<properties>" + PMD.EOL + "<property name=\"xpath\">" + PMD.EOL + "<value>" + PMD.EOL
			+ "<![CDATA[ //Block ]]>" + PMD.EOL + "</value>" + PMD.EOL + "</property>" + PMD.EOL + "</properties>"
			+ PMD.EOL + "</rule></ruleset>";

	private static final String XPATH_PLUGINNAME = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">"
			+ PMD.EOL + "<description>testdesc</description>" + PMD.EOL + "<priority>3</priority>" + PMD.EOL
			+ "<rule name=\"MockRuleName\" " + PMD.EOL + "message=\"avoid the mock rule\" " + PMD.EOL
			+ "class=\"net.sourceforge.pmd.MockRule\">" + PMD.EOL + "<description>testdesc2</description>" + PMD.EOL
			+ "<properties>" + PMD.EOL + "<property name=\"xpath\" pluginname=\"true\">" + PMD.EOL + "<value>"
			+ PMD.EOL + "<![CDATA[ //Block ]]>" + PMD.EOL + "</value>" + PMD.EOL + "</property>" + PMD.EOL
			+ "</properties>" + PMD.EOL + "</rule></ruleset>";

	private static final String PRIORITY = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
			+ "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
			+ "message=\"avoid the mock rule\" " + PMD.EOL + "class=\"net.sourceforge.pmd.MockRule\">"
			+ "<priority>3</priority>" + PMD.EOL + "</rule></ruleset>";

	private static final String DFA = "<?xml version=\"1.0\"?>" + PMD.EOL + "<ruleset name=\"test\">" + PMD.EOL
			+ "<description>testdesc</description>" + PMD.EOL + "<rule " + PMD.EOL + "name=\"MockRuleName\" " + PMD.EOL
			+ "message=\"avoid the mock rule\" " + PMD.EOL + "dfa=\"true\" " + PMD.EOL
			+ "class=\"net.sourceforge.pmd.MockRule\">" + "<priority>3</priority>" + PMD.EOL + "</rule></ruleset>";

	private static final String INCLUDE_EXCLUDE_RULESET = "<?xml version=\"1.0\"?>" + PMD.EOL
			+ "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL
			+ "<include-pattern>include1</include-pattern>" + PMD.EOL + "<include-pattern>include2</include-pattern>"
			+ PMD.EOL + "<exclude-pattern>exclude1</exclude-pattern>" + PMD.EOL
			+ "<exclude-pattern>exclude2</exclude-pattern>" + PMD.EOL + "<exclude-pattern>exclude3</exclude-pattern>"
			+ PMD.EOL + "</ruleset>";

	private Rule loadFirstRule(String ruleSetXml) {
		RuleSet rs = loadRuleSet(ruleSetXml);
		return rs.getRules().iterator().next();
	}

	private RuleSet loadRuleSetByFileName(String ruleSetFileName) throws RuleSetNotFoundException {
		RuleSetFactory rsf = new RuleSetFactory();
		return rsf.createSingleRuleSet(ruleSetFileName);
	}

	private RuleSet loadRuleSet(String ruleSetXml) {
		RuleSetFactory rsf = new RuleSetFactory();
		return rsf.createRuleSet(new ByteArrayInputStream(ruleSetXml.getBytes()));
	}

	@Test
	public void testExternalReferences() {
		RuleSet rs = loadRuleSet(EXTERNAL_REFERENCE_RULE_SET);
		assertEquals(1, rs.size());
		assertEquals(UnusedLocalVariableRule.class.getName(), rs.getRuleByName("UnusedLocalVariable").getRuleClass());
	}

	private static final String EXTERNAL_REFERENCE_RULE_SET = "<?xml version=\"1.0\"?>" + PMD.EOL
			+ "<ruleset name=\"test\">" + PMD.EOL + "<description>testdesc</description>" + PMD.EOL
			+ "<rule ref=\"rulesets/unusedcode.xml/UnusedLocalVariable\"/>" + PMD.EOL + "</ruleset>";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RuleSetFactoryTest.class);
	}
}
