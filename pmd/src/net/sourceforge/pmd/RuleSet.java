/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RuleSet {

    private Set rules = new HashSet();
    private String name;
    private String description;

    public int size() {
        return rules.size();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public Set getRules() {
        return rules;
    }

    /**
     * @return true if any rule in the RuleSet needs the symbol table
     */
    public boolean usesSymbolTable() {
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule r = (Rule) i.next();
            if (r.usesSymbolTable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if any rule in the RuleSet needs the DFA layer
     */
    public boolean usesDFA() {
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule r = (Rule) i.next();
            if (r.usesDFA()) {
                return true;
            }
        }
        return false;
    }

    public Rule getRuleByName(String ruleName) {
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule r = (Rule) i.next();
            if (r.getName().equals(ruleName)) {
                return r;
            }
        }
        throw new RuntimeException("Couldn't find rule named " + ruleName + " in the ruleset " + name);
    }

    public void addRuleSet(RuleSet ruleSet) {
        rules.addAll(ruleSet.getRules());
    }

    public void apply(List acuList, RuleContext ctx) {
        Iterator rs = rules.iterator();
        while (rs.hasNext()) {
            Rule rule = (Rule) rs.next();
            rule.apply(acuList, ctx);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
