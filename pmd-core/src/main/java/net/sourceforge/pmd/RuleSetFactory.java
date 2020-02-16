/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
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
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.rules.RuleFactory;
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

    private final ResourceLoader resourceLoader;
    private final RulePriority minimumPriority;
    private final boolean warnDeprecated;
    private final RuleSetFactoryCompatibility compatibilityFilter;

    /**
     * @deprecated Use {@link RulesetsFactoryUtils#defaultFactory()}
     */
    @Deprecated // to be removed with PMD 7.0.0.
    public RuleSetFactory() {
        this(new ResourceLoader(), RulePriority.LOW, false, true);
    }

    /**
     * @deprecated Use {@link RulesetsFactoryUtils#createFactory(ClassLoader, RulePriority, boolean, boolean)}
     *     or {@link RulesetsFactoryUtils#createFactory(RulePriority, boolean, boolean)}
     */
    @Deprecated // to be removed with PMD 7.0.0.
    public RuleSetFactory(final ClassLoader classLoader, final RulePriority minimumPriority,
                          final boolean warnDeprecated, final boolean enableCompatibility) {
        this(new ResourceLoader(classLoader), minimumPriority, warnDeprecated, enableCompatibility);
    }

    /**
     * @deprecated Use {@link RulesetsFactoryUtils#createFactory(ClassLoader, RulePriority, boolean, boolean)}
     *     or {@link RulesetsFactoryUtils#createFactory(RulePriority, boolean, boolean)}
     */
    @Deprecated // to be hidden with PMD 7.0.0.
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
     * "categories.properties" resource for each Language with Rule support.
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
                rulesetsProperties = "category/" + language.getTerseName() + "/categories.properties";
                try (InputStream inputStream = resourceLoader.loadClassPathResourceAsStreamOrThrow(rulesetsProperties)) {
                    props.load(inputStream);
                    String rulesetFilenames = props.getProperty("rulesets.filenames");
                    if (rulesetFilenames != null) {
                        ruleSetReferenceIds.addAll(RuleSetReferenceId.parse(rulesetFilenames));
                    }
                } catch (RuleSetNotFoundException e) {
                    LOG.warning("The language " + language.getTerseName() + " provides no " + rulesetsProperties + ".");
                }
            }
            return createRuleSets(ruleSetReferenceIds).getRuleSetsIterator();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't find " + rulesetsProperties
                    + "; please ensure that the directory is on the classpath. The current classpath is: "
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
                    "No RuleSetReferenceId can be parsed from the string: <" + referenceString + '>');
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
     * @param excludePatterns list of exclude patterns, if any is not a valid regular expression, it will be ignored
     * @param includePatterns list of include patterns, if any is not a valid regular expression, it will be ignored
     * @param rules the collection with the rules to add to the new ruleset
     * @return the new ruleset
     */
    public RuleSet createNewRuleSet(String name,
                                    String description,
                                    String fileName,
                                    Collection<String> excludePatterns,
                                    Collection<String> includePatterns,
                                    Collection<Rule> rules) {
        RuleSetBuilder builder = new RuleSetBuilder(0L); // TODO: checksum missing
        builder.withName(name)
               .withDescription(description)
               .withFileName(fileName)
               .replaceFileExclusions(toPatterns(excludePatterns))
               .replaceFileInclusions(toPatterns(includePatterns));
        for (Rule rule : rules) {
            builder.addRule(rule);
        }
        return builder.build();
    }

    private Collection<Pattern> toPatterns(Collection<String> sources) {
        List<Pattern> result = new ArrayList<>();
        for (String s : sources) {
            try {
                result.add(Pattern.compile(s));
            } catch (PatternSyntaxException ignored) {

            }
        }
        return result;
    }

    /**
     * Creates a new RuleSet containing a single rule.
     *
     * @param rule
     *            The rule being created
     * @return The newly created RuleSet
     */
    public RuleSet createSingleRuleRuleSet(final Rule rule) { // TODO make static?
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

            Set<String> rulesetReferences = new HashSet<>();

            NodeList nodeList = ruleSetElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    String text = parseTextNode(node);
                    if (DESCRIPTION.equals(nodeName)) {
                        ruleSetBuilder.withDescription(text);
                    } else if ("include-pattern".equals(nodeName)) {
                        final Pattern pattern = parseRegex(text);
                        if (pattern == null) {
                            continue;
                        }
                        ruleSetBuilder.withFileInclusions(pattern);
                    } else if ("exclude-pattern".equals(nodeName)) {
                        final Pattern pattern = parseRegex(text);
                        if (pattern == null) {
                            continue;
                        }
                        ruleSetBuilder.withFileExclusions(pattern);
                    } else if ("rule".equals(nodeName)) {
                        parseRuleNode(ruleSetReferenceId, ruleSetBuilder, node, withDeprecatedRuleReferences, rulesetReferences);
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

            ruleSetBuilder.filterRulesByPriority(minimumPriority);

            return ruleSetBuilder.build();
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't find the class " + ex.getMessage(), ex);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read the ruleset " + ruleSetReferenceId + ": " + ex.getMessage(), ex);
        }
    }

    private Pattern parseRegex(String text) {
        final Pattern pattern;
        try {
            pattern = Pattern.compile(text);
        } catch (PatternSyntaxException pse) {
            LOG.warning(pse.getMessage());
            return null;
        }
        return pattern;
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
            LOG.log(Level.WARNING, "Ignored unsupported XML Parser Feature for parsing rulesets", e);
        }

        return dbf.newDocumentBuilder();
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
     * @param rulesetReferences keeps track of already processed complete ruleset references in order to log a warning
     */
    private void parseRuleNode(RuleSetReferenceId ruleSetReferenceId, RuleSetBuilder ruleSetBuilder, Node ruleNode,
            boolean withDeprecatedRuleReferences, Set<String> rulesetReferences)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.endsWith("xml")) {
            parseRuleSetReferenceNode(ruleSetBuilder, ruleElement, ref, rulesetReferences);
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
     * @param ruleSetBuilder
     *            The RuleSet being constructed.
     * @param ruleElement
     *            Must be a rule element node.
     * @param ref
     *            The RuleSet reference.
     * @param rulesetReferences keeps track of already processed complete ruleset references in order to log a warning
     */
    private void parseRuleSetReferenceNode(RuleSetBuilder ruleSetBuilder, Element ruleElement, String ref, Set<String> rulesetReferences)
            throws RuleSetNotFoundException {
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

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = new RuleSetFactory(resourceLoader, RulePriority.LOW, false, this.compatibilityFilter != null);
        RuleSet otherRuleSet = ruleSetFactory.createRuleSet(RuleSetReferenceId.parse(ref).get(0));
        List<RuleReference> potentialRules = new ArrayList<>();
        int countDeprecated = 0;
        for (Rule rule : otherRuleSet.getRules()) {
            excludedRulesCheck.remove(rule.getName());
            if (!ruleSetReference.getExcludes().contains(rule.getName())) {
                RuleReference ruleReference = new RuleReference(rule, ruleSetReference);
                // override the priority
                if (priority != null) {
                    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(priority)));
                }

                if (rule.isDeprecated()) {
                    countDeprecated++;
                }
                potentialRules.add(ruleReference);
            }
        }

        boolean rulesetDeprecated = false;
        if (!potentialRules.isEmpty() && potentialRules.size() == countDeprecated) {
            // all rules in the ruleset have been deprecated - the ruleset itself is considered to be deprecated
            rulesetDeprecated = true;
            LOG.warning("The RuleSet " + ref + " has been deprecated and will be removed in PMD " + PMDVersion.getNextMajorRelease());
        }

        for (RuleReference r : potentialRules) {
            if (rulesetDeprecated || !r.getRule().isDeprecated()) {
                // add the rule, if either the ruleset itself is deprecated (then we add all rules)
                // or if the rule is not deprecated (in that case, the ruleset might contain deprecated as well
                // as valid rules)
                ruleSetBuilder.addRuleIfNotExists(r);
            }
        }

        if (!excludedRulesCheck.isEmpty()) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.warning(
                    "Unable to exclude rules " + excludedRulesCheck + " from ruleset reference " + ref
                    + "; perhaps the rule name is mispelled or the rule doesn't exist anymore?");
            }
        }

        if (rulesetReferences.contains(ref)) {
            LOG.warning("The ruleset " + ref + " is referenced multiple times in \""
                    + ruleSetBuilder.getName() + "\".");
        }
        rulesetReferences.add(ref);
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
        Rule rule = new RuleFactory(resourceLoader).buildRule(ruleElement);
        rule.setRuleSetName(ruleSetBuilder.getName());

        ruleSetBuilder.addRule(rule);
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

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = new RuleSetFactory(resourceLoader, RulePriority.LOW, false, this.compatibilityFilter != null);

        boolean isSameRuleSet = false;
        RuleSetReferenceId otherRuleSetReferenceId = RuleSetReferenceId.parse(ref).get(0);
        if (!otherRuleSetReferenceId.isExternal()
                && containsRule(ruleSetReferenceId, otherRuleSetReferenceId.getRuleName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(ref, ruleSetReferenceId);
            isSameRuleSet = true;
        } else if (otherRuleSetReferenceId.isExternal()
                && otherRuleSetReferenceId.getRuleSetFileName().equals(ruleSetReferenceId.getRuleSetFileName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(otherRuleSetReferenceId.getRuleName(), ruleSetReferenceId);
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
                    LOG.warning("Use Rule name " + ruleReference.getRuleSetReference().getRuleSetFileName() + '/'
                            + ruleReference.getOriginalName() + " instead of the deprecated Rule name "
                            + otherRuleSetReferenceId
                            + ". PMD " + PMDVersion.getNextMajorRelease()
                            + " will remove support for this deprecated Rule name usage.");
                }
            } else if (referencedRule instanceof MockRule) {
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
                            + " as it has been removed from PMD and no longer functions."
                            + " PMD " + PMDVersion.getNextMajorRelease()
                            + " will remove support for this Rule.");
                }
            } else {
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Discontinue using Rule name " + otherRuleSetReferenceId
                            + " as it is scheduled for removal from PMD."
                            + " PMD " + PMDVersion.getNextMajorRelease()
                            + " will remove support for this Rule.");
                }
            }
        }

        RuleSetReference ruleSetReference = new RuleSetReference(otherRuleSetReferenceId.getRuleSetFileName(), false);

        RuleReference ruleReference = new RuleFactory(resourceLoader).decorateRule(referencedRule, ruleSetReference, ruleElement);

        if (warnDeprecated && ruleReference.isDeprecated() && !isSameRuleSet) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.warning("Use Rule name " + ruleReference.getRuleSetReference().getRuleSetFileName() + '/'
                        + ruleReference.getOriginalName() + " instead of the deprecated Rule name "
                        + ruleSetReferenceId.getRuleSetFileName() + '/' + ruleReference.getName()
                        + ". PMD " + PMDVersion.getNextMajorRelease()
                        + " will remove support for this deprecated Rule name usage.");
            }
        }

        if (withDeprecatedRuleReferences || !isSameRuleSet || !ruleReference.isDeprecated()) {
            Rule existingRule = ruleSetBuilder.getExistingRule(ruleReference);
            if (existingRule instanceof RuleReference) {
                RuleReference existingRuleReference = (RuleReference) existingRule;
                // the only valid use case is: the existing rule does not override anything yet
                // which means, it is a plain reference. And the new reference overrides.
                // for all other cases, we should log a warning
                if (existingRuleReference.hasOverriddenAttributes() || !ruleReference.hasOverriddenAttributes()) {
                    LOG.warning("The rule " + ruleReference.getName() + " is referenced multiple times in \""
                            + ruleSetBuilder.getName() + "\". "
                            + "Only the last rule configuration is used.");
                }
            }

            ruleSetBuilder.addRuleReplaceIfExists(ruleReference);
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
