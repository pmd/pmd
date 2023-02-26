/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;

/**
 * An object that suppresses rule violations. Suppressors are used by
 * {@link RuleContext} to filter out violations. In PMD 6.0.x,
 * the {@link Report} object filtered violations itself - but it has
 * no knowledge of language-specific suppressors.
 */
public interface ViolationSuppressor {
    // todo move to package reporting

    /**
     * Suppressor for the violationSuppressRegex property.
     */
    ViolationSuppressor REGEX_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "Regex";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            String regex = rv.getRule().getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR); // Regex
            if (regex != null && rv.getDescription() != null) {
                if (Pattern.matches(regex, rv.getDescription())) {
                    return new SuppressedViolation(rv, this, regex);
                }
            }
            return null;
        }
    };

    /**
     * Suppressor for the violationSuppressXPath property.
     */
    ViolationSuppressor XPATH_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "XPath";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            // todo this should not be implemented via a rule property
            //  because the parsed xpath expression should be stored, not a random string
            //  this needs to be checked to be a valid xpath expression in the ruleset,
            //  not at the time it is evaluated, and also parsed by the XPath parser only once
            Rule rule = rv.getRule();
            String xpath = rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
            if (xpath == null) {
                return null;
            }
            SaxonXPathRuleQuery rq = new SaxonXPathRuleQuery(
                xpath,
                XPathVersion.DEFAULT,
                rule.getPropertiesByPropertyDescriptor(),
                node.getAstInfo().getLanguageProcessor().services().getXPathHandler(),
                DeprecatedAttrLogger.createForSuppression(rv.getRule())
            );
            if (!rq.evaluate(node).isEmpty()) {
                return new SuppressedViolation(rv, this, xpath);
            }
            return null;
        }
    };

    /**
     * Suppressor for regular NOPMD comments.
     *
     * @implNote This requires special support from the language, namely,
     *     the parser must fill-in {@link AstInfo#getSuppressionComments()}.
     */
    ViolationSuppressor NOPMD_COMMENT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "//NOPMD";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            Map<Integer, String> noPmd = node.getAstInfo().getSuppressionComments();
            if (noPmd.containsKey(rv.getBeginLine())) {
                return new SuppressedViolation(rv, this, noPmd.get(rv.getBeginLine()));
            }
            return null;
        }
    };


    /**
     * A name, for reporting and documentation purposes.
     */
    String getId();


    /**
     * Returns a {@link SuppressedViolation} if the given violation is
     * suppressed by this object. The node and the rule are provided
     * for context. Returns null if the violation is not suppressed.
     */
    @Nullable
    SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node);


    /**
     * Apply a list of suppressors on the violation. Returns the violation
     * of the first suppressor that matches the input violation. If no
     * suppressor matches, then returns null.
     */
    static @Nullable SuppressedViolation suppressOrNull(List<ViolationSuppressor> suppressorList,
                                                        RuleViolation rv,
                                                        Node node) {
        for (ViolationSuppressor suppressor : suppressorList) {
            SuppressedViolation suppressed = suppressor.suppressOrNull(rv, node);
            if (suppressed != null) {
                return suppressed;
            }
        }
        return null;
    }
}
