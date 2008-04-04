package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;

/**
 * The RuleChain is a means by which Rules can participate in a uniform
 * visitation of the AST, and not need perform their own independent visitation.
 * The RuleChain exists as a means to improve the speed of PMD when there are
 * many Rules.
 */
// FUTURE Can this class be eliminated or reworked based upon LanguageVersionHandler?
public class RuleChain {
    // Mapping from Language to RuleChainVisitor
    private final Map<Language, RuleChainVisitor> languageToRuleChainVisitor = new HashMap<Language, RuleChainVisitor>();

    /**
     * Add all Rules from the given RuleSet which want to participate in the
     * RuleChain.
     * 
     * @param ruleSet
     *            The RuleSet to add Rules from.
     */
    public void add(RuleSet ruleSet) {
	Language language = ruleSet.getLanguage();
	for (Rule r : ruleSet.getRules()) {
	    add(language, r);
	}
    }

    /**
     * Add the given Rule if it wants to participate in the RuleChain.
     * 
     * @param language
     *            The Language used by the Rule.
     * @param rule
     *            The Rule to add.
     */
    public void add(Language language, Rule rule) {
	RuleChainVisitor visitor = getRuleChainVisitor(language);
	if (visitor != null) {
	    visitor.add(rule);
	}
    }

    /**
     * Apply the RuleChain to the given Nodes using the given
     * RuleContext, for those rules using the given Language.
     * 
     * @param nodes
     *            The Nodes.
     * @param ctx
     *            The RuleContext.
     * @param language
     *            The Language.
     */
    public void apply(List<Node> nodes, RuleContext ctx, Language language) {
	RuleChainVisitor visitor = getRuleChainVisitor(language);
	if (visitor != null) {
	    visitor.visitAll(nodes, ctx);
	}
    }

    // Get the RuleChainVisitor for the appropriate Language.
    private RuleChainVisitor getRuleChainVisitor(Language language) {
	if (language == null) {
	    language = Language.JAVA;
	}
	RuleChainVisitor visitor = languageToRuleChainVisitor.get(language);
	if (visitor == null) {
	    if (Language.JAVA.equals(language)) {
		visitor = new JavaRuleChainVisitor();
	    } else if (Language.JSP.equals(language)) {
		visitor = new JspRuleChainVisitor();
	    } else {
		throw new IllegalArgumentException("Unknown language: " + language);
	    }
	    languageToRuleChainVisitor.put(language, visitor);
	}
	return visitor;
    }
}
