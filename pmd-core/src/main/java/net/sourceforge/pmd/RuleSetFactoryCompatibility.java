/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides a simple filter mechanism to avoid failing to parse an old ruleset,
 * which references rules, that have either been removed from PMD already or
 * renamed or moved to another ruleset.
 *
 * @see <a href="https://sourceforge.net/p/pmd/bugs/1360/">issue 1360</a>
 */
final class RuleSetFactoryCompatibility {

    static final RuleSetFactoryCompatibility EMPTY = new RuleSetFactoryCompatibility();
    static final RuleSetFactoryCompatibility DEFAULT = new RuleSetFactoryCompatibility();


    static {
        // PMD 5.3.0
        DEFAULT.addFilterRuleRenamed("java", "design", "UncommentedEmptyMethod", "UncommentedEmptyMethodBody");
        DEFAULT.addFilterRuleRemoved("java", "controversial", "BooleanInversion");

        // PMD 5.3.1
        DEFAULT.addFilterRuleRenamed("java", "design", "UseSingleton", "UseUtilityClass");

        // PMD 5.4.0
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyCatchBlock");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyIfStatement");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyWhileStmt");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyTryBlock");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyFinallyBlock");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptySwitchStatements");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptySynchronizedBlock");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyStatementNotInLoop");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyInitializer");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyStatementBlock");
        DEFAULT.addFilterRuleMoved("java", "basic", "empty", "EmptyStaticInitializer");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryConversionTemporary");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryReturn");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UnnecessaryFinalModifier");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UselessOverridingMethod");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UselessOperationOnImmutable");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UnusedNullCheckInEquals");
        DEFAULT.addFilterRuleMoved("java", "basic", "unnecessary", "UselessParentheses");

        // PMD 5.6.0
        DEFAULT.addFilterRuleRenamed("java", "design", "AvoidConstantsInterface", "ConstantsInInterface");
        // unused/UnusedModifier moved AND renamed, order is important!
        DEFAULT.addFilterRuleMovedAndRenamed("java", "unusedcode", "UnusedModifier", "unnecessary", "UnnecessaryModifier");

        // PMD 6.0.0
        DEFAULT.addFilterRuleMoved("java", "controversial", "unnecessary", "UnnecessaryParentheses");
        DEFAULT.addFilterRuleRenamed("java", "unnecessary", "UnnecessaryParentheses", "UselessParentheses");
        DEFAULT.addFilterRuleMoved("java", "typeresolution", "coupling", "LooseCoupling");
        DEFAULT.addFilterRuleMoved("java", "typeresolution", "clone", "CloneMethodMustImplementCloneable");
        DEFAULT.addFilterRuleMoved("java", "typeresolution", "imports", "UnusedImports");
        DEFAULT.addFilterRuleMoved("java", "typeresolution", "strictexception", "SignatureDeclareThrowsException");
        DEFAULT.addFilterRuleRenamed("java", "naming", "MisleadingVariableName", "MIsLeadingVariableName");
        DEFAULT.addFilterRuleRenamed("java", "unnecessary", "UnnecessaryFinalModifier", "UnnecessaryModifier");
        DEFAULT.addFilterRuleRenamed("java", "empty", "EmptyStaticInitializer", "EmptyInitializer");
        // GuardLogStatementJavaUtil moved and renamed...
        DEFAULT.addFilterRuleMovedAndRenamed("java", "logging-java", "GuardLogStatementJavaUtil", "logging-jakarta-commons", "GuardLogStatement");
        DEFAULT.addFilterRuleRenamed("java", "logging-jakarta-commons", "GuardDebugLogging", "GuardLogStatement");

    }


    private static final Logger LOG = Logger.getLogger(RuleSetFactoryCompatibility.class.getName());

    private final List<RuleSetFilter> filters = new ArrayList<>();

    /**
     * Creates a new instance of the compatibility filter with the built-in
     * filters for the modified PMD rules.
     */
    RuleSetFactoryCompatibility() {

    }

    void addFilterRuleMovedAndRenamed(String language, String oldRuleset, String oldName, String newRuleset, String newName) {
        filters.add(RuleSetFilter.ruleMoved(language, oldRuleset, newRuleset, oldName));
        filters.add(RuleSetFilter.ruleRenamed(language, newRuleset, oldName, newName));
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

    @Nullable String applyRef(String ref) {
        return applyRef(ref, false);
    }


    /**
     * Returns the new rule ref, or null if the rule was deleted. Returns
     * the argument if no replacement is needed.
     *
     * @param ref  Original ref
     * @param warn Whether to output a warning if a replacement is done
     */
    public @Nullable String applyRef(String ref, boolean warn) {
        String result = ref;
        for (RuleSetFilter filter : filters) {
            result = filter.applyRef(result, warn);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    /**
     * Returns the new rule name, or null if the rule was deleted. Returns
     * the argument if no replacement is needed.
     *
     * @param rulesetRef  Ruleset name
     * @param excludeName Original excluded name
     * @param warn        Whether to output a warning if a replacement is done
     */
    public @Nullable String applyExclude(String rulesetRef, String excludeName, boolean warn) {
        String result = excludeName;
        for (RuleSetFilter filter : filters) {
            result = filter.applyExclude(rulesetRef, result, warn);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    private static class RuleSetFilter {

        private static final String MOVED_MESSAGE = "The rule \"{1}\" has been moved from ruleset \"{0}\" to \"{2}\". Please change your ruleset!";
        private static final String RENAMED_MESSAGE = "The rule \"{1}\" has been renamed to \"{3}\". Please change your ruleset!";
        private static final String REMOVED_MESSAGE = "The rule \"{1}\" in ruleset \"{0}\" has been removed from PMD and no longer exists. Please change your ruleset!";
        private final String ruleRef;
        private final String oldRuleset;
        private final String oldName;
        private final String newRuleset;
        private final String newName;
        private final String logMessage;

        private RuleSetFilter(String oldRuleset,
                              String oldName,
                              @Nullable String newRuleset,
                              @Nullable String newName,
                              String logMessage) {
            this.oldRuleset = oldRuleset;
            this.oldName = oldName;
            this.newRuleset = newRuleset;
            this.newName = newName;
            this.logMessage = logMessage;
            this.ruleRef = oldRuleset + "/" + oldName;
        }

        public static RuleSetFilter ruleRenamed(String language, String ruleset, String oldName, String newName) {
            String base = "rulesets/" + language + "/" + ruleset + ".xml";
            return new RuleSetFilter(base, oldName, base, newName, RENAMED_MESSAGE);
        }

        public static RuleSetFilter ruleMoved(String language, String oldRuleset, String newRuleset, String ruleName) {
            String base = "rulesets/" + language + "/";
            return new RuleSetFilter(base + oldRuleset + ".xml", ruleName,
                                     base + newRuleset + ".xml", ruleName,
                                     MOVED_MESSAGE);
        }

        public static RuleSetFilter ruleRemoved(String language, String ruleset, String name) {
            String oldRuleset = "rulesets/" + language + "/" + ruleset + ".xml";
            return new RuleSetFilter(oldRuleset, name,
                                     null, null,
                                     REMOVED_MESSAGE);
        }

        @Nullable String applyExclude(String ref, String name, boolean warn) {
            if (oldRuleset.equals(ref)
                && oldName.equals(name)
                && oldRuleset.equals(newRuleset)) {
                if (warn) {
                    warn();
                }

                return newName;
            }

            return name;
        }

        @Nullable String applyRef(String ref, boolean warn) {

            if (ref.equals(this.ruleRef)) {

                if (warn) {
                    warn();
                }

                if (newName != null) {
                    return newRuleset + "/" + newName;
                } else {
                    // deleted
                    return null;
                }
            }

            return ref;
        }

        private void warn() {
            if (LOG.isLoggable(Level.WARNING)) {
                String log = MessageFormat.format(logMessage, oldRuleset, oldName, newRuleset, newName);
                LOG.warning("Applying rule set filter: " + log);
            }
        }
    }
}
