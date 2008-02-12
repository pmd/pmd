package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.ast.CompilationUnit;

/**
 * Grouping of Rules per Language in a RuleSet.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class RuleSets {
    /**
     * Map of RuleLanguage on RuleSet.
     */
    private Collection<RuleSet> ruleSets = new ArrayList<RuleSet>();

    /**
     * RuleChain for efficient AST visitation.
     */
    private RuleChain ruleChain = new RuleChain();

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
        ruleChain.add(ruleSet);
    }

    /**
     * Get all the RuleSets.
     *
     * @return RuleSet[]
     */
    public RuleSet[] getAllRuleSets() {
        return ruleSets.toArray(new RuleSet[ruleSets.size()]);
    }

    public Iterator<RuleSet> getRuleSetsIterator() {
        return ruleSets.iterator();
    }

    /**
     * Return all rules from all rulesets.
     *
     * @return Set
     */
    public Set<Rule> getAllRules() {
        HashSet<Rule> result = new HashSet<Rule>();
        for (RuleSet r: ruleSets) {
            result.addAll(r.getRules());
        }
        return result;
    }
    
    /**
     * Check if a given source file should be checked by rules in this RuleSets.
     * 
     * @param file the source file to check
     * @return <code>true</code> if the file should be checked, <code>false</code> otherwise
     */
    public boolean applies(File file) {
        for (Iterator i = ruleSets.iterator(); i.hasNext();) {
            if (((RuleSet)i.next()).applies(file)) {
                return true;
            }
        }
        return false;
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
     * Notify all rules of the start of processing.
     */
    public void start(RuleContext ctx) {
        for (RuleSet ruleSet: ruleSets) {
        	ruleSet.start(ctx);
        }
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
    public void apply(List<CompilationUnit> acuList, RuleContext ctx, Language language) {
        ruleChain.apply(acuList, ctx, language);
        for (RuleSet ruleSet: ruleSets) {
            if (applies(language, ruleSet.getLanguage())) {
                ruleSet.apply(acuList, ctx);
            }
        }
    }
    
    /**
     * Notify all rules of the end of processing.
     */
    public void end(RuleContext ctx) {
        for (RuleSet ruleSet: ruleSets) {
        	ruleSet.end(ctx);
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
        for (RuleSet ruleSet: ruleSets) {
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
        for (Iterator<RuleSet> i = ruleSets.iterator(); i.hasNext() && (rule == null);) {
            RuleSet ruleSet = i.next();
            rule = ruleSet.getRuleByName(ruleName);
        }
        return rule;
    }

	public boolean usesTypeResolution(Language language) {
		for (RuleSet ruleSet: ruleSets) {
			if (applies(language, ruleSet.getLanguage()) && ruleSet.usesTypeResolution()) {
				return true;
			}
		}
		return false;
	}
}
