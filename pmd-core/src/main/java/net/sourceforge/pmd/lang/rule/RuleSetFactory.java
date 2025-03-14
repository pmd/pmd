/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.DESCRIPTION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.EXCLUDE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.EXCLUDE_PATTERN;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.INCLUDE_PATTERN;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.NAME;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PRIORITY;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.REF;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.RULE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.RULESET;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.rule.internal.RuleSetReference;
import net.sourceforge.pmd.lang.rule.internal.RuleSetReferenceId;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.internal.ResourceLoader;
import net.sourceforge.pmd.util.internal.xml.PmdXmlReporter;
import net.sourceforge.pmd.util.internal.xml.XmlErrorMessages;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

import com.github.oowekyala.ooxml.DomUtils;
import com.github.oowekyala.ooxml.messages.NiceXmlMessageSpec;
import com.github.oowekyala.ooxml.messages.OoxmlFacade;
import com.github.oowekyala.ooxml.messages.PositionedXmlDoc;
import com.github.oowekyala.ooxml.messages.XmlException;
import com.github.oowekyala.ooxml.messages.XmlMessageHandler;
import com.github.oowekyala.ooxml.messages.XmlMessageReporterBase;
import com.github.oowekyala.ooxml.messages.XmlPosition;
import com.github.oowekyala.ooxml.messages.XmlPositioner;
import com.github.oowekyala.ooxml.messages.XmlSeverity;

/**
 * RuleSetFactory is responsible for creating RuleSet instances from XML
 * content. See {@link RuleSetLoader} for configuration options and
 * their defaults.
 */
