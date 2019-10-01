package net.sourceforge.pmd;

import java.util.Map;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * An object that suppresses rule violations. Suppressors are used by
 * {@link RuleViolationFactory} to filter out violations. In PMD 6.0.x,
 * the {@link Report} object filtered violations itself - but it has
 * no knowledge of language-specific suppressors.
 */
public interface ViolationSuppressor {

    /**
     * Suppressor for the violationSuppressRegex property.
     */
    ViolationSuppressor REGEX_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String id() {
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
        public String id() {
            return "XPath";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            String xpath = rv.getRule().getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
            if (xpath != null && node.hasDescendantMatchingXPath(xpath)) {
                return new SuppressedViolation(rv, this, xpath);
            }
            return null;
        }
    };

    /**
     * Suppressor for regular NOPMD comments.
     *
     * @implNote This requires special support from the language, namely
     *     an implementation of {@link RootNode#getNoPmdComments()}.
     *
     * - TODO this could replace the ParserOptions + RootNode#getNoPmdComments
     *    Just make a custom suppressor with constructor params
     *     * the suppress marker
     *     * a lang-specific strategy to get the comments
     *
     *    This would require the service architecture I'd like to put forward.
     */
    ViolationSuppressor NOPMD_COMMENT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String id() {
            return "//NOPMD";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            Map<Integer, String> noPmd = node.getRoot().getNoPmdComments();
            if (noPmd.containsKey(rv.getBeginLine())) {
                return new SuppressedViolation(rv, this, noPmd.get(rv.getBeginLine()));
            }
            return null;
        }
    };


    /**
     * A name, for reporting and documentation purposes.
     */
    String id();


    /**
     * Returns a {@link SuppressedViolation} if the given violation is
     * suppressed by this object. The node and the rule are provided
     * for context. Returns null if the violation is not suppressed.
     */
    @Nullable
    SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node);


}
