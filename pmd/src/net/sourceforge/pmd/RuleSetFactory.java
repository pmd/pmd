/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.util.ResourceLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML content.
 */
public class RuleSetFactory {

	private int minPriority = Rule.LOWEST_PRIORITY;

	/**
	 * Set the minimum rule priority threshold for all Rules which are loaded
	 * from RuleSets via reference.
	 * 
	 * @param minPriority The minimum priority.
	 */
	public void setMinimumPriority(int minPriority) {
		this.minPriority = minPriority;
	}

	/**
	 * Returns an Iterator of RuleSet objects loaded from descriptions from the
	 * "rulesets.properties" resource.
	 *
	 * @return An Iterator of RuleSet objects.
	 */
	public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
		try {
			Properties props = new Properties();
			props.load(ResourceLoader.loadResourceAsStream("rulesets/rulesets.properties"));
			String rulesetFilenames = props.getProperty("rulesets.filenames");
			return createRuleSets(rulesetFilenames).getRuleSetsIterator();
		} catch (IOException ioe) {
			throw new RuntimeException(
					"Couldn't find rulesets.properties; please ensure that the rulesets directory is on the classpath.  Here's the current classpath: "
							+ System.getProperty("java.class.path"));
		}
	}

	/**
	 * Create a RuleSets from a list of names.
	 * The ClassLoader of the RuleSetFactory class is used.
	 *
	 * @param ruleSetFileNames  A comma-separated list of rule set files.
	 * @return The new RuleSets.
	 * @throws RuleSetNotFoundException if unable to find a resource.
	 */
	public RuleSets createRuleSets(String ruleSetFileNames) throws RuleSetNotFoundException {
		return createRuleSets(ruleSetFileNames, getClass().getClassLoader());
	}

	/**
	 * Create a RuleSets from a list of names with a specified ClassLoader.
	 *
	 * @param ruleSetFileNames  A comma-separated list of rule set files.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @return The new RuleSets.
	 * @throws RuleSetNotFoundException if unable to find a resource.
	 */
	public RuleSets createRuleSets(String ruleSetFileNames, ClassLoader classLoader) throws RuleSetNotFoundException {
		RuleSets ruleSets = new RuleSets();

		for (StringTokenizer st = new StringTokenizer(ruleSetFileNames, ","); st.hasMoreTokens();) {
			RuleSet ruleSet = createSingleRuleSet(st.nextToken().trim(), classLoader);
			ruleSets.addRuleSet(ruleSet);
		}

		return ruleSets;
	}

	/**
	 * Create a ruleset from a name or from a list of names
	 *
	 * @param name        name of rule set file loaded as a resource
	 * @param classLoader the classloader used to load the ruleset and subsequent rules
	 * @return the new ruleset
	 * @throws RuleSetNotFoundException
	 * @deprecated Use createRuleSets instead, because this method puts all rules in one
	 *             single RuleSet object, and thus removes name and language of the
	 *             originating rule set files.
	 */
	public RuleSet createRuleSet(String name, ClassLoader classLoader) throws RuleSetNotFoundException {
		RuleSets ruleSets = createRuleSets(name, classLoader);
		RuleSet result = new RuleSet();
		RuleSet[] allRuleSets = ruleSets.getAllRuleSets();
		for (RuleSet ruleSet : allRuleSets) {
			result.addRuleSet(ruleSet);
		}
		return result;
	}

	/**
	 * Create a RuleSet from a file name resource.
	 * The ClassLoader of the RuleSetFactory class is used.
	 *
	 * @param ruleSetFileName The name of rule set file loaded as a resource.
	 * @return A new RuleSet.
	 * @throws RuleSetNotFoundException if unable to find a resource.
	 */
	public RuleSet createSingleRuleSet(String ruleSetFileName) throws RuleSetNotFoundException {
		return createSingleRuleSet(ruleSetFileName, getClass().getClassLoader());
	}

	/**
	 * Create a RuleSet from a file name resource with a specified ClassLoader.
	 *
	 * @param ruleSetFileName The name of rule set file loaded as a resource.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @return A new RuleSet.
	 * @throws RuleSetNotFoundException if unable to find a resource.
	 */
	private RuleSet createSingleRuleSet(String ruleSetFileName, ClassLoader classLoader)
			throws RuleSetNotFoundException {
		return parseRuleSetNode(ruleSetFileName, tryToGetStreamTo(ruleSetFileName, classLoader), classLoader);
	}

	/**
	 * Create a RuleSet from an InputStream.
	 * The ClassLoader of the RuleSetFactory class is used.
	 *
	 * @param inputStream InputStream containing the RuleSet XML configuration.
	 * @return A new RuleSet.
	 */
	public RuleSet createRuleSet(InputStream inputStream) {
		return createRuleSet(inputStream, getClass().getClassLoader());
	}

	/**
	 * Create a RuleSet from an InputStream with a specified ClassLoader.
	 *
	 * @param inputStream InputStream containing the RuleSet XML configuration.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @return A new RuleSet.
	 */
	public RuleSet createRuleSet(InputStream inputStream, ClassLoader classLoader) {
		return parseRuleSetNode(null, inputStream, classLoader);
	}

	/**
	 * Try to load a resource with the specified class loader
	 *
	 * @param name A resource name (e.g. a RuleSet description).
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @return An InputStream to that resource.
	 * @throws RuleSetNotFoundException if unable to find a resource.
	 */
	private InputStream tryToGetStreamTo(String name, ClassLoader classLoader) throws RuleSetNotFoundException {
		InputStream in = ResourceLoader.loadResourceAsStream(name, classLoader);
		if (in == null) {
			throw new RuleSetNotFoundException(
					"Can't find resource "
							+ name
							+ ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
							+ System.getProperty("java.class.path"));
		}
		return in;
	}

	/**
	 * Parse a ruleset node to construct a RuleSet.
	 * 
	 * @param inputStream InputStream containing the RuleSet XML configuration.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @return The new RuleSet.
	 */
	private RuleSet parseRuleSetNode(String fileName, InputStream inputStream, ClassLoader classLoader) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Element ruleSetElement = document.getDocumentElement();

			RuleSet ruleSet = new RuleSet();
			ruleSet.setFileName(fileName);
			ruleSet.setName(ruleSetElement.getAttribute("name"));
			ruleSet.setLanguage(Language.getByName(ruleSetElement.getAttribute("language")));

			NodeList nodeList = ruleSetElement.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.getNodeName().equals("description")) {
						ruleSet.setDescription(parseTextNode(node));
					} else if (node.getNodeName().equals("include-pattern")) {
						ruleSet.addIncludePattern(parseTextNode(node));
					} else if (node.getNodeName().equals("exclude-pattern")) {
						ruleSet.addExcludePattern(parseTextNode(node));
					} else if (node.getNodeName().equals("rule")) {
						parseRuleNode(ruleSet, node, classLoader);
					}
				}
			}

			return ruleSet;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + cnfe.getMessage());
		} catch (InstantiationException ie) {
			ie.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + ie.getMessage());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + iae.getMessage());
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + pce.getMessage());
		} catch (RuleSetNotFoundException rsnfe) {
			rsnfe.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + rsnfe.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + ioe.getMessage());
		} catch (SAXException se) {
			se.printStackTrace();
			throw new RuntimeException("Couldn't find that class " + se.getMessage());
		}
	}

	/**
	 * Parse a rule node.
	 *
	 * @param ruleSet The RuleSet being constructed.
	 * @param ruleNode Must be a rule element node.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 */
	private void parseRuleNode(RuleSet ruleSet, Node ruleNode, ClassLoader classLoader) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, RuleSetNotFoundException {
		Element ruleElement = (Element)ruleNode;
		String ref = ruleElement.getAttribute("ref");
		if (ref.endsWith("xml")) {
			parseRuleSetReferenceNode(ruleSet, ruleElement, ref);
		} else if (ref.trim().length() == 0) {
			parseSingleRuleNode(ruleSet, ruleNode, classLoader);
		} else {
			parseRuleReferenceNode(ruleSet, ruleNode, ref);
		}
	}

	/**
	 * Parse a rule node as an RuleSetReference for all Rules.  Every Rule from
	 * the referred to RuleSet will be added as a RuleReference except for those
	 * explicitly excluded.
	 *
	 * @param ruleSet The RuleSet being constructed.
	 * @param ruleElement Must be a rule element node.
	 * @param ref The RuleSet reference.
	 */
	private void parseRuleSetReferenceNode(RuleSet ruleSet, Element ruleElement, String ref)
			throws RuleSetNotFoundException {

		RuleSetReference ruleSetReference = new RuleSetReference();
		ruleSetReference.setAllRules(true);
		ruleSetReference.setRuleSetFileName(ref);
		NodeList excludeNodes = ruleElement.getChildNodes();
		for (int i = 0; i < excludeNodes.getLength(); i++) {
			if ((excludeNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
					&& (excludeNodes.item(i).getNodeName().equals("exclude"))) {
				Element excludeElement = (Element)excludeNodes.item(i);
				ruleSetReference.addExclude(excludeElement.getAttribute("name"));
			}
		}

		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		RuleSet otherRuleSet = ruleSetFactory.createRuleSet(ResourceLoader.loadResourceAsStream(ref));
		for (Rule rule : otherRuleSet.getRules()) {
			if (!ruleSetReference.getExcludes().contains(rule.getName()) && rule.getPriority() <= minPriority) {
				RuleReference ruleReference = new RuleReference();
				ruleReference.setRuleSetReference(ruleSetReference);
				ruleReference.setRule(rule);
				ruleSet.addRule(ruleReference);
			}
		}
	}

	/**
	 * Parse a rule node as a single Rule.  The Rule has been fully defined within
	 * the context of the current RuleSet.
	 *
	 * @param ruleSet The RuleSet being constructed.
	 * @param ruleNode Must be a rule element node.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 */
	private void parseSingleRuleNode(RuleSet ruleSet, Node ruleNode, ClassLoader classLoader)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Element ruleElement = (Element)ruleNode;

		String attribute = ruleElement.getAttribute("class");
		Class<?> c = classLoader.loadClass(attribute);
		Rule rule = (Rule)c.newInstance();

		rule.setName(ruleElement.getAttribute("name"));
		String since = ruleElement.getAttribute("since");
		if (since.length() > 0) {
			rule.setSince(since);
		}
		rule.setMessage(ruleElement.getAttribute("message"));
		rule.setRuleSetName(ruleSet.getName());
		rule.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));

		if (ruleElement.hasAttribute("dfa") && ruleElement.getAttribute("dfa").equals("true")) {
			rule.setUsesDFA();
		}

		if (ruleElement.hasAttribute("typeResolution") && ruleElement.getAttribute("typeResolution").equals("true")) {
			rule.setUsesTypeResolution();
		}

		for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
			Node node = ruleElement.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("description")) {
					rule.setDescription(parseTextNode(node));
				} else if (node.getNodeName().equals("example")) {
					rule.addExample(parseTextNode(node));
				} else if (node.getNodeName().equals("priority")) {
					rule.setPriority(Integer.parseInt(parseTextNode(node).trim()));
				} else if (node.getNodeName().equals("properties")) {
					Properties p = new Properties();
					parsePropertiesNode(p, node);
					for (Map.Entry<Object, Object> entry : p.entrySet()) {
						rule.addProperty((String)entry.getKey(), (String)entry.getValue());
					}
				}
			}
		}
		if (rule.getPriority() <= minPriority) {
			ruleSet.addRule(rule);
		}
	}

	/**
	 * Parse a rule node as a RuleReference.  A RuleReference is a single Rule
	 * which comes from another RuleSet with some of it's attributes potentially
	 * overridden.
	 *
	 * @param ruleSet The RuleSet being constructed.
	 * @param ruleNode Must be a rule element node.
	 * @param classLoader The ClassLoader to load Classes and resources.
	 * @param ref A reference to a Rule.
	 */
	private void parseRuleReferenceNode(RuleSet ruleSet, Node ruleNode, String ref) throws RuleSetNotFoundException {
		RuleSetFactory ruleSetFactory = new RuleSetFactory();

		ExternalRuleID externalRuleID = new ExternalRuleID(ref);
		RuleSet externalRuleSet = ruleSetFactory.createRuleSet(ResourceLoader.loadResourceAsStream(externalRuleID.getFilename()));
		Rule externalRule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
		if (externalRule == null) {
			throw new IllegalArgumentException("Unable to find rule " + externalRuleID.getRuleName()
					+ "; perhaps the rule name is mispelled?");
		}

		RuleSetReference ruleSetReference = new RuleSetReference();
		ruleSetReference.setAllRules(false);
		ruleSetReference.setRuleSetFileName(externalRuleID.getFilename());

		RuleReference ruleReference = new RuleReference();
		ruleReference.setRuleSetReference(ruleSetReference);
		ruleReference.setRule(externalRule);

		Element ruleElement = (Element)ruleNode;
		if (ruleElement.hasAttribute("name")) {
			ruleReference.setName(ruleElement.getAttribute("name"));
		}
		if (ruleElement.hasAttribute("message")) {
			ruleReference.setMessage(ruleElement.getAttribute("message"));
		}
		if (ruleElement.hasAttribute("externalInfoUrl")) {
			ruleReference.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));
		}
		for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
			Node node = ruleElement.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("description")) {
					ruleReference.setDescription(parseTextNode(node));
				} else if (node.getNodeName().equals("example")) {
					ruleReference.addExample(parseTextNode(node));
				} else if (node.getNodeName().equals("priority")) {
					ruleReference.setPriority(Integer.parseInt(parseTextNode(node)));
				} else if (node.getNodeName().equals("properties")) {
					Properties p = new Properties();
					parsePropertiesNode(p, node);
					ruleReference.addProperties(p);
				}
			}
		}

		if (externalRule.getPriority() <= minPriority) {
			ruleSet.addRule(ruleReference);
		}
	}

	/**
	 * Parse a properties node.
	 *
	 * @param p The Properties to which the properties should be added.
	 * @param propertiesNode Must be a properties element node.
	 */
	private static void parsePropertiesNode(Properties p, Node propertiesNode) {
		for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
			Node node = propertiesNode.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("property")) {
				parsePropertyNode(p, node);
			}
		}
	}

	/**
	 * Parse a property node.
	 *
	 * @param p The Properties to which the property should be added.
	 * @param propertyNode Must be a property element node.
	 */
	private static void parsePropertyNode(Properties p, Node propertyNode) {
		Element propertyElement = (Element)propertyNode;
		String name = propertyElement.getAttribute("name");
		String value = propertyElement.getAttribute("value");
		// TODO String description = propertyElement.getAttribute("description");
		if (value.trim().length() == 0) {
			for (int i = 0; i < propertyNode.getChildNodes().getLength(); i++) {
				Node node = propertyNode.getChildNodes().item(i);
				if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName().equals("value")) {
					value = parseTextNode(node);
				}
			}
		}
		if (propertyElement.hasAttribute("pluginname")) {
			p.setProperty("pluginname", propertyElement.getAttributeNode("pluginname").getNodeValue());
		}
		p.setProperty(name, value);
	}

	/**
	 * Parse a String from a textually type node.
	 *
	 * @param node The node.
	 * @return The String.
	 */
	private static String parseTextNode(Node node) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node childNode = node.getChildNodes().item(i);
			if (childNode.getNodeType() == Node.CDATA_SECTION_NODE || childNode.getNodeType() == Node.TEXT_NODE) {
				buffer.append(childNode.getNodeValue());
			}
		}
		return buffer.toString();
	}
}
