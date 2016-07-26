/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.factories.PropertyDescriptorUtil;
import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.StringUtil;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML
 * content. By default Rules will be loaded using the ClassLoader for this
 * class, using the {@link RulePriority#LOW} priority, with Rule deprecation
 * warnings off.
 * By default, the ruleset compatibility filter is active, too. See {@link RuleSetFactoryCompatibility}.
 */
public class RuleSetFactory {

    private static final Logger LOG = Logger.getLogger(RuleSetFactory.class.getName());

    private static final String DESCRIPTION = "description";
    private static final String UNEXPECTED_ELEMENT = "Unexpected element <";
    private static final String PRIORITY = "priority";
    private static final String FOR_RULE = "' for Rule ";
    private static final String MESSAGE = "message";
    private static final String EXTERNAL_INFO_URL = "externalInfoUrl";

    private ClassLoader classLoader = RuleSetFactory.class.getClassLoader();
    private RulePriority minimumPriority = RulePriority.LOW;
    private boolean warnDeprecated = false;
    private RuleSetFactoryCompatibility compatibilityFilter = new RuleSetFactoryCompatibility();

    /**
     * Set the ClassLoader to use when loading Rules.
     *
     * @param classLoader The ClassLoader to use.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Set the minimum rule priority threshold for all Rules which are loaded
     * from RuleSets via reference.
     * 
     * @param minimumPriority The minimum priority.
     */
    public void setMinimumPriority(RulePriority minimumPriority) {
        this.minimumPriority = minimumPriority;
    }

    /**
     * Set whether warning messages should be logged for usage of deprecated
     * Rules.
     * 
     * @param warnDeprecated <code>true</code> to log warning messages.
     */
    public void setWarnDeprecated(boolean warnDeprecated) {
        this.warnDeprecated = warnDeprecated;
    }

    /**
     * Disable the ruleset compatibility filter. Disabling this filter will cause
     * exception when loading a ruleset, which uses references to old/not existing rules.
     */
    public void disableCompatibilityFilter() {
        compatibilityFilter = null;
    }

    /**
     * Gets the compatibility filter in order to adjust it, e.g. add additional filters.
     * @return the {@link RuleSetFactoryCompatibility}
     */
    public RuleSetFactoryCompatibility getCompatibilityFilter() {
        return compatibilityFilter;
    }

    /**
     * Returns an Iterator of RuleSet objects loaded from descriptions from the
     * "rulesets.properties" resource for each Language with Rule support.
     *
     * @return An Iterator of RuleSet objects.
     */
    public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
        String rulesetsProperties = null;
        try {
            List<RuleSetReferenceId> ruleSetReferenceIds = new ArrayList<>();
            for (Language language : LanguageRegistry.findWithRuleSupport()) {
                Properties props = new Properties();
                rulesetsProperties = "rulesets/" + language.getTerseName() + "/rulesets.properties";
                props.load(ResourceLoader.loadResourceAsStream(rulesetsProperties));
                String rulesetFilenames = props.getProperty("rulesets.filenames");
                ruleSetReferenceIds.addAll(RuleSetReferenceId.parse(rulesetFilenames));
            }
            return createRuleSets(ruleSetReferenceIds).getRuleSetsIterator();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't find " + rulesetsProperties
                    + "; please ensure that the rulesets directory is on the classpath.  The current classpath is: "
                    + System.getProperty("java.class.path"));
        }
    }

    /**
     * Create a RuleSets from a comma separated list of RuleSet reference IDs.
     * This is a convenience method which calls
     * {@link RuleSetReferenceId#parse(String)}, and then calls
     * {@link #createRuleSets(List)}. The currently configured ClassLoader is
     * used.
     *
     * @param referenceString A comma separated list of RuleSet reference IDs.
     * @return The new RuleSets.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    public synchronized RuleSets createRuleSets(String referenceString) throws RuleSetNotFoundException {
        return createRuleSets(RuleSetReferenceId.parse(referenceString));
    }

    /**
     * Create a RuleSets from a list of RuleSetReferenceIds. The currently
     * configured ClassLoader is used.
     *
     * @param ruleSetReferenceIds The List of RuleSetReferenceId of the RuleSets
     *            to create.
     * @return The new RuleSets.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    public synchronized RuleSets createRuleSets(List<RuleSetReferenceId> ruleSetReferenceIds)
            throws RuleSetNotFoundException {
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
     * ClassLoader is used.
     *
     * @param referenceString A comma separated list of RuleSet reference IDs.
     * @return A new RuleSet.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    public synchronized RuleSet createRuleSet(String referenceString) throws RuleSetNotFoundException {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(referenceString);
        if (references.isEmpty()) {
            throw new RuleSetNotFoundException("No RuleSetReferenceId can be parsed from the string: <"
                    + referenceString + ">");
        }
        return createRuleSet(references.get(0));
    }

    /**
     * Create a RuleSet from a RuleSetReferenceId. Priority filtering is ignored
     * when loading a single Rule. The currently configured ClassLoader is used.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet to
     *            create.
     * @return A new RuleSet.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    public synchronized RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
        return createRuleSet(ruleSetReferenceId, false);
    }

    private synchronized RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId,
            boolean withDeprecatedRuleReferences) throws RuleSetNotFoundException {
        return parseRuleSetNode(ruleSetReferenceId, withDeprecatedRuleReferences);
    }

    /**
     * Create a Rule from a RuleSet created from a file name resource. The
     * currently configured ClassLoader is used.
     * <p>
     * Any Rules in the RuleSet other than the one being created, are _not_
     * created. Deprecated rules are _not_ ignored, so that they can be
     * referenced.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet with the
     *            Rule to create.
     * @param withDeprecatedRuleReferences Whether RuleReferences that are
     *            deprecated should be ignored or not
     * @return A new Rule.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    private Rule createRule(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
            throws RuleSetNotFoundException {
        if (ruleSetReferenceId.isAllRules()) {
            throw new IllegalArgumentException("Cannot parse a single Rule from an all Rule RuleSet reference: <"
                    + ruleSetReferenceId + ">.");
        }
        RuleSet ruleSet = createRuleSet(ruleSetReferenceId, withDeprecatedRuleReferences);
        return ruleSet.getRuleByName(ruleSetReferenceId.getRuleName());
    }

    /**
     * Parse a ruleset node to construct a RuleSet.
     * 
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being
     *            parsed.
     * @param withDeprecatedRuleReferences whether rule references that are
     *            deprecated should be ignored or not
     * @return The new RuleSet.
     */
    private RuleSet parseRuleSetNode(RuleSetReferenceId ruleSetReferenceId,
                                     boolean withDeprecatedRuleReferences) throws RuleSetNotFoundException {
        try (InputStream inputStream = ruleSetReferenceId.getInputStream(this.classLoader)){
            if (!ruleSetReferenceId.isExternal()) {
                throw new IllegalArgumentException("Cannot parse a RuleSet from a non-external reference: <"
                        + ruleSetReferenceId + ">.");
            }
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource;
            if (compatibilityFilter != null) {
                inputSource = new InputSource(compatibilityFilter.filterRuleSetFile(inputStream));
            } else {
                inputSource = new InputSource(inputStream);
            }
            Document document = builder.parse(inputSource);
            Element ruleSetElement = document.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setFileName(ruleSetReferenceId.getRuleSetFileName());
            ruleSet.setName(ruleSetElement.getAttribute("name"));

            NodeList nodeList = ruleSetElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (DESCRIPTION.equals(nodeName)) {
                        ruleSet.setDescription(parseTextNode(node));
                    } else if ("include-pattern".equals(nodeName)) {
                        ruleSet.addIncludePattern(parseTextNode(node));
                    } else if ("exclude-pattern".equals(nodeName)) {
                        ruleSet.addExcludePattern(parseTextNode(node));
                    } else if ("rule".equals(nodeName)) {
                        parseRuleNode(ruleSetReferenceId, ruleSet, node, withDeprecatedRuleReferences);
                    } else {
                        throw new IllegalArgumentException(UNEXPECTED_ELEMENT + node.getNodeName()
                                + "> encountered as child of <ruleset> element.");
                    }
                }
            }

            return ruleSet;
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

    private static RuleSet classNotFoundProblem(Exception ex) throws RuntimeException {
        ex.printStackTrace();
        throw new RuntimeException("Couldn't find the class " + ex.getMessage());
    }

    /**
     * Parse a rule node.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being
     *            parsed.
     * @param ruleSet The RuleSet being constructed.
     * @param ruleNode Must be a rule element node.
     * @param withDeprecatedRuleReferences whether rule references that are
     *            deprecated should be ignored or not
     */
    private void parseRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode,
            boolean withDeprecatedRuleReferences) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.endsWith("xml")) {
            parseRuleSetReferenceNode(ruleSetReferenceId, ruleSet, ruleElement, ref);
        } else if (StringUtil.isEmpty(ref)) {
            parseSingleRuleNode(ruleSetReferenceId, ruleSet, ruleNode);
        } else {
            parseRuleReferenceNode(ruleSetReferenceId, ruleSet, ruleNode, ref, withDeprecatedRuleReferences);
        }
    }

    /**
     * Parse a rule node as an RuleSetReference for all Rules. Every Rule from
     * the referred to RuleSet will be added as a RuleReference except for those
     * explicitly excluded, below the minimum priority threshold for this
     * RuleSetFactory, or which are deprecated.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being
     *            parsed.
     * @param ruleSet The RuleSet being constructed.
     * @param ruleElement Must be a rule element node.
     * @param ref The RuleSet reference.
     */
    private void parseRuleSetReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Element ruleElement,
            String ref) throws RuleSetNotFoundException {
        RuleSetReference ruleSetReference = new RuleSetReference();
        ruleSetReference.setAllRules(true);
        ruleSetReference.setRuleSetFileName(ref);
        String priority = null;
        NodeList childNodes = ruleElement.getChildNodes();
        Set<String> excludedRulesCheck = new HashSet<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (isElementNode(child, "exclude")) {
                Element excludeElement = (Element) child;
                String excludedRuleName = excludeElement.getAttribute("name");
                ruleSetReference.addExclude(excludedRuleName);
                excludedRulesCheck.add(excludedRuleName);
            } else if (isElementNode(child, PRIORITY)) {
                priority = parseTextNode(child).trim();
            }
        }

        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.setClassLoader(classLoader);
        RuleSet otherRuleSet = ruleSetFactory.createRuleSet(RuleSetReferenceId.parse(ref).get(0));
        for (Rule rule : otherRuleSet.getRules()) {
            excludedRulesCheck.remove(rule.getName());
            if (!ruleSetReference.getExcludes().contains(rule.getName())
                    && rule.getPriority().compareTo(minimumPriority) <= 0 && !rule.isDeprecated()) {
                RuleReference ruleReference = new RuleReference();
                ruleReference.setRuleSetReference(ruleSetReference);
                ruleReference.setRule(rule);
                ruleSet.addRuleIfNotExists(ruleReference);

                // override the priority
                if (priority != null) {
                    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(priority)));
                }
            }
        }
        if (!excludedRulesCheck.isEmpty()) {
            throw new IllegalArgumentException("Unable to exclude rules " + excludedRulesCheck
                    + "; perhaps the rule name is mispelled?");
        }
    }

    /**
     * Parse a rule node as a single Rule. The Rule has been fully defined
     * within the context of the current RuleSet.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being
     *            parsed.
     * @param ruleSet The RuleSet being constructed.
     * @param ruleNode Must be a rule element node.
     */
    private void parseSingleRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
                && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }

        String attribute = ruleElement.getAttribute("class");
        if (attribute == null || "".equals(attribute)) {
            throw new IllegalArgumentException("The 'class' field of rule can't be null, nor empty.");
        }
        Rule rule = (Rule) classLoader.loadClass(attribute).newInstance();
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
            throw new IllegalArgumentException("Rule " + rule.getName()
                    + " does not have a Language; missing 'language' attribute?");
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
            throw new IllegalArgumentException("The minimum Language Version '"
                    + rule.getMinimumLanguageVersion().getTerseName()
                    + "' must be prior to the maximum Language Version '"
                    + rule.getMaximumLanguageVersion().getTerseName() + FOR_RULE + rule.getName()
                    + "; perhaps swap them around?");
        }

        String since = ruleElement.getAttribute("since");
        if (StringUtil.isNotEmpty(since)) {
            rule.setSince(since);
        }
        rule.setMessage(ruleElement.getAttribute(MESSAGE));
        rule.setRuleSetName(ruleSet.getName());
        rule.setExternalInfoUrl(ruleElement.getAttribute(EXTERNAL_INFO_URL));

        if (hasAttributeSetTrue(ruleElement, "dfa")) {
            rule.setUsesDFA();
        }

        if (hasAttributeSetTrue(ruleElement, "typeResolution")) {
            rule.setUsesTypeResolution();
        }

        final NodeList nodeList = ruleElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String nodeName = node.getNodeName();
            if (nodeName.equals(DESCRIPTION)) {
                rule.setDescription(parseTextNode(node));
            } else if (nodeName.equals("example")) {
                rule.addExample(parseTextNode(node));
            } else if (nodeName.equals(PRIORITY)) {
                rule.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node).trim())));
            } else if (nodeName.equals("properties")) {
                parsePropertiesNode(rule, node);
            } else {
                throw new IllegalArgumentException(UNEXPECTED_ELEMENT + nodeName
                        + "> encountered as child of <rule> element for Rule " + rule.getName());
            }

        }
        if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
                || rule.getPriority().compareTo(minimumPriority) <= 0) {
            ruleSet.addRule(rule);
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
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being
     *            parsed.
     * @param ruleSet The RuleSet being constructed.
     * @param ruleNode Must be a rule element node.
     * @param ref A reference to a Rule.
     * @param withDeprecatedRuleReferences whether rule references that are
     *            deprecated should be ignored or not
     */
    private void parseRuleReferenceNode(RuleSetReferenceId ruleSetReferenceId, RuleSet ruleSet, Node ruleNode,
            String ref, boolean withDeprecatedRuleReferences) throws RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
                && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }

        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.setClassLoader(classLoader);

        boolean isSameRuleSet = false;
        RuleSetReferenceId otherRuleSetReferenceId = RuleSetReferenceId.parse(ref).get(0);
        if (!otherRuleSetReferenceId.isExternal()
                && containsRule(ruleSetReferenceId, otherRuleSetReferenceId.getRuleName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(ref, ruleSetReferenceId);
            isSameRuleSet = true;
        }
        Rule referencedRule = ruleSetFactory.createRule(otherRuleSetReferenceId, true); // do
                                                                                        // not
                                                                                        // ignore
                                                                                        // deprecated
                                                                                        // rule
                                                                                        // references
        if (referencedRule == null) {
            throw new IllegalArgumentException("Unable to find referenced rule "
                    + otherRuleSetReferenceId.getRuleName() + "; perhaps the rule name is mispelled?");
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

        RuleSetReference ruleSetReference = new RuleSetReference();
        ruleSetReference.setAllRules(false);
        ruleSetReference.setRuleSetFileName(otherRuleSetReferenceId.getRuleSetFileName());

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

        if (StringUtil.isNotEmpty(ruleSetReferenceId.getRuleName())
                || referencedRule.getPriority().compareTo(minimumPriority) <= 0) {
            if (withDeprecatedRuleReferences || !isSameRuleSet || !ruleReference.isDeprecated()) {
                ruleSet.addRuleReplaceIfExists(ruleReference);
            }
        }
    }

    /**
     * Check whether the given ruleName is contained in the given ruleset.
     * 
     * @param ruleSetReferenceId the ruleset to check
     * @param ruleName the rule name to search for
     * @return <code>true</code> if the ruleName exists
     */
    private boolean containsRule(RuleSetReferenceId ruleSetReferenceId, String ruleName) {
        boolean found = false;
        try (InputStream ruleSet = ruleSetReferenceId.getInputStream(classLoader)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
     * @param rule The Rule to which the properties should be added.
     * @param propertiesNode Must be a properties element node.
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
     * Parse a property node.
     *
     * @param rule The Rule to which the property should be added. //@param
     *            propertyNode Must be a property element node.
     */
    // private static void parsePropertyNode(Rule rule, Node propertyNode) {
    // Element propertyElement = (Element) propertyNode;
    // String name = propertyElement.getAttribute("name");
    // String description = propertyElement.getAttribute("description");
    // String type = propertyElement.getAttribute("type");
    // String delimiter = propertyElement.getAttribute("delimiter");
    // String min = propertyElement.getAttribute("min");
    // String max = propertyElement.getAttribute("max");
    // String value = propertyElement.getAttribute("value");
    //
    // // If value not provided, get from child <value> element.
    // if (StringUtil.isEmpty(value)) {
    // for (int i = 0; i < propertyNode.getChildNodes().getLength(); i++) {
    // Node node = propertyNode.getChildNodes().item(i);
    // if ((node.getNodeType() == Node.ELEMENT_NODE) &&
    // node.getNodeName().equals("value")) {
    // value = parseTextNode(node);
    // }
    // }
    // }
    //
    // // Setting of existing property, or defining a new property?
    // if (StringUtil.isEmpty(type)) {
    // PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(name);
    // if (propertyDescriptor == null) {
    // throw new IllegalArgumentException("Cannot set non-existant property '" +
    // name + "' on Rule " + rule.getName());
    // } else {
    // Object realValue = propertyDescriptor.valueFrom(value);
    // rule.setProperty(propertyDescriptor, realValue);
    // }
    // } else {
    // PropertyDescriptor propertyDescriptor =
    // PropertyDescriptorFactory.createPropertyDescriptor(name, description,
    // type, delimiter, min, max, value);
    // rule.definePropertyDescriptor(propertyDescriptor);
    // }
    // }
    private static <T> void setValue(Rule rule, PropertyDescriptor<T> desc, String strValue) {
        T realValue = desc.valueFrom(strValue);
        rule.setProperty(desc, realValue);
    }

    private static void parsePropertyNodeBR(Rule rule, Node propertyNode) {

        Element propertyElement = (Element) propertyNode;
        String typeId = propertyElement.getAttribute(PropertyDescriptorFields.TYPE);
        String strValue = propertyElement.getAttribute(PropertyDescriptorFields.VALUE);
        if (StringUtil.isEmpty(strValue)) {
            strValue = valueFrom(propertyElement);
        }

        // Setting of existing property, or defining a new property?
        if (StringUtil.isEmpty(typeId)) {
            String name = propertyElement.getAttribute(PropertyDescriptorFields.NAME);

            PropertyDescriptor<?> propertyDescriptor = rule.getPropertyDescriptor(name);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Cannot set non-existant property '" + name + "' on Rule "
                        + rule.getName());
            } else {
                setValue(rule, propertyDescriptor, strValue);
            }
            return;
        }

        net.sourceforge.pmd.PropertyDescriptorFactory pdFactory = PropertyDescriptorUtil.factoryFor(typeId);
        if (pdFactory == null) {
            throw new RuntimeException("No property descriptor factory for type: " + typeId);
        }

        Map<String, Boolean> valueKeys = pdFactory.expectedFields();
        Map<String, String> values = new HashMap<>(valueKeys.size());

        // populate a map of values for an individual descriptor
        for (Map.Entry<String, Boolean> entry : valueKeys.entrySet()) {
            String valueStr = propertyElement.getAttribute(entry.getKey());
            if (entry.getValue() && StringUtil.isEmpty(valueStr)) {
                System.out.println("Missing required value for: " + entry.getKey()); // debug
                                                                                     // pt
                                                                                     // TODO
            }
            values.put(entry.getKey(), valueStr);
        }

        PropertyDescriptor<?> desc = pdFactory.createWith(values);
        PropertyDescriptorWrapper<?> wrapper = new PropertyDescriptorWrapper<>(desc);

        rule.definePropertyDescriptor(wrapper);
        setValue(rule, desc, strValue);
    }

    /**
     * Parse a String from a textually type node.
     *
     * @param node The node.
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
     * @param ruleName The Rule name.
     * @return <code>true</code> if the Rule would have the given name,
     *         <code>false</code> otherwise.
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
