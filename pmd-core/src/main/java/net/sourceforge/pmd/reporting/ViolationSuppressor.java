/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.AstInfo.SuppressionCommentWrapper;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * An object that suppresses rule violations. Suppressors are used by
 * {@link RuleContext} to filter out violations. In PMD 6.0.x,
 * the {@link Report} object filtered violations itself - but it has
 * no knowledge of language-specific suppressors.
 */
public interface ViolationSuppressor {
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
            Optional<Pattern> regex = rv.getRule().getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR); // Regex
            if (regex.isPresent() && rv.getDescription() != null) {
                if (regex.get().matcher(rv.getDescription()).matches()) {
                    return new SuppressedViolation(rv, this, regex.get().pattern());
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
            Optional<String> xpath = rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
            if (!xpath.isPresent()) {
                return null;
            }
            SaxonXPathRuleQuery rq = new SaxonXPathRuleQuery(
                xpath.get(),
                XPathVersion.DEFAULT,
                rule.getPropertiesByPropertyDescriptor(),
                node.getAstInfo().getLanguageProcessor().services().getXPathHandler(),
                DeprecatedAttrLogger.createForSuppression(rv.getRule())
            );
            if (!rq.evaluate(node).isEmpty()) {
                return new SuppressedViolation(rv, this, xpath.get());
            }
            return null;
        }
    };

    /**
     * Suppressor for regular NOPMD comments.
     *
     * @implNote This requires special support from the language, namely,
     *     the parser must fill-in {@link AstInfo#getSuppressionCommentMap()}.
     */
    ViolationSuppressor NOPMD_COMMENT_SUPPRESSOR = new ViolationSuppressor() {
        private final SimpleDataKey<Set<SuppressionCommentWrapper>> usedSuppressionComments =
            DataMap.simpleDataKey("pmd.core.comment.suppressor");

        @Override
        public String getId() {
            return "//NOPMD";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            AstInfo<? extends RootNode> astInfo = node.getAstInfo();
            Map<Integer, SuppressionCommentWrapper> noPmd = astInfo.getSuppressionCommentMap();
            SuppressionCommentWrapper wrapper = noPmd.get(rv.getBeginLine());
            if (wrapper != null) {
                astInfo.getUserMap().computeIfAbsent(usedSuppressionComments, HashSet::new).add(wrapper);
                return new SuppressedViolation(rv, this, wrapper.getUserMessage());
            }
            return null;
        }

        @Override
        public Set<UnusedSuppressorNode> getUnusedSuppressors(RootNode tree) {
            Set<SuppressionCommentWrapper> usedSuppressors = tree.getAstInfo().getUserMap().getOrDefault(usedSuppressionComments, Collections.emptySet());
            Set<SuppressionCommentWrapper> allSuppressors = tree.getAstInfo().getSuppressionCommentMap().values().stream().collect(CollectionUtil.toMutableSet());
            allSuppressors.removeAll(usedSuppressors);
            return new AbstractSet<UnusedSuppressorNode>() {
                @Override
                public @NonNull Iterator<UnusedSuppressorNode> iterator() {
                    return IteratorUtil.map(
                        allSuppressors.iterator(),
                        comment -> new UnusedSuppressorNode() {
                            @Override
                            public Reportable getLocation() {
                                return comment.getLocation();
                            }

                            @Override
                            public String unusedReason() {
                                return "Unnecessary PMD suppression comment";
                            }
                        }
                    );
                }

                @Override
                public int size() {
                    return allSuppressors.size();
                }
            };
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
     * Return the set of suppressor nodes related to this suppressor
     * that were not used during the analysis.
     * For instance, for an annotation suppressor, the set contains
     * suppressor nodes wrapping annotations.
     * This must be implemented if this suppressor wants to play well
     * with the unused PMD suppression rule.
     *
     * @param tree Root node of a file
     *
     * @return A set
     */
    default Set<UnusedSuppressorNode> getUnusedSuppressors(RootNode tree) {
        return Collections.emptySet();
    }


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


    /**
     * Represents an instance of a "suppressor" that didn't suppress anything.
     * This could be a suppression annotation, or part of an annotation, a
     * comment, etc.
     */
    interface UnusedSuppressorNode {

        Reportable getLocation();

        String unusedReason();
    }
}
