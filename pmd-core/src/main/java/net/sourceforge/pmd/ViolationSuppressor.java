package net.sourceforge.pmd;

import java.util.Map;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Generic rule violation suppressor.
 */
public interface ViolationSuppressor {

    String NOPMD_COMMENT_ID = "//NOPMD";


    ViolationSuppressor REGEX_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String id() {
            return "Regex";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, Node node, Rule rule) {
            String regex = rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR); // Regex
            if (regex != null && rv.getDescription() != null) {
                if (Pattern.matches(regex, rv.getDescription())) {
                    return new SuppressedViolation(rv, this, regex);
                }
            }
            return null;
        }
    };

    ViolationSuppressor XPATH_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String id() {
            return "XPath";
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, Node node, Rule rule) {
            String xpath = rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
            if (xpath != null && node.hasDescendantMatchingXPath(xpath)) {
                return new SuppressedViolation(rv, this, xpath);
            }
            return null;
        }
    };

    ViolationSuppressor NOPMD_COMMENT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String id() {
            return NOPMD_COMMENT_ID;
        }

        @Override
        public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, Node node, Rule rule) {
            Map<Integer, String> noPmd = node.getRoot().getNoPmdComments();
            if (noPmd.containsKey(rv.getBeginLine())) {
                return new SuppressedViolation(rv, this, noPmd.get(rv.getBeginLine()));
            }
            return null;
        }
    };

    String id();

    @Nullable
    SuppressedViolation suppressOrNull(RuleViolation rv, Node node, Rule rule);


}
