/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.properties.PropertyDescriptorField.DEFAULT_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.properties.AbstractPropertyDescriptorFactory;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorFactory;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertyDescriptorUtil;
import net.sourceforge.pmd.util.ResourceLoader;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML
 * content. By default Rules will be loaded using the {@link RulePriority#LOW} priority,
 * with Rule deprecation warnings off.
 * By default, the ruleset compatibility filter is active, too.
 * See {@link RuleSetFactoryCompatibility}.
 */
public class RuleSetFactory {

    private static final Logger LOG = Logger.getLogger(RuleSetFactory.class.getName());

    private static final String DESCRIPTION = "description";
    private static final String UNEXPECTED_ELEMENT = "Unexpected element <";
    private static final String PRIORITY = "priority";
    private static final String FOR_RULE = "' for Rule ";
    private static final String MESSAGE = "message";
    private static final String EXTERNAL_INFO_URL = "externalInfoUrl";

    private final ResourceLoader resourceLoader;
    private final RulePriority minimumPriority;
    private final boolean warnDeprecated;
    private final RuleSetFactoryCompatibility compatibilityFilter;

    public RuleSetFactory() {
        this(new ResourceLoader(), RulePriority.LOW, false, true);
    }

    public RuleSetFactory(final ResourceLoader resourceLoader, final RulePriority minimumPriority,
            final boolean warnDeprecated, final boolean enableCompatibility) {
        this.resourceLoader = resourceLoader;
        this.minimumPriority = minimumPriority;
        this.warnDeprecated = warnDeprecated;

        if (enableCompatibility) {
            this.compatibilityFilter = new RuleSetFactoryCompatibility();
        } else {
            this.compatibilityFilter = null;
        }
    }

    /**
     * Constructor copying all configuration from another factory.
     *
     * @param factory
     *            The factory whose configuration to copy.
     * @param warnDeprecated
     *            Whether deprecation warnings are to be produced by this
     *            factory.
     */
    public RuleSetFactory(final RuleSetFactory factory, final boolean warnDeprecated) {
        this(factory.resourceLoader, factory.minimumPriority, warnDeprecated, factory.compatibilityFilter != null);
    }

    /**
     * Gets the compatibility filter in order to adjust it, e.g. add additional
     * filters.
     *
     * @return the {@link RuleSetFactoryCompatibility}
     */
    /* package */ RuleSetFactoryCompatibility getCompatibilityFilter() {
        return compatibilityFilter;
    }

    /**
     * Returns an Iterator of RuleSet objects loaded from descriptions from the
     * "rulesets.properties" resource for each Language with Rule support.
     *
     * @return An Iterator of RuleSet objects.
     *
     * @throws RuleSetNotFoundException if the ruleset file could not be found
     */
    public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
        String rulesetsProperties = null;
        try {
            List<RuleSetReferenceId> ruleSetReferenceIds = new ArrayList<>();
            for (Language language : LanguageRegistry.findWithRuleSupport()) {
                Properties props = new Properties();
                rulesetsProperties = "rulesets/" + language.getTerseName() + "/rulesets.properties";
                try (InputStream inputStream = resourceLoader.loadClassPathResourceAsStreamOrThrow(rulesetsProperties)) {
                    props.load(inputStream);
                }
                String rulesetFilenames = props.getProperty("rulesets.filenames");
                ruleSetReferenceIds.addAll(RuleSetReferenceId.parse(rulesetFilenames));
            }
            return createRuleSets(ruleSetReferenceIds).getRuleSetsIterator();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't find " + rulesetsProperties
                    + "; please ensure that the rulesets directory is on the classpath. The current classpath is: "
                    + System.getProperty("java.class.path"));
        }
    }

    /**
     * Create a RuleSets from a comma separated list of RuleSet reference IDs.
     * This is a convenience method which calls
     * {@link RuleSetReferenceId#parse(String)}, and then calls
     * {@link #createRuleSets(List)}. The currently configured ResourceLoader is
     * used.
     *
     * @param referenceString
     *            A comma separated list of RuleSet reference IDs.
     * @return The new RuleSets.
     * @throws RuleSetNotFoundException
     *             if unable to find a resource.
     */
    public RuleSets createRuleSets(String referenceString) throws RuleSetNotFoundException {
        return createRuleSets(RuleSetReferenceId.parse(referenceString));
    }

    /**
     * Create a RuleSets from a list of RuleSetReferenceIds. The currently
     * configured ResourceLoader is used.
     *
     * @param ruleSetReferenceIds
     *            The List of RuleSetReferenceId of the RuleSets to create.
     * @return The new RuleSets.
     * @throws RuleSetNotFoundException
     *             if unable to find a resource.
     */
    public RuleSets createRuleSets(List<RuleSetReferenceId> ruleSetReferenceIds) throws RuleSetNotFoundException {
        RuleSets ruleSets = new RuleSets();
        for (RuleSetReferenceId ruleSetReferenceId : ruleSetReferenceIds) {
            RuleSet ruleSet = createRuleSet(ruleSetReferenceId);
            ruleSets.addRuleSet(ruleSet);
        }
        return ruleSets;
    }

    /**
     * Create a RuleSet from a RuleSet reference ID string. This is a
     * convenience method which calls {@link RuleSetReferenceId#parse(String)},
     * gets the first item in the List, and then calls
     * {@link #createRuleSet(RuleSetReferenceId)}. The currently configured
     * ResourceLoader is used.
     *
     * @param referenceString
     *            A comma separated list of RuleSet reference IDs.
     * @return A new RuleSet.
     * @throws RuleSetNotFoundException
     *             if unable to find a resource.
     */
    public RuleSet createRuleSet(String referenceString) throws RuleSetNotFoundException {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(referenceString);
        if (references.isEmpty()) {
            throw new RuleSetNotFoundException(
                    "No RuleSetReferenceId can be parsed from the string: <" + referenceString + ">");
        }
        return createRuleSet(references.get(0));
    }

    /**
     * Create a RuleSet from a RuleSetReferenceId. Priority filtering is ignored
     * when loading a single Rule. The currently configured ResourceLoader is used.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet to create.
     * @return A new RuleSet.
     * @throws RuleSetNotFoundException
     *             if unable to find a resource.
     */
    public RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
        return createRuleSet(ruleSetReferenceId, false);
    }

    private RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
            throws RuleSetNotFoundException {
        return parseRuleSetNode(ruleSetReferenceId, withDeprecatedRuleReferences);
    }

    /**
     * Creates a copy of the given ruleset. All properties like name, description, fileName
     * and exclude/include patterns are copied.
     *
     * <p><strong>Note:</strong> The rule instances are shared between the original
     * and the new ruleset (copy-by-reference). This might lead to concurrency issues,
     * if the original ruleset and the new ruleset are used in different threads.
     * </p>
     *
     * @param original the original rule set to copy from
     * @return the copy
     */
    public RuleSet createRuleSetCopy(RuleSet original) {
        RuleSetBuilder builder = new RuleSetBuilder(original);
        return builder.build();
    }

    /**
     * Creates a new ruleset with the given metadata such as name, description,
     * fileName, exclude/include patterns are used. The rules are taken from the given
     * collection.
     *
     * <p><strong>Note:</strong> The rule instances are shared between the collection
     * and the new ruleset (copy-by-reference). This might lead to concurrency issues,
     * if the rules of the collection are also referenced by other rulesets and used
     * in different threads.
     * </p>
     *
     * @param name the name of the ruleset
     * @param description the description
     * @param fileName the filename
     * @param excludePatterns list of exclude patterns
     * @param includePatterns list of include patterns
     * @param rules the collection with the rules to add to the new ruleset
     * @return the new ruleset
     */
    public RuleSet createNewRuleSet(String name, String description, String fileName, Collection<String> excludePatterns,
            Collection<String> includePatterns, Collection<Rule> rules) {
        RuleSetBuilder builder = new RuleSetBuilder(0L); // TODO: checksum missing
        builder.withName(name)
            .withDescription(description)
            .withFileName(fileName)
            .setExcludePatterns(excludePatterns)
            .setIncludePatterns(includePatterns);
        for (Rule rule : rules) {
            builder.addRule(rule);
        }
        return builder.build();
    }

    /**
     * Creates a new RuleSet for a single rule
     *
     * @param rule
     *            The rule being created
     * @return The newly created RuleSet
     */
    public RuleSet createSingleRuleRuleSet(final Rule rule) {
        final long checksum;
        if (rule instanceof XPathRule) {
            checksum = rule.getProperty(XPathRule.XPATH_DESCRIPTOR).hashCode();
        } else {
            // TODO : Is this good enough? all properties' values + rule name
            checksum = rule.getPropertiesByPropertyDescriptor().values().hashCode() * 31 + rule.getName().hashCode();
        }

        final RuleSetBuilder builder = new RuleSetBuilder(checksum)
                .withName(rule.getName())
                .withDescription("RuleSet for " + rule.getName());
        builder.addRule(rule);
        return builder.build();
    }

    /**
     * Create a Rule from a RuleSet created from a file name resource. The
     * currently configured ResourceLoader is used.
     * <p>
     * Any Rules in the RuleSet other than the one being created, are _not_
     * created. Deprecated rules are _not_ ignored, so that they can be
     * referenced.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet with the Rule to create.
     * @param withDeprecatedRuleReferences
     *            Whether RuleReferences that are deprecated should be ignored
     *            or not
     * @return A new Rule.
     * @throws RuleSetNotFoundException
     *             if unable to find a resource.
     */
    private Rule createRule(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
            throws RuleSetNotFoundException {
        if (ruleSetReferenceId.isAllRules()) {
            throw new IllegalArgumentException(
                    "Cannot parse a single Rule from an all Rule RuleSet reference: <" + ruleSetReferenceId + ">.");
        }
        RuleSet ruleSet = createRuleSet(ruleSetReferenceId, withDeprecatedRuleReferences);
        return ruleSet.getRuleByName(ruleSetReferenceId.getRuleName());
    }

    /**
     * Parse a ruleset node to construct a RuleSet.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet being parsed.
     * @param withDeprecatedRuleReferences
     *            whether rule references that are deprecated should be ignored
     *            or not
     * @return The new RuleSet.
     */
    private RuleSet parseRuleSetNode(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
            throws RuleSetNotFoundException {
        try (CheckedInputStream inputStream = new CheckedInputStream(
                ruleSetReferenceId.getInputStream(resourceLoader), new Adler32());) {
            if (!ruleSetReferenceId.isExternal()) {
                throw new IllegalArgumentException(
                        "Cannot parse a RuleSet from a non-external reference: <" + ruleSetReferenceId + ">.");
            }
            DocumentBuilder builder = createDocumentBuilder();
            InputSource inputSource;
            if (compatibilityFilter != null) {
                inputSource = new InputSource(compatibilityFilter.filterRuleSetFile(inputStream));
            } else {
                inputSource = new InputSource(inputStream);
            }
            Document document = builder.parse(inputSource);
            Element ruleSetElement = document.getDocumentElement();

            RuleSetBuilder ruleSetBuilder = new RuleSetBuilder(inputStream.getChecksum().getValue())
                    .withFileName(ruleSetReferenceId.getRuleSetFileName());

            if (ruleSetElement.hasAttribute("name")) {
                ruleSetBuilder.withName(ruleSetElement.getAttribute("name"));
            } else {
                LOG.warning("RuleSet name is missing. Future versions of PMD will require it.");
                ruleSetBuilder.withName("Missing RuleSet Name");
            }

            NodeList nodeList = ruleSetElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (DESCRIPTION.equals(nodeName)) {
                        ruleSetBuilder.withDescription(parseTextNode(node));
                    } else if ("include-pattern".equals(nodeName)) {
                        ruleSetBuilder.addIncludePattern(parseTextNode(node));
                    } else if ("exclude-pattern".equals(nodeName)) {
                        ruleSetBuilder.addExcludePattern(parseTextNode(node));
                    } else if ("rule".equals(nodeName)) {
                        parseRuleNode(ruleSetReferenceId, ruleSetBuilder, node, withDeprecatedRuleReferences);
                    } else {
                        throw new IllegalArgumentException(UNEXPECTED_ELEMENT + node.getNodeName()
                                + "> encountered as child of <ruleset> element.");
                    }
                }
            }

            if (!ruleSetBuilder.hasDescription()) {
                LOG.warning("RuleSet description is missing. Future versions of PMD will require it.");
                ruleSetBuilder.withDescription("Missing description");
            }

            return ruleSetBuilder.build();
        } catch (ClassNotFoundException cnfe) {
            return classNotFoundProblem(cnfe);
        } catch (InstantiationException ie) {
            return classNotFoundProblem(ie);
        } catch (IllegalAccessException iae) {
            return classNotFoundProblem(iae);
        } catch (ParserConfigurationException pce) {
            return classNotFoundProblem(pce);
        } catch (IOException ioe) {
            return classNotFoundProblem(ioe);
        } catch (SAXException se) {
            return classNotFoundProblem(se);
        }
    }

    private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            /*
             * parser hardening
             * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J
             */
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            
            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities    
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities    
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            // Disable external DTDs as well
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            
            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
        } catch (final ParserConfigurationException e) {
            // an unsupported feature... too bad, but won't fail execution due to this
        }
        
        return dbf.newDocumentBuilder();
    }

    private static RuleSet classNotFoundProblem(Exception ex) {
        ex.printStackTrace();
        throw new RuntimeException("Couldn't find the class " + ex.getMessage());
    }

    /**
     * Parse a rule node.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder
     *            The RuleSet being constructed.
     * @param ruleNode
     *            Must be a rule element node.
     * @param withDeprecatedRuleReferences
     *            whether rule references that are deprecated should be ignored
     *            or not
     */
    private void parseRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSetBuilder ruleSetBuilder, Node ruleNode,
            boolean withDeprecatedRuleReferences)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.endsWith("xml")) {
            parseRuleSetReferenceNode(ruleSetReferenceId, ruleSetBuilder, ruleElement, ref);
        } else if (StringUtils.isBlank(ref)) {
            parseSingleRuleNode(ruleSetReferenceId, ruleSetBuilder, ruleNode);
        } else {
            parseRuleReferenceNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, ref, withDeprecatedRuleReferences);
        }
    }

    /**
     * Parse a rule node as an RuleSetReference for all Rules. Every Rule from
     * the referred to RuleSet will be added as a RuleReference except for those
     * explicitly excluded, below the minimum priority threshold for this
     * RuleSetFactory, or which are deprecated.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder
     *            The RuleSet being constructed.
     * @param ruleElement
     *            Must be a rule element node.
     * @param ref
     *            The RuleSet reference.
     */
    private void parseRuleSetReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSetBuilder ruleSetBuilder,
            Element ruleElement, String ref) throws RuleSetNotFoundException {
        String priority = null;
        NodeList childNodes = ruleElement.getChildNodes();
        Set<String> excludedRulesCheck = new HashSet<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (isElementNode(child, "exclude")) {
                Element excludeElement = (Element) child;
                String excludedRuleName = excludeElement.getAttribute("name");
                excludedRulesCheck.add(excludedRuleName);
            } else if (isElementNode(child, PRIORITY)) {
                priority = parseTextNode(child).trim();
            }
        }
        final RuleSetReference ruleSetReference = new RuleSetReference(ref, true, excludedRulesCheck);

        RuleSetFactory ruleSetFactory = new RuleSetFactory(this, warnDeprecated);
        RuleSet otherRuleSet = ruleSetFactory.createRuleSet(RuleSetReferenceId.parse(ref).get(0));
        for (Rule rule : otherRuleSet.getRules()) {
            excludedRulesCheck.remove(rule.getName());
            if (!ruleSetReference.getExcludes().contains(rule.getName()) && !rule.isDeprecated()) {
                RuleReference ruleReference = new RuleReference();
                ruleReference.setRuleSetReference(ruleSetReference);
                ruleReference.setRule(rule);
                ruleSetBuilder.addRuleIfNotExists(ruleReference);

                // override the priority
                if (priority != null) {
                    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(priority)));
                }
            }
        }
        if (!excludedRulesCheck.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unable to exclude rules " + excludedRulesCheck + "; perhaps the rule name is mispelled?");
        }
    }

    /**
     * Parse a rule node as a single Rule. The Rule has been fully defined
     * within the context of the current RuleSet.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder
     *            The RuleSet being constructed.
     * @param ruleNode
     *            Must be a rule element node.
     */
    private void parseSingleRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSetBuilder ruleSetBuilder,
            Node ruleNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
                && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }

        String attribute = ruleElement.getAttribute("class");
        if (attribute == null || "".equals(attribute)) {
            throw new IllegalArgumentException("The 'class' field of rule can't be null, nor empty.");
        }
        Rule rule = (Rule) Class.forName(attribute).newInstance();
        rule.setName(ruleElement.getAttribute("name"));

        if (ruleElement.hasAttribute("language")) {
            String languageName = ruleElement.getAttribute("language");
            Language language = LanguageRegistry.findLanguageByTerseName(languageName);
            if (language == null) {
                throw new IllegalArgumentException("Unknown Language '" + languageName + FOR_RULE + rule.getName()
                        + ", supported Languages are "
                        + LanguageRegistry.commaSeparatedTerseNamesForLanguage(LanguageRegistry.findWithRuleSupport()));
            }
            rule.setLanguage(language);
        }

        Language language = rule.getLanguage();
        if (language == null) {
            throw new IllegalArgumentException(
                    "Rule " + rule.getName() + " does not have a Language; missing 'language' attribute?");
        }

        if (ruleElement.hasAttribute("minimumLanguageVersion")) {
            String minimumLanguageVersionName = ruleElement.getAttribute("minimumLanguageVersion");
            LanguageVersion minimumLanguageVersion = language.getVersion(minimumLanguageVersionName);
            if (minimumLanguageVersion == null) {
                throw new IllegalArgumentException("Unknown minimum Language Version '" + minimumLanguageVersionName
                        + "' for Language '" + language.getTerseName() + FOR_RULE + rule.getName()
                        + "; supported Language Versions are: "
                        + LanguageRegistry.commaSeparatedTerseNamesForLanguageVersion(language.getVersions()));
            }
            rule.setMinimumLanguageVersion(minimumLanguageVersion);
        }

        if (ruleElement.hasAttribute("maximumLanguageVersion")) {
            String maximumLanguageVersionName = ruleElement.getAttribute("maximumLanguageVersion");
            LanguageVersion maximumLanguageVersion = language.getVersion(maximumLanguageVersionName);
            if (maximumLanguageVersion == null) {
                throw new IllegalArgumentException("Unknown maximum Language Version '" + maximumLanguageVersionName
                        + "' for Language '" + language.getTerseName() + FOR_RULE + rule.getName()
                        + "; supported Language Versions are: "
                        + LanguageRegistry.commaSeparatedTerseNamesForLanguageVersion(language.getVersions()));
            }
            rule.setMaximumLanguageVersion(maximumLanguageVersion);
        }

        if (rule.getMinimumLanguageVersion() != null && rule.getMaximumLanguageVersion() != null) {
            throw new IllegalArgumentException(
                    "The minimum Language Version '" + rule.getMinimumLanguageVersion().getTerseName()
                            + "' must be prior to the maximum Language Version '"
                            + rule.getMaximumLanguageVersion().getTerseName() + FOR_RULE + rule.getName()
                            + "; perhaps swap them around?");
        }

        String since = ruleElement.getAttribute("since");
        if (StringUtils.isNotBlank(since)) {
            rule.setSince(since);
        }
        rule.setMessage(ruleElement.getAttribute(MESSAGE));
        rule.setRuleSetName(ruleSetBuilder.getName());
        rule.setExternalInfoUrl(ruleElement.getAttribute(EXTERNAL_INFO_URL));

        if (hasAttributeSetTrue(ruleElement, "deprecated")) {
            rule.setDeprecated(true);
        }

        if (hasAttributeSetTrue(ruleElement, "dfa")) {
            rule.setUsesDFA();
        }

        if (hasAttributeSetTrue(ruleElement, "typeResolution")) {
            rule.setUsesTypeResolution();
        }

        if (hasAttributeSetTrue(ruleElement, "multifile")) {
            // rule.setUsesMultifile(); // TODO, once that's safe
        }

        final NodeList nodeList = ruleElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String nodeName = node.getNodeName();
            if (DESCRIPTION.equals(nodeName)) {
                rule.setDescription(parseTextNode(node));
            } else if ("example".equals(nodeName)) {
                rule.addExample(parseTextNode(node));
            } else if (PRIORITY.equals(nodeName)) {
                rule.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node).trim())));
            } else if ("properties".equals(nodeName)) {
                parsePropertiesNode(rule, node);
            } else {
                throw new IllegalArgumentException(UNEXPECTED_ELEMENT + nodeName
                        + "> encountered as child of <rule> element for Rule " + rule.getName());
            }

        }
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
                || rule.getPriority().compareTo(minimumPriority) <= 0) {
            ruleSetBuilder.addRule(rule);
        }
    }

    private static boolean hasAttributeSetTrue(Element element, String attributeId) {
        return element.hasAttribute(attributeId) && "true".equalsIgnoreCase(element.getAttribute(attributeId));
    }

    /**
     * Parse a rule node as a RuleReference. A RuleReference is a single Rule
     * which comes from another RuleSet with some of it's attributes potentially
     * overridden.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder
     *            The RuleSet being constructed.
     * @param ruleNode
     *            Must be a rule element node.
     * @param ref
     *            A reference to a Rule.
     * @param withDeprecatedRuleReferences
     *            whether rule references that are deprecated should be ignored
     *            or not
     */
    private void parseRuleReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSetBuilder ruleSetBuilder,
            Node ruleNode, String ref, boolean withDeprecatedRuleReferences) throws RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
                && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }

        RuleSetFactory ruleSetFactory = new RuleSetFactory(this, warnDeprecated);

        boolean isSameRuleSet = false;
        RuleSetReferenceId otherRuleSetReferenceId = RuleSetReferenceId.parse(ref).get(0);
        if (!otherRuleSetReferenceId.isExternal()
                && containsRule(ruleSetReferenceId, otherRuleSetReferenceId.getRuleName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(ref, ruleSetReferenceId);
            isSameRuleSet = true;
        }
        // do not ignore deprecated rule references
        Rule referencedRule = ruleSetFactory.createRule(otherRuleSetReferenceId, true);

        if (referencedRule == null) {
            throw new IllegalArgumentException("Unable to find referenced rule " + otherRuleSetReferenceId.getRuleName()
                    + "; perhaps the rule name is mispelled?");
        }

        if (warnDeprecated && referencedRule.isDeprecated()) {
            if (referencedRule instanceof RuleReference) {
                RuleReference ruleReference = (RuleReference) referencedRule;
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Use Rule name " + ruleReference.getRuleSetReference().getRuleSetFileName() + "/"
                            + ruleReference.getOriginalName() + " instead of the deprecated Rule name "
                            + otherRuleSetReferenceId
                            + ". Future versions of PMD will remove support for this deprecated Rule name usage.");
                }
            } else if (referencedRule instanceof MockRule) {
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
                            + " as it has been removed from PMD and no longer functions."
                            + " Future versions of PMD will remove support for this Rule.");
                }
            } else {
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
                            + " as it is scheduled for removal from PMD."
                            + " Future versions of PMD will remove support for this Rule.");
                }
            }
        }

        RuleSetReference ruleSetReference = new RuleSetReference(otherRuleSetReferenceId.getRuleSetFileName(), false);

        RuleReference ruleReference = new RuleReference();
        ruleReference.setRuleSetReference(ruleSetReference);
        ruleReference.setRule(referencedRule);

        if (ruleElement.hasAttribute("deprecated")) {
            ruleReference.setDeprecated(Boolean.parseBoolean(ruleElement.getAttribute("deprecated")));
        }
        if (ruleElement.hasAttribute("name")) {
            ruleReference.setName(ruleElement.getAttribute("name"));
        }
        if (ruleElement.hasAttribute(MESSAGE)) {
            ruleReference.setMessage(ruleElement.getAttribute(MESSAGE));
        }
        if (ruleElement.hasAttribute(EXTERNAL_INFO_URL)) {
            ruleReference.setExternalInfoUrl(ruleElement.getAttribute(EXTERNAL_INFO_URL));
        }
        for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
            Node node = ruleElement.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals(DESCRIPTION)) {
                    ruleReference.setDescription(parseTextNode(node));
                } else if (node.getNodeName().equals("example")) {
                    ruleReference.addExample(parseTextNode(node));
                } else if (node.getNodeName().equals(PRIORITY)) {
                    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node))));
                } else if (node.getNodeName().equals("properties")) {
                    parsePropertiesNode(ruleReference, node);
                } else {
                    throw new IllegalArgumentException(UNEXPECTED_ELEMENT + node.getNodeName()
                            + "> encountered as child of <rule> element for Rule " + ruleReference.getName());
                }
            }
        }

        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
                || referencedRule.getPriority().compareTo(minimumPriority) <= 0) {
            if (withDeprecatedRuleReferences || !isSameRuleSet || !ruleReference.isDeprecated()) {
                ruleSetBuilder.addRuleReplaceIfExists(ruleReference);
            }
        }
    }


    /**
     * Check whether the given ruleName is contained in the given ruleset.
     *
     * @param ruleSetReferenceId the ruleset to check
     * @param ruleName           the rule name to search for
     *
     * @return {@code true} if the ruleName exists
     */
    private boolean containsRule(RuleSetReferenceId ruleSetReferenceId, String ruleName) {
        boolean found = false;
        try (InputStream ruleSet = ruleSetReferenceId.getInputStream(resourceLoader)) {
            DocumentBuilder builder = createDocumentBuilder();
            Document document = builder.parse(ruleSet);
            Element ruleSetElement = document.getDocumentElement();

            NodeList rules = ruleSetElement.getElementsByTagName("rule");
            for (int i = 0; i < rules.getLength(); i++) {
                Element rule = (Element) rules.item(i);
                if (rule.hasAttribute("name") && rule.getAttribute("name").equals(ruleName)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return found;
    }

    private static boolean isElementNode(Node node, String name) {
        return node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name);
    }

    /**
     * Parse a properties node.
     *
     * @param rule
     *            The Rule to which the properties should be added.
     * @param propertiesNode
     *            Must be a properties element node.
     */
    private static void parsePropertiesNode(Rule rule, Node propertiesNode) {
        for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
            Node node = propertiesNode.getChildNodes().item(i);
            if (isElementNode(node, "property")) {
                parsePropertyNodeBR(rule, node);
            }
        }
    }

    private static String valueFrom(Node parentNode) {

        final NodeList nodeList = parentNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (isElementNode(node, "value")) {
                return parseTextNode(node);
            }
        }
        return null;
    }


    /**
     * Sets the value of a property.
     *
     * @param rule     The rule which has the property
     * @param desc     The property descriptor
     * @param strValue The string value of the property, converted to a T
     * @param <T>      The type of values of the property descriptor
     */
    private static <T> void setValue(Rule rule, PropertyDescriptor<T> desc, String strValue) {
        T realValue = desc.valueFrom(strValue);
        rule.setProperty(desc, realValue);
    }


    /**
     * Parse a property node.
     *
     * @param rule         The Rule to which the property should be added.
     * @param propertyNode Must be a property element node.
     */
    private static void parsePropertyNodeBR(Rule rule, Node propertyNode) {

        Element propertyElement = (Element) propertyNode;
        String typeId = propertyElement.getAttribute(PropertyDescriptorField.TYPE.attributeName());
        String strValue = propertyElement.getAttribute(DEFAULT_VALUE.attributeName());
        if (StringUtils.isBlank(strValue)) {
            strValue = valueFrom(propertyElement);
        }

        // Setting of existing property, or defining a new property?
        if (StringUtils.isBlank(typeId)) {
            String name = propertyElement.getAttribute(PropertyDescriptorField.NAME.attributeName());

            PropertyDescriptor<?> propertyDescriptor = rule.getPropertyDescriptor(name);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException(
                        "Cannot set non-existant property '" + name + "' on Rule " + rule.getName());
            } else {
                setValue(rule, propertyDescriptor, strValue);
            }
            return;
        }

        PropertyDescriptorFactory<?> pdFactory = PropertyDescriptorUtil.factoryFor(typeId);
        if (pdFactory == null) {
            throw new RuntimeException("No property descriptor factory for type: " + typeId);
        }

        Set<PropertyDescriptorField> valueKeys = pdFactory.expectableFields();
        Map<PropertyDescriptorField, String> values = new HashMap<>(valueKeys.size());

        // populate a map of values for an individual descriptor
        for (PropertyDescriptorField field : valueKeys) {
            String valueStr = propertyElement.getAttribute(field.attributeName());
            if (valueStr != null) {
                values.put(field, valueStr);
            }
        }

        if (StringUtils.isBlank(values.get(DEFAULT_VALUE))) {
            NodeList children = propertyElement.getElementsByTagName(DEFAULT_VALUE.attributeName());
            if (children.getLength() == 1) {
                values.put(DEFAULT_VALUE, children.item(0).getTextContent());
            } else {
                throw new RuntimeException("No value defined!");
            }
        }

        // casting is not pretty but prevents the interface from having this method
        PropertyDescriptor<?> desc = ((AbstractPropertyDescriptorFactory<?>) pdFactory).createExternalWith(values);

        rule.definePropertyDescriptor(desc);
        setValue(rule, desc, strValue);
    }

    /**
     * Parse a String from a textually type node.
     *
     * @param node
     *            The node.
     * @return The String.
     */
    private static String parseTextNode(Node node) {

        final int nodeCount = node.getChildNodes().getLength();
        if (nodeCount == 0) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < nodeCount; i++) {
            Node childNode = node.getChildNodes().item(i);
            if (childNode.getNodeType() == Node.CDATA_SECTION_NODE || childNode.getNodeType() == Node.TEXT_NODE) {
                buffer.append(childNode.getNodeValue());
            }
        }
        return buffer.toString();
    }


    /**
     * Determine if the specified rule element will represent a Rule with the
     * given name.
     *
     * @param ruleElement The rule element.
     * @param ruleName    The Rule name.
     *
     * @return {@code true} if the Rule would have the given name, {@code false} otherwise.
     */
    private boolean isRuleName(Element ruleElement, String ruleName) {
        if (ruleElement.hasAttribute("name")) {
            return ruleElement.getAttribute("name").equals(ruleName);
        } else if (ruleElement.hasAttribute("ref")) {
            RuleSetReferenceId ruleSetReferenceId = RuleSetReferenceId.parse(ruleElement.getAttribute("ref")).get(0);
            return ruleSetReferenceId.getRuleName() != null && ruleSetReferenceId.getRuleName().equals(ruleName);
        } else {
            return false;
        }
    }
}