final class RuleSetFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSetFactory.class);

    private final ResourceLoader resourceLoader;
    private final LanguageRegistry languageRegistry;
    private final RulePriority minimumPriority;
    private final boolean warnDeprecated;
    private final PmdReporter reporter;
    private final boolean includeDeprecatedRuleReferences;

    private final Map<RuleSetReferenceId, RuleSet> parsedRulesets = new HashMap<>();

    RuleSetFactory(ResourceLoader resourceLoader,
                   LanguageRegistry languageRegistry,
                   RulePriority minimumPriority,
                   boolean warnDeprecated,
                   boolean includeDeprecatedRuleReferences,
                   PmdReporter reporter) {
        this.resourceLoader = resourceLoader;
        this.languageRegistry = Objects.requireNonNull(languageRegistry);
        this.minimumPriority = minimumPriority;
        this.warnDeprecated = warnDeprecated;
        this.includeDeprecatedRuleReferences = includeDeprecatedRuleReferences;

        this.reporter = reporter;
    }


    /**
     * Create a RuleSet from a RuleSetReferenceId. Priority filtering is ignored
     * when loading a single Rule. The currently configured ResourceLoader is used.
     *
     * @param ruleSetReferenceId The RuleSetReferenceId of the RuleSet to create.
     *
     * @return A new RuleSet.
     */
    @NonNull RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) {
        return createRuleSet(ruleSetReferenceId, includeDeprecatedRuleReferences);
    }

    private @NonNull RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences)
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
        RuleSetReferenceId parentRuleset = ruleSetReferenceId.getParentRulesetIfThisIsARule();
        if (parentRuleset == null) {
            throw new IllegalArgumentException(
                "Cannot parse a single Rule from an all Rule RuleSet reference: <" + ruleSetReferenceId + ">.");
        }
        // can't use computeIfAbsent as creating a ruleset may add more entries to the map.
        RuleSet ruleSet = parsedRulesets.get(parentRuleset);
        if (ruleSet == null) {
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
    private @NonNull RuleSet readDocument(RuleSetReferenceId ruleSetReferenceId, boolean withDeprecatedRuleReferences) {

        try (CheckedInputStream inputStream = new CheckedInputStream(ruleSetReferenceId.getInputStream(resourceLoader), new Adler32())) {
            if (!ruleSetReferenceId.isAbsolute()) {
                throw new IllegalArgumentException(
                    "Cannot parse a RuleSet from a non-absolute reference: <" + ruleSetReferenceId + ">.");
            }

            XmlMessageHandler printer = getXmlMessagePrinter();
            DocumentBuilder builder = createDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(ruleSetReferenceId.getRuleSetFileName());

            OoxmlFacade ooxml = new OoxmlFacade()
                .withPrinter(printer)
                .withAnsiColors(false);
            PositionedXmlDoc parsed = ooxml.parse(builder, inputSource);

            @SuppressWarnings("PMD.CloseResource")
            PmdXmlReporterImpl err = new PmdXmlReporterImpl(reporter, ooxml, parsed.getPositioner());
            try {
                RuleSetBuilder ruleSetBuilder = new RuleSetBuilder(inputStream.getChecksum().getValue()).withFileName(ruleSetReferenceId.getRuleSetFileName());

                RuleSet ruleSet = parseRulesetNode(ruleSetReferenceId, withDeprecatedRuleReferences, parsed, ruleSetBuilder, err);
                if (err.errCount > 0) {
                    // note this makes us jump to the catch branch
                    // these might have been non-fatal errors
                    String message;
                    if (err.errCount == 1) {
                        message = "An XML validation error occurred";
                    } else {
                        message = err.errCount + " XML validation errors occurred";
                    }
                    throw new RuleSetLoadException(ruleSetReferenceId, message);
                }
                return ruleSet;
            } catch (Exception | Error e) {
                throw e;
            }
        } catch (ParserConfigurationException | IOException ex) {
            throw new RuleSetLoadException(ruleSetReferenceId, ex);
        }
    }


    private RuleSet parseRulesetNode(RuleSetReferenceId ruleSetReferenceId,
                                     boolean withDeprecatedRuleReferences,
                                     PositionedXmlDoc parsed,
                                     RuleSetBuilder builder,
                                     PmdXmlReporter err) {
        Element ruleSetElement = parsed.getDocument().getDocumentElement();

        if (ruleSetElement.hasAttribute("name")) {
            builder.withName(ruleSetElement.getAttribute("name"));
        } else {
            err.at(ruleSetElement).warn("RuleSet name is missing. Future versions of PMD will require it.");
            builder.withName("Missing RuleSet Name");
        }

        Set<String> rulesetReferences = new HashSet<>();

        for (Element node : DomUtils.children(ruleSetElement)) {
            String text = XmlUtil.parseTextNode(node);
            if (DESCRIPTION.matchesElt(node)) {
                builder.withDescription(text);
            } else if (INCLUDE_PATTERN.matchesElt(node)) {
                final Pattern pattern = parseRegex(node, text, err);
                if (pattern == null) {
                    continue;
                }
                builder.withFileInclusions(pattern);
            } else if (EXCLUDE_PATTERN.matchesElt(node)) {
                final Pattern pattern = parseRegex(node, text, err);
                if (pattern == null) {
                    continue;
                }
                builder.withFileExclusions(pattern);
            } else if (RULE.matchesElt(node)) {
                try {
                    parseRuleNode(ruleSetReferenceId, builder, node, withDeprecatedRuleReferences, rulesetReferences, err);
                } catch (XmlException ignored) {
                    // already reported (it's an XmlException), error count
                    // was incremented so parent method will throw RuleSetLoadException.
                }
            } else {
                err.at(node).error(XmlErrorMessages.ERR__UNEXPECTED_ELEMENT_IN,
                                   node.getTagName(),
                                   RULESET);
            }
        }

        if (!builder.hasDescription()) {
            err.at(ruleSetElement).warn("RuleSet description is missing. Future versions of PMD will require it.");
            builder.withDescription("Missing description");
        }

        builder.filterRulesByPriority(minimumPriority);

        return builder.build();
    }

    private Pattern parseRegex(Element node, String text, PmdXmlReporter err) {
        final Pattern pattern;
        try {
            pattern = Pattern.compile(text);
        } catch (PatternSyntaxException pse) {
            err.at(node).error(pse);
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
                               Element ruleNode,
                               boolean withDeprecatedRuleReferences,
                               Set<String> rulesetReferences,
                               PmdXmlReporter err) {
        if (REF.hasAttribute(ruleNode)) {
            String ref = REF.getAttributeOrThrow(ruleNode, err);
            RuleSetReferenceId refId = parseReferenceAndWarn(ref, REF.getAttributeNode(ruleNode), err);
            if (refId != null) {
                if (refId.isAllRules()) {
                    parseRuleSetReferenceNode(ruleSetBuilder, ruleNode, ref, refId, rulesetReferences, err);
                } else {
                    parseRuleReferenceNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, ref, refId, withDeprecatedRuleReferences, err);
                }
                return;
            }
        }
        parseSingleRuleNode(ruleSetReferenceId, ruleSetBuilder, ruleNode, err);
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
    private void parseRuleSetReferenceNode(RuleSetBuilder ruleSetBuilder,
                                           Element ruleElement,
                                           String ref,
                                          RuleSetReferenceId ruleSetReferenceId, Set<String> rulesetReferences,
                                           PmdXmlReporter err) {
        RulePriority priority = null;
        Map<String, Element> excludedRulesCheck = new HashMap<>();
        for (Element child : XmlUtil.getElementChildrenList(ruleElement)) {
            if (EXCLUDE.matchesElt(child)) {
                String excludedRuleName;
                try {
                    excludedRuleName = NAME.getAttributeOrThrow(child, err);
                } catch (XmlException ignored) {
                    // has been reported
                    continue;
                }
                if (excludedRuleName != null) {
                    excludedRulesCheck.put(excludedRuleName, child);
                }
            } else if (PRIORITY.matchesElt(child)) {
                priority = RuleFactory.parsePriority(err, child);
            } else {
                XmlUtil.reportIgnoredUnexpectedElt(ruleElement, child, setOf(EXCLUDE, PRIORITY), err);
            }
        }
        final RuleSetReference ruleSetReference = new RuleSetReference(ref, true, excludedRulesCheck.keySet());

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
                    ruleReference.setPriority(priority);
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
            err.at(REF.getAttributeNode(ruleElement))
                .warn("The RuleSet {0} has been deprecated and will be removed in PMD {1}",
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
            excludedRulesCheck.forEach(
                (name, elt) ->
                    err.at(elt).warn("Exclude pattern ''{0}'' did not match any rule in ruleset ''{1}''", name, ref));
        }

        if (rulesetReferences.contains(ref)) {
            err.at(ruleElement).warn("The ruleset {0} is referenced multiple times in ruleset ''{1}''", ref, ruleSetBuilder.getName());
        }
        rulesetReferences.add(ref);
    }

    private RuleSetReferenceId parseReferenceAndWarn(String ref,
                                                     Node xmlPlace,
                                                     PmdXmlReporter err) {
        if (ref == null) {
            err.at(xmlPlace).warn("Rule reference references a deleted rule, ignoring");
            return null; // deleted rule
        }
        // only emit a warning if we check for deprecated syntax
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(ref);
        if (references.size() > 1 && warnDeprecated) {
            err.at(xmlPlace).warn("Using a comma separated list as a ref attribute is deprecated. "
                                      + "All references but the first are ignored.");
        } else if (references.isEmpty()) {
            err.at(xmlPlace).warn("Empty ref attribute");
            return null;
        }
        return references.get(0);
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
                                     Element ruleNode,
                                     PmdXmlReporter err) {

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
            && !isRuleName(ruleNode, ruleSetReferenceId.getRuleName())) {
            return;
        }
        Rule rule = new RuleFactory(resourceLoader, languageRegistry).buildRule(ruleNode, err);
        rule.setRuleSetName(ruleSetBuilder.getName());

        if (warnDeprecated && StringUtils.isBlank(ruleNode.getAttribute("language"))) {
            err.at(ruleNode).warn(
                "Rule {0}/{1} does not mention attribute language='{2}',"
                    + " please mention it explicitly to be compatible with PMD 7",
                ruleSetReferenceId.getRuleSetFileName(), rule.getName(),
                rule.getLanguage().getId());
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
                                        Element ruleNode,
                                        String ref,
                                       RuleSetReferenceId otherRuleSetReferenceId,
                                        boolean withDeprecatedRuleReferences,
                                        PmdXmlReporter err) {

        // Stop if we're looking for a particular Rule, and this element is not
        // it.
        if (StringUtils.isNotBlank(ruleSetReferenceId.getRuleName())
            && !isRuleName(ruleNode, ruleSetReferenceId.getRuleName())) {
            return;
        }

        // load the ruleset with minimum priority low, so that we get all rules, to be able to exclude any rule
        // minimum priority will be applied again, before constructing the final ruleset
        RuleSetFactory ruleSetFactory = toLoader().filterAbovePriority(RulePriority.LOW).warnDeprecated(false).toFactory();

        boolean isSameRuleSet = false;
        if (!otherRuleSetReferenceId.isAbsolute()
            && containsRule(ruleSetReferenceId, otherRuleSetReferenceId.getRuleName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(ref, ruleSetReferenceId);
            isSameRuleSet = true;
        } else if (otherRuleSetReferenceId.isAbsolute()
            && otherRuleSetReferenceId.getRuleSetFileName().equals(ruleSetReferenceId.getRuleSetFileName())) {
            otherRuleSetReferenceId = new RuleSetReferenceId(otherRuleSetReferenceId.getRuleName(), ruleSetReferenceId);
            isSameRuleSet = true;
        }
        // do not ignore deprecated rule references
        Rule referencedRule = ruleSetFactory.createRule(otherRuleSetReferenceId, true);

        if (referencedRule == null) {
            throw err.at(ruleNode).error(
                "Unable to find referenced rule {0}"
                    + "; perhaps the rule name is misspelled?",
                otherRuleSetReferenceId.getRuleName());
        }

        if (warnDeprecated && referencedRule.isDeprecated()) {
            if (referencedRule instanceof RuleReference) {
                RuleReference ruleReference = (RuleReference) referencedRule;
                err.at(ruleNode).warn(
                    "Use Rule name {0}/{1} instead of the deprecated Rule name {2}. PMD {3}"
                        + " will remove support for this deprecated Rule name usage.",
                    ruleReference.getRuleSetReference().getRuleSetFileName(),
                    ruleReference.getOriginalName(), otherRuleSetReferenceId,
                    PMDVersion.getNextMajorRelease());
            } else {
                err.at(ruleNode).warn(
                    "Discontinue using Rule name {0} as it is scheduled for removal from PMD."
                        + " PMD {1} will remove support for this Rule.",
                    otherRuleSetReferenceId, PMDVersion.getNextMajorRelease());
            }
        }

        RuleSetReference ruleSetReference = new RuleSetReference(otherRuleSetReferenceId.getRuleSetFileName(), false);

        RuleReference ruleReference;
        try {
            ruleReference = new RuleFactory(resourceLoader, languageRegistry).decorateRule(referencedRule, ruleSetReference, ruleNode, err);
        } catch (XmlException e) {
            throw err.at(ruleNode).error(e, "Error while parsing rule reference");
        }

        if (warnDeprecated && ruleReference.isDeprecated() && !isSameRuleSet) {
            err.at(ruleNode).warn(
                "Use Rule name {0}/{1} instead of the deprecated Rule name {2}/{3}. PMD {4}"
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
                    err.at(ruleNode).warn(
                        "The rule {0} is referenced multiple times in ruleset ''{1}''. "
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
            throw new RuleSetLoadException(ruleSetReferenceId, e);
        }

        return found;
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
                                  .includeDeprecatedRuleReferences(includeDeprecatedRuleReferences)
                                  .withReporter(reporter)
                                  .withLanguages(languageRegistry);
    }

    private @NonNull XmlMessageHandler getXmlMessagePrinter() {
        return entry -> {
            Level level = entry.getSeverity() == XmlSeverity.WARNING ? Level.WARN : Level.ERROR;
            String quotedText = StringUtil.quoteMessageFormat(entry.toString());
            reporter.logEx(level, quotedText, new Object[0], entry.getCause());
        };
    }

    private static final class PmdXmlReporterImpl
        extends XmlMessageReporterBase<PmdReporter>
        implements PmdXmlReporter {

        private final PmdReporter pmdReporter;
        private int errCount;

        PmdXmlReporterImpl(PmdReporter pmdReporter, OoxmlFacade ooxml, XmlPositioner positioner) {
            super(ooxml, positioner);
            this.pmdReporter = pmdReporter;
        }

        @Override
        protected PmdReporter create2ndStage(XmlPosition position, XmlPositioner positioner) {
            return new PmdReporter() {
                @Override
                public boolean isLoggable(Level level) {
                    return pmdReporter.isLoggable(level);
                }


                @Override
                public void log(Level level, String message, Object... formatArgs) {
                    logEx(level, message, formatArgs, null);
                }

                @Override
                public void logEx(Level level, String message, Object[] formatArgs, @Nullable Throwable error) {
                    newException(level, error, message, formatArgs);
                }

                @Override
                public XmlException error(@Nullable Throwable cause, @Nullable String contextMessage, Object... formatArgs) {
                    return newException(Level.ERROR, cause, contextMessage, formatArgs);
                }

                @Override
                public XmlException newException(Level level, Throwable cause, String message, Object... formatArgs) {
                    XmlSeverity severity;
                    switch (level) {
                    case WARN:
                        severity = XmlSeverity.WARNING;
                        break;
                    case ERROR:
                        errCount++;
                        severity = XmlSeverity.ERROR;
                        break;
                    default:
                        throw new IllegalArgumentException("unexpected level " + level);
                    }

                    if (message == null && formatArgs.length != 0) {
                        throw new IllegalArgumentException("Cannot pass format arguments for null message");
                    }

                    String actualMessage = message == null ? cause.getMessage()
                                                           : MessageFormat.format(message, formatArgs);
                    NiceXmlMessageSpec spec =
                        new NiceXmlMessageSpec(position, actualMessage)
                            .withSeverity(severity)
                            .withCause(cause);
                    String fullMessage = ooxml.getFormatter().formatSpec(ooxml, spec, positioner);
                    XmlException ex = new XmlException(spec, fullMessage);
                    ooxml.getPrinter().accept(ex); // spec of newException is also to log.
                    return ex;
                }

                @Override
                public int numErrors() {
                    return pmdReporter.numErrors();
                }
            };
        }
    }

}
