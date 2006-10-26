package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Grouping of Rules per Language in a RuleSet.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class RuleSets {
    /**
     * Map of RuleLanguage on RuleSet.
     */
    private Collection ruleSets = new ArrayList();

    /**
     * Public constructor.
     */
    public RuleSets() {
    }

    /**
     * Public constructor. Add the given rule set.
     *
     * @param ruleSet the RuleSet
     */
    public RuleSets(RuleSet ruleSet) {
        this();
        addRuleSet(ruleSet);
    }

    /**
     * Add a ruleset for a language. Only one ruleset can be added for a specific
     * language. If ruleSet.getLanguage() is null, it is assumed to be a RuleSet of java
     * rules.
     *
     * @param ruleSet the RuleSet
     */
    public void addRuleSet(RuleSet ruleSet) {
        ruleSets.add(ruleSet);
    }

    /**
     * Get all the RuleSets.
     *
     * @return RuleSet[]
     */
    public RuleSet[] getAllRuleSets() {
        return (RuleSet[]) ruleSets.toArray(new RuleSet[ruleSets.size()]);
    }

    public Iterator getRuleSetsIterator() {
        return ruleSets.iterator();
    }

    /**
     * Return all rules from all rulesets.
     *
     * @return Set
     */
    public Set getAllRules() {
        HashSet result = new HashSet();
        for (Iterator i = ruleSets.iterator(); i.hasNext();) {
            result.addAll(((RuleSet) i.next()).getRules());
        }
        return result;
    }

    /**
     * Check if a source with given language should be checked by rules for a given
     * language. This is the case if both languages are equal, or if the source is in
     * java, and the language of the rules is unknown (for backward-compatibility
     * reasons).
     *
     * @param languageOfSource language of a source; can not be null
     * @param languageOfRule   language of a ruleset; can be null
     * @return  boolean true if the rule applies, else false
     */
    public boolean applies(Language languageOfSource, Language languageOfRule) {
        return (languageOfSource.equals(languageOfRule) || (languageOfSource
                .equals(Language.JAVA) && (null == languageOfRule)));
    }

    /**
     * Apply all applicable rules to the compilation units.
     * Applicable means the language of the rules must match the language
     * of the source (@see applies).
     *
     * @param acuList  the List of compilation units; the type these must have,
     *                 depends on the source language
     * @param ctx      the RuleContext
     * @param language the Language of the source
     */
    public void apply(List acuList, RuleContext ctx, Language language) {
        for (Iterator i = ruleSets.iterator(); i.hasNext();) {
            RuleSet ruleSet = (RuleSet) i.next();
            if (applies(language, ruleSet.getLanguage())) {
                ruleSet.apply(acuList, ctx);
            }
        }
    }

    /**
     * Check if the rules that apply to a source of the given language
     * use DFA.
     *
     * @param language the language of a source
     * @return true if any rule in the RuleSet needs the DFA layer
     */
    public boolean usesDFA(Language language) {
        for (Iterator i = ruleSets.iterator(); i.hasNext();) {
            RuleSet ruleSet = (RuleSet) i.next();
            if (applies(language, ruleSet.getLanguage()) && ruleSet.usesDFA()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Rule with the given name
     *
     * @param ruleName the name of the rule to find
     * @return the rule or null if not found
     */
    public Rule getRuleByName(String ruleName) {
        Rule rule = null;
        for (Iterator i = ruleSets.iterator(); i.hasNext() && (rule == null);) {
            RuleSet ruleSet = (RuleSet) i.next();
            rule = ruleSet.getRuleByName(ruleName);
        }
        return rule;
    }

	public boolean usesTypeResolution(Language language) {
		for (Iterator i = ruleSets.iterator(); i.hasNext();) {
			RuleSet ruleSet = (RuleSet) i.next();
			if (applies(language, ruleSet.getLanguage()) && ruleSet.usesTypeResolution()) {
				return true;
			}
		}
		return false;
	}
}
