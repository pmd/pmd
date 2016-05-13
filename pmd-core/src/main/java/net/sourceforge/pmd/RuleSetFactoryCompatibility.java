/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Provides a simple filter mechanism to avoid failing to parse an old ruleset, which references rules, that
 * have either been removed from PMD already or renamed or moved to another ruleset.
 *
 * @see <a href="https://sourceforge.net/p/pmd/bugs/1360/">issue 1360</a>
 */
public class RuleSetFactoryCompatibility {
    private static final Logger LOG = Logger.getLogger(RuleSetFactoryCompatibility.class.getName());

    private List<RuleSetFilter> filters = new LinkedList<RuleSetFilter>();

    /**
     * Creates a new instance of the compatibility filter with the built-in filters for the
     * modified PMD rules.
     */
    public RuleSetFactoryCompatibility() {
        // PMD 5.3.0
        addFilterRuleRenamed("java", "design", "UncommentedEmptyMethod", "UncommentedEmptyMethodBody");
        addFilterRuleRemoved("java", "controversial", "BooleanInversion");

        // PMD 5.3.1
        addFilterRuleRenamed("java", "design", "UseSingleton", "UseUtilityClass");

        // PMD 5.4.0
        addFilterRuleMoved("java", "basic", "empty", "EmptyCatchBlock");
        addFilterRuleMoved("java", "basic", "empty", "EmptyIfStatement");
        addFilterRuleMoved("java", "basic", "empty", "EmptyWhileStmt");
        addFilterRuleMoved("java", "basic", "empty", "EmptyTryBlock");
        addFilterRuleMoved("java", "basic", "empty", "EmptyFinallyBlock");
        addFilterRuleMoved("java", "basic", "empty", "EmptySwitchStatements");
        addFilterRuleMoved("java", "basic", "empty", "EmptySynchronizedBlock");
        addFilterRuleMoved("java", "basic", "empty", "EmptyStatementNotInLoop");
        addFilterRuleMoved("java", "basic", "empty", "EmptyInitializer");
        addFilterRuleMoved("java", "basic", "empty", "EmptyStatementBlock");
        addFilterRuleMoved("java", "basic", "empty", "EmptyStaticInitializer");
        addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryConversionTemporary");
        addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryReturn");
        addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryFinalModifier");
        addFilterRuleMoved("java", "basic", "unnecessary", "UselessOverridingMethod");
        addFilterRuleMoved("java", "basic", "unnecessary", "UselessOperationOnImmutable");
        addFilterRuleMoved("java", "basic", "unnecessary", "UnusedNullCheckInEquals");
        addFilterRuleMoved("java", "basic", "unnecessary", "UselessParentheses");
    }

    void addFilterRuleRenamed(String language, String ruleset, String oldName, String newName) {
        filters.add(RuleSetFilter.ruleRenamed(language, ruleset, oldName, newName));
    }
    void addFilterRuleMoved(String language, String oldRuleset, String newRuleset, String ruleName) {
        filters.add(RuleSetFilter.ruleMoved(language, oldRuleset, newRuleset, ruleName));
    }
    void addFilterRuleRemoved(String language, String ruleset, String name) {
        filters.add(RuleSetFilter.ruleRemoved(language, ruleset, name));
    }

    /**
     * Applies all configured filters against the given input stream.
     * The resulting reader will contain the original ruleset modified by
     * the filters.
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public Reader filterRuleSetFile(InputStream stream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(stream);
        String encoding = determineEncoding(bytes);
        String ruleset = new String(bytes, encoding);

        ruleset = applyAllFilters(ruleset);

        return new StringReader(ruleset);
    }

    private String applyAllFilters(String in) {
        String result = in;
        for (RuleSetFilter filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }

    private static final Pattern ENCODING_PATTERN = Pattern.compile("encoding=\"([^\"]+)\"");
    /**
     * Determines the encoding of the given bytes, assuming this is a XML document, which specifies
     * the encoding in the first 1024 bytes.
     * 
     * @param bytes the input bytes, might be more or less than 1024 bytes
     * @return the determined encoding, falls back to the default UTF-8 encoding
     */
    String determineEncoding(byte[] bytes) {
        String firstBytes = new String(bytes, 0, bytes.length > 1024 ? 1024 : bytes.length, Charset.forName("ISO-8859-1"));
        Matcher matcher = ENCODING_PATTERN.matcher(firstBytes);
        String encoding = Charset.forName("UTF-8").name();
        if (matcher.find()) {
            encoding = matcher.group(1);
        }
        return encoding;
    }

    private static class RuleSetFilter {
        private final Pattern refPattern;
        private final String replacement;
        private Pattern exclusionPattern;
        private String exclusionReplacement;
        private final String logMessage;
        private RuleSetFilter(String refPattern, String replacement, String logMessage) {
            this.logMessage = logMessage;
            if (replacement != null) {
                this.refPattern = Pattern.compile("ref=\"" + Pattern.quote(refPattern) + "\"");
                this.replacement = "ref=\"" + replacement + "\"";
            } else {
                this.refPattern = Pattern.compile("<rule\\s+ref=\"" + Pattern.quote(refPattern) + "\"\\s*/>");
                this.replacement = "";
            }
        }

        private void setExclusionPattern(String oldName, String newName) {
            exclusionPattern = Pattern.compile("<exclude\\s+name=[\"']" + Pattern.quote(oldName) + "[\"']\\s*/>");
            if (newName != null) {
                exclusionReplacement = "<exclude name=\"" + newName + "\" />";
            } else {
                exclusionReplacement = "";
            }
        }

        public static RuleSetFilter ruleRenamed(String language, String ruleset, String oldName, String newName) {
            String base = "rulesets/" + language + "/" + ruleset + ".xml/";
            RuleSetFilter filter = new RuleSetFilter(base + oldName, base + newName,
                    "The rule \"" + oldName + "\" has been renamed to \"" + newName + "\". Please change your ruleset!");
            filter.setExclusionPattern(oldName, newName);
            return filter;
        }
        public static RuleSetFilter ruleMoved(String language, String oldRuleset, String newRuleset, String ruleName) {
            String base = "rulesets/" + language + "/";
            return new RuleSetFilter(base + oldRuleset + ".xml/" + ruleName, base + newRuleset + ".xml/" + ruleName,
                    "The rule \"" + ruleName + "\" has been moved from ruleset \"" + oldRuleset + "\" to \"" + newRuleset + "\". Please change your ruleset!");
        }
        public static RuleSetFilter ruleRemoved(String language, String ruleset, String name) {
            RuleSetFilter filter = new RuleSetFilter("rulesets/" + language + "/" + ruleset + ".xml/" + name, null,
                    "The rule \"" + name + "\" in ruleset \"" + ruleset + "\" has been removed from PMD and no longer exists. Please change your ruleset!");
            filter.setExclusionPattern(name, null);
            return filter;
        }

        String apply(String in) {
            String result = in;
            Matcher matcher = refPattern.matcher(in);

            if (matcher.find()) {
                result = matcher.replaceAll(replacement);

                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Applying rule set filter: " + logMessage);
                }
            }

            if (exclusionPattern == null) return result;

            Matcher exclusions = exclusionPattern.matcher(result);
            if (exclusions.find()) {
                result = exclusions.replaceAll(exclusionReplacement);

                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning("Applying rule set filter for exclusions: " + logMessage);
                }
            }

            return result;
        }
    }
}
