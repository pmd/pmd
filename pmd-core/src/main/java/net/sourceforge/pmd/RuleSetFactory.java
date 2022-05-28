/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import net.sourceforge.pmd.internal.DOMUtils;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.rules.RuleFactory;
import net.sourceforge.pmd.util.ResourceLoader;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML
 * content. See {@link RuleSetLoader} for configuration options and
 * their defaults.
 *
 * @deprecated Use a {@link RuleSetLoader} instead. This will be hidden in PMD 7
 *     (it's the implementation, while {@link RuleSetLoader} is the API).
 */
@Deprecated
public class RuleSetFactory {

    private static final Logger LOG = Logger.getLogger(RuleSetFactory.class.getName());

    private static final String DESCRIPTION = "description";
    private static final String UNEXPECTED_ELEMENT = "Unexpected element <";
    private static final String PRIORITY = "priority";

    private final ResourceLoader resourceLoader;
    private final RulePriority minimumPriority;
    private final boolean warnDeprecated;
    private final RuleSetFactoryCompatibility compatibilityFilter;
    private final boolean includeDeprecatedRuleReferences;

    private final Map<RuleSetReferenceId, RuleSet> parsedRulesets = new HashMap<>();

    /**
     * @deprecated Use a {@link RuleSetLoader} to build a new factory
     */
    @Deprecated // to be removed with PMD 7.0.0.
    public RuleSetFactory() {
        this(new ResourceLoader(), RulePriority.LOW, false, true);
    }

    /**
     * @deprecated Use a {@link RuleSetLoader} to build a new factory
     */
    @Deprecated // to be removed with PMD 7.0.0.
    public RuleSetFactory(final ClassLoader classLoader, final RulePriority minimumPriority,
                          final boolean warnDeprecated, final boolean enableCompatibility) {
        this(new ResourceLoader(classLoader), minimumPriority, warnDeprecated, enableCompatibility);
    }

    /**
     * @deprecated Use a {@link RuleSetLoader} to build a new factory
     */
    @Deprecated // to be hidden with PMD 7.0.0.
    public RuleSetFactory(final ResourceLoader resourceLoader, final RulePriority minimumPriority,
                          final boolean warnDeprecated, final boolean enableCompatibility) {
        this(resourceLoader, minimumPriority, warnDeprecated, enableCompatibility, false);
    }

    RuleSetFactory(final ResourceLoader resourceLoader, final RulePriority minimumPriority,
            final boolean warnDeprecated, final boolean enableCompatibility, boolean includeDeprecatedRuleReferences) {
        this.resourceLoader = resourceLoader;
        this.minimumPriority = minimumPriority;
        this.warnDeprecated = warnDeprecated;
        this.includeDeprecatedRuleReferences = includeDeprecatedRuleReferences;

        if (enableCompatibility) {
            this.compatibilityFilter = new RuleSetFactoryCompatibility();
        } else {
            this.compatibilityFilter = null;
        }
    }

    /**
     * Constructor copying all configuration from another factory.
     *
     * @param factory        The factory whose configuration to copy.
     * @param warnDeprecated Whether deprecation warnings are to be produced by this
     *                       factory
     *
     * @deprecated Use {@link #toLoader()} to rebuild a factory from a configuration
     */
    @Deprecated
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
     *
     * @deprecated Use {@link RuleSetLoader#getStandardRuleSets()}
     */
    @Deprecated
    public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
        return toLoader().getStandardRuleSets().iterator();
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
     *
     * @deprecated Use {@link RuleSetLoader#loadFromResource(String)},
     * but note that that method does not split on commas
     */
    @Deprecated
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
     *
     * @deprecated Will not be replaced
     */
    @Deprecated
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
     *
     * @deprecated Use {@link RuleSetLoader#loadFromResource(String)} and discard the rest of the list.
     */
    @Deprecated
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
     *
     * @deprecated Will not be replaced
     */
    @Deprecated
    public RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
        return createRuleSet(ruleSetReferenceId, includeDeprecatedRuleReferences);
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
     *
     * @deprecated Use {@link RuleSet#copy(RuleSet)}
     */
    @Deprecated
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
     *
     * @deprecated Use {@link RuleSet#create(String, String, String, Collection, Collection, Iterable)}
     */
    @Deprecated
    public RuleSet createNewRuleSet(String name,
                                    String description,
                                    String fileName,
                                    Collection<String> excludePatterns,
                                    Collection<String> includePatterns,
                                    Collection<Rule> rules) {
        return RuleSet.create(name, description, fileName, toPatterns(excludePatterns), toPatterns(includePatterns), rules);
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
     * @param rule The rule being created
     *
     * @return The newly created RuleSet
     *
     * @deprecated Use {@link RuleSet#forSingleRule(Rule)}
     */
    @Deprecated
    public RuleSet createSingleRuleRuleSet(final Rule rule) {
        return RuleSet.forSingleRule(rule);
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
        RuleSet ruleSet;
        // java8: computeIfAbsent
        if (parsedRulesets.containsKey(ruleSetReferenceId)) {
            ruleSet = parsedRulesets.get(ruleSetReferenceId);
        } else {
            ruleSet = createRuleSet(ruleSetReferenceId, withDeprecatedRuleReferences);
            parsedRulesets.put(ruleSetReferenceId, ruleSet);
        }
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
                    String text = DOMUtils.parseTextNode(node);
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
        throws RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        if (ruleElement.hasAttribute("ref")) {
            String ref = ruleElement.getAttribute("ref");
            RuleSetReferenceId refId = parseReferenceAndWarn(ruleSetBuilder, ref);
            if (refId != null) {
                if (refId.isAllRules()) {
                    parseRuleSetReferenceNode(ruleSetBuilder, ruleElement, ref, refId, rulesetReferences);
                } else {
                    parseRuleReferenceNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, ref, refId, withDeprecatedRuleReferences);
                }
                return;
            }
        }
        parseSingleRuleNode(ruleSetReferenceId, ruleSetBuilder, ruleNode);
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
    private void parseRuleSetReferenceNode(RuleSetBuilder ruleSetBuilder, Element ruleElement,
                                           String ref,
                                           RuleSetReferenceId ruleSetReferenceId, Set<String> rulesetReferences)
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
                priority = DOMUtils.parseTextNode(child).trim();
            }
        }
        final RuleSetReference ruleSetReference = new RuleSetReference(ref, true, excludedRulesCheck);

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = toLoader().filterAbovePriority(RulePriority.LOW).warnDeprecated(false).toFactory();
        RuleSet otherRuleSet = ruleSetFactory.createRuleSet(ruleSetReferenceId);
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
                    + "; perhaps the rule name is misspelled or the rule doesn't exist anymore?");
            }
        }

        if (rulesetReferences.contains(ref)) {
            LOG.warning("The ruleset " + ref + " is referenced multiple times in \""
                        + ruleSetBuilder.getName() + "\".");
        }
        rulesetReferences.add(ref);
    }

    private RuleSetReferenceId parseReferenceAndWarn(RuleSetBuilder ruleSetBuilder, String ref) {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(ref, warnDeprecated);
        if (references.size() > 1 && warnDeprecated) {
            LOG.warning("Using a comma separated list as a ref attribute is deprecated. "
                        + "All references but the first are ignored. Reference: '" + ref + "'");
        } else if (references.isEmpty()) {
            LOG.warning("Empty ref attribute in ruleset '" + ruleSetBuilder.getName() + "'");
            return null;
        }
        return references.get(0);
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
            Node ruleNode) {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
            && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }
        Rule rule = new RuleFactory(resourceLoader).buildRule(ruleElement);
        rule.setRuleSetName(ruleSetBuilder.getName());

        if (warnDeprecated && StringUtils.isBlank(ruleElement.getAttribute("language"))) {
            LOG.warning("Rule " + ruleSetReferenceId.getRuleSetFileName() + "/" + rule.getName() + " does not mention attribute"
                            + " language='" + rule.getLanguage().getTerseName() + "',"
                            + " please mention it explicitly to be compatible with PMD 7");
        }

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
                                        Node ruleNode, String ref,
                                        RuleSetReferenceId otherRuleSetReferenceId,
                                        boolean withDeprecatedRuleReferences) throws RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
                && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = toLoader().filterAbovePriority(RulePriority.LOW).warnDeprecated(false).toFactory();

        boolean isSameRuleSet = false;
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
                    + "; perhaps the rule name is misspelled?");
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
        // TODO: avoid reloading the ruleset once again
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


    /**
     * Create a new {@link RuleSetLoader} with the same config as this
     * factory. This is a transitional API.
     */
    public RuleSetLoader toLoader() {
        return new RuleSetLoader().loadResourcesWith(resourceLoader)
                                  .filterAbovePriority(minimumPriority)
                                  .warnDeprecated(warnDeprecated)
                                  .enableCompatibility(compatibilityFilter != null)
                                  .includeDeprecatedRuleReferences(includeDeprecatedRuleReferences);
    }


}
