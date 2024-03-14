/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static boolean ruleSetApplies(Rule rule, LanguageVersion languageVersion) {
        return RuleSet.applies(rule, languageVersion);
    }

    public static boolean ruleSetApplies(RuleSet ruleSet, FileId fileId) {
        return ruleSet.applies(fileId);
    }

    public static List<RuleSet> loadRuleSetsWithoutException(RuleSetLoader ruleSetLoader, List<String> rulesetPaths) {
        return ruleSetLoader.loadRuleSetsWithoutException(rulesetPaths);
    }

    public static RuleSetLoader withReporter(RuleSetLoader ruleSetLoader, @NonNull PmdReporter reporter) {
        return ruleSetLoader.withReporter(reporter);
    }
}
