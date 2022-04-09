/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.internal.util.xml.XmlUtil;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.rules.RuleFactory;
import net.sourceforge.pmd.util.ResourceLoader;

import com.github.oowekyala.ooxml.DomUtils;
import com.github.oowekyala.ooxml.messages.AccumulatingErrorReporter;
import com.github.oowekyala.ooxml.messages.LoggerMessageHandler;
import com.github.oowekyala.ooxml.messages.PositionedXmlDoc;
import com.github.oowekyala.ooxml.messages.XmlErrorReporter;
import com.github.oowekyala.ooxml.messages.XmlException;
import com.github.oowekyala.ooxml.messages.XmlException.Severity;
import com.github.oowekyala.ooxml.messages.XmlMessageUtils;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML
 * content. See {@link RuleSetLoader} for configuration options and
 * their defaults.
 */
final class RuleSetFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSetFactory.class);

    private final ResourceLoader resourceLoader;
    private final RulePriority minimumPriority;
    private final boolean warnDeprecated;
    private final RuleSetFactoryCompatibility compatibilityFilter;
    private final boolean includeDeprecatedRuleReferences;

    private final Map<RuleSetReferenceId, RuleSet> parsedRulesets = new HashMap<>();

    RuleSetFactory(ResourceLoader resourceLoader,
                   RulePriority minimumPriority,
                   boolean warnDeprecated,
                   RuleSetFactoryCompatibility compatFilter,
                   boolean includeDeprecatedRuleReferences) {
        this.resourceLoader = resourceLoader;
        this.minimumPriority = minimumPriority;
        this.warnDeprecated = warnDeprecated;
        this.includeDeprecatedRuleReferences = includeDeprecatedRuleReferences;

        this.compatibilityFilter = compatFilter;
    }


    /**
     * Create a RuleSet from a RuleSetReferenceId. Priority filtering is ignored
     * when loading a single Rule. The currently configured ResourceLoader is used.
     *
     * @param ruleSetReferenceId
     *            The RuleSetReferenceId of the RuleSet to create.
     * @return A new RuleSet.
     */
    RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) {
        return createRuleSet(ruleSetReferenceId, includeDeprecatedRuleReferences);
    }

    private RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
        throws RuleSetLoadException {
        return readDocument(ruleSetReferenceId, withDeprecatedRuleReferences);
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
     */
    private Rule createRule(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences) {
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
     * @param ruleSetReferenceId           The RuleSetReferenceId of the RuleSet being parsed.
     * @param withDeprecatedRuleReferences whether rule references that are deprecated should be ignored
     *                                     or not
     *
     * @return The new RuleSet.
     *
     * @throws RuleSetLoadException If the ruleset cannot be parsed (eg IO exception, malformed XML, validation errors)
     */
    private RuleSet readDocument(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences) {

        try (CheckedInputStream inputStream = new CheckedInputStream(ruleSetReferenceId.getInputStream(resourceLoader), new Adler32());) {
            if (!ruleSetReferenceId.isExternal()) {
                throw new IllegalArgumentException(
                    "Cannot parse a RuleSet from a non-external reference: <" + ruleSetReferenceId + ">.");
            }

            LoggerMessageHandler handler = new LoggerMessageHandler(LOG, false);
            DocumentBuilder builder = createDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(ruleSetReferenceId.getRuleSetFileName());

            PositionedXmlDoc parsed = XmlMessageUtils.getInstance().parse(builder, inputSource, handler);

            @SuppressWarnings("PMD.CloseResource")
            AccumulatingErrorReporter err = makeReporter(handler, parsed);
            try {
                RuleSetBuilder ruleSetBuilder = new RuleSetBuilder(inputStream.getChecksum().getValue()).withFileName(ruleSetReferenceId.getRuleSetFileName());

                RuleSet ruleSet = parseRulesetNode(ruleSetReferenceId, withDeprecatedRuleReferences, parsed, ruleSetBuilder, err);
                err.close(Severity.WARNING, Severity.ERROR);
                return ruleSet;
            } catch (XmlException e) {
                err.close(e.getSeverity(), Severity.ERROR);
                throw e;
            }
        } catch (ParserConfigurationException | IOException ex) {
            throw new RuleSetLoadException("Couldn't read the ruleset " + ruleSetReferenceId, ex);
        }
    }

    private @NonNull AccumulatingErrorReporter makeReporter(LoggerMessageHandler handler, PositionedXmlDoc parsed) {
        return new AccumulatingErrorReporter(handler, parsed.getPositioner(), Severity.WARNING) {
            @Override
            protected String template(String message, Object... args) {
                return MessageFormat.format(message, args);
            }
        };
    }

    private RuleSet parseRulesetNode(RuleSetReferenceId ruleSetReferenceId,
                                     boolean withDeprecatedRuleReferences,
                                     PositionedXmlDoc parsed,
                                     RuleSetBuilder builder,
                                     XmlErrorReporter err) {
        Element ruleSetElement = parsed.getDocument().getDocumentElement();

        if (ruleSetElement.hasAttribute("name")) {
            builder.withName(ruleSetElement.getAttribute("name"));
        } else {
            err.warn(ruleSetElement, "RuleSet name is missing. Future versions of PMD will require it.");
            builder.withName("Missing RuleSet Name");
        }

        Set<String> rulesetReferences = new HashSet<>();

        for (Element node : DomUtils.elementsIn(ruleSetElement)) {
            String nodeName = node.getNodeName();
            String text = XmlUtil.parseTextNode(node);
            switch (nodeName) {
            case RuleFactory.DESCRIPTION:
                builder.withDescription(text);
                break;
            case "include-pattern": {
                final Pattern pattern = parseRegex(node, text, err);
                if (pattern == null) {
                    continue;
                }
                builder.withFileInclusions(pattern);
                break;
            }
            case "exclude-pattern": {
                final Pattern pattern = parseRegex(node, text, err);
                if (pattern == null) {
                    continue;
                }
                builder.withFileExclusions(pattern);
                break;
            }
            case RuleFactory.RULE:
                parseRuleNode(ruleSetReferenceId, builder, node, withDeprecatedRuleReferences, rulesetReferences, err);
                break;
            default:
                throw err.error(node, "Unexpected element as child of <ruleset>");
            }
        }

        if (!builder.hasDescription()) {
            err.warn(ruleSetElement, "RuleSet description is missing. Future versions of PMD will require it.");
            builder.withDescription("Missing description");
        }

        builder.filterRulesByPriority(minimumPriority);

        return builder.build();
    }

    private Pattern parseRegex(Element node, String text, XmlErrorReporter err) {
        final Pattern pattern;
        try {
            pattern = Pattern.compile(text);
        } catch (PatternSyntaxException pse) {
            err.error(node, pse);
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
            LOG.warn("Ignored unsupported XML Parser Feature for parsing rulesets", e);
        }

        return dbf.newDocumentBuilder();
    }

    /**
     * Parse a rule node.
     *
     * @param ruleSetReferenceId           The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder               The RuleSet being constructed.
     * @param ruleNode                     Must be a rule element node.
     * @param withDeprecatedRuleReferences whether rule references that are deprecated should be ignored
     *                                     or not
     * @param rulesetReferences            keeps track of already processed complete ruleset references in order to log
     *                                     a warning
     */
    private void parseRuleNode(RuleSetReferenceId ruleSetReferenceId,
                               RuleSetBuilder ruleSetBuilder,
                               Node ruleNode,
                               boolean withDeprecatedRuleReferences,
                               Set<String> rulesetReferences,
                               XmlErrorReporter err) {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        ref = compatibilityFilter.applyRef(ref, this.warnDeprecated);
        if (ref == null) {
            return; // deleted rule
        }
        if (ref.endsWith("xml")) {
            parseRuleSetReferenceNode(ruleSetBuilder, ruleElement, ref, rulesetReferences);
        } else if (StringUtils.isBlank(ref)) {
            parseSingleRuleNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, err);
        } else {
            parseRuleReferenceNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, ref, withDeprecatedRuleReferences, err);
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
    // todo error reporting
    private void parseRuleSetReferenceNode(RuleSetBuilder ruleSetBuilder, Element ruleElement, String ref, Set<String> rulesetReferences) {
        String priority = null;
        NodeList childNodes = ruleElement.getChildNodes();
        Set<String> excludedRulesCheck = new HashSet<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (isElementNode(child, "exclude")) {
                Element excludeElement = (Element) child;
                String excludedRuleName = excludeElement.getAttribute("name");
                excludedRuleName = compatibilityFilter.applyExclude(ref, excludedRuleName, this.warnDeprecated);
                if (excludedRuleName != null) {
                    excludedRulesCheck.add(excludedRuleName);
                }
            } else if (isElementNode(child, RuleFactory.PRIORITY)) {
                priority = XmlUtil.parseTextNode(child).trim();
            }
        }
        final RuleSetReference ruleSetReference = new RuleSetReference(ref, true, excludedRulesCheck);

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = toLoader().filterAbovePriority(RulePriority.LOW).warnDeprecated(false).toFactory();
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
            LOG.warn("The RuleSet {} has been deprecated and will be removed in PMD {}",
                    ref, PMDVersion.getNextMajorRelease());
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
            LOG.warn(
                "Unable to exclude rules {} from ruleset reference {}"
                + "; perhaps the rule name is misspelled or the rule doesn't exist anymore?",
                excludedRulesCheck, ref);
        }

        if (rulesetReferences.contains(ref)) {
            LOG.warn("The ruleset {} is referenced multiple times in \"{}\".", ref, ruleSetBuilder.getName());
        }
        rulesetReferences.add(ref);
    }

    /**
     * Parse a rule node as a single Rule. The Rule has been fully defined
     * within the context of the current RuleSet.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder     The RuleSet being constructed.
     * @param ruleNode           Must be a rule element node.
     * @param err                Error reporter
     */
    private void parseSingleRuleNode(RuleSetReferenceId ruleSetReferenceId,
                                     RuleSetBuilder ruleSetBuilder,
                                     Node ruleNode,
                                     XmlErrorReporter err) {
        Element ruleElement = (Element) ruleNode;

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
            && !isRuleName(ruleElement, ruleSetReferenceId.getRuleName())) {
            return;
        }
        Rule rule = new RuleFactory(resourceLoader).buildRule(ruleElement, err);
        rule.setRuleSetName(ruleSetBuilder.getName());

        if (warnDeprecated && StringUtils.isBlank(ruleElement.getAttribute("language"))) {
            LOG.warn("Rule {}/{} does not mention attribute language='{}',"
                        + " please mention it explicitly to be compatible with PMD 7",
                        ruleSetReferenceId.getRuleSetFileName(), rule.getName(),
                        rule.getLanguage().getTerseName());
        }

        ruleSetBuilder.addRule(rule);
    }


    /**
     * Parse a rule node as a RuleReference. A RuleReference is a single Rule
     * which comes from another RuleSet with some of it's attributes potentially
     * overridden.
     *
     * @param ruleSetReferenceId           The RuleSetReferenceId of the RuleSet being parsed.
     * @param ruleSetBuilder               The RuleSet being constructed.
     * @param ruleNode                     Must be a rule element node.
     * @param ref                          A reference to a Rule.
     * @param withDeprecatedRuleReferences whether rule references that are deprecated should be ignored
     * @param err                          Error reporter
     */
    private void parseRuleReferenceNode(RuleSetReferenceId ruleSetReferenceId,
                                        RuleSetBuilder ruleSetBuilder,
                                        Node ruleNode,
                                        String ref,
                                        boolean withDeprecatedRuleReferences,
                                        XmlErrorReporter err) {
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
                    + "; perhaps the rule name is misspelled?");
        }

        if (warnDeprecated && referencedRule.isDeprecated()) {
            if (referencedRule instanceof RuleReference) {
                RuleReference ruleReference = (RuleReference) referencedRule;
                LOG.warn("Use Rule name {}/{} instead of the deprecated Rule name {}. PMD {}"
                        + " will remove support for this deprecated Rule name usage.",
                        ruleReference.getRuleSetReference().getRuleSetFileName(),
                        ruleReference.getOriginalName(), otherRuleSetReferenceId,
                        PMDVersion.getNextMajorRelease());
            } else {
                LOG.warn("Discontinue using Rule name {} as it is scheduled for removal from PMD."
                        + " PMD {} will remove support for this Rule.",
                        otherRuleSetReferenceId, PMDVersion.getNextMajorRelease());
            }
        }

        RuleSetReference ruleSetReference = new RuleSetReference(otherRuleSetReferenceId.getRuleSetFileName(), false);

        RuleReference ruleReference = new RuleFactory(resourceLoader).decorateRule(referencedRule, ruleSetReference, ruleElement, err);

        if (warnDeprecated && ruleReference.isDeprecated() && !isSameRuleSet) {
            LOG.warn("Use Rule name {}/{} instead of the deprecated Rule name {}/{}. PMD {}"
                    + " will remove support for this deprecated Rule name usage.",
                    ruleReference.getRuleSetReference().getRuleSetFileName(),
                    ruleReference.getOriginalName(),
                    ruleSetReferenceId.getRuleSetFileName(),
                    ruleReference.getName(),
                    PMDVersion.getNextMajorRelease());
        }

        if (withDeprecatedRuleReferences || !isSameRuleSet || !ruleReference.isDeprecated()) {
            Rule existingRule = ruleSetBuilder.getExistingRule(ruleReference);
            if (existingRule instanceof RuleReference) {
                RuleReference existingRuleReference = (RuleReference) existingRule;
                // the only valid use case is: the existing rule does not override anything yet
                // which means, it is a plain reference. And the new reference overrides.
                // for all other cases, we should log a warning
                if (existingRuleReference.hasOverriddenAttributes() || !ruleReference.hasOverriddenAttributes()) {
                    LOG.warn("The rule {} is referenced multiple times in \"{}\". "
                            + "Only the last rule configuration is used.",
                            ruleReference.getName(),
                            ruleSetBuilder.getName());
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
            throw new RuleSetLoadException("Cannot load " + ruleSetReferenceId, e);
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
