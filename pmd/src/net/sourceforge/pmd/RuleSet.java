package net.sourceforge.pmd;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public class RuleSet
{
    private Set rules = new HashSet();
    private String name;
    private String description;

    public RuleSet() { }

    public int size() {
	return rules.size();
    }

    public void addRule( Rule rule ) {
 	rules.add( rule );
    }

    public Set getRules() {
	return rules;
    }

    public Rule getRuleByName(String ruleName) {
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule r = (Rule)i.next();
            if (r.getName().equals(ruleName)) {
                return r;
            }
        }
        throw new RuntimeException("Couldn't find rule named " + ruleName + " in the ruleset " + name);
    }

    public void addRuleSet(RuleSet ruleSet) {
        rules.addAll(ruleSet.getRules());
    }

    public void apply( List acuList,
		       RuleContext ctx ) {
	Iterator rs = rules.iterator();
	while (rs.hasNext()) {
	    Rule rule = (Rule) rs.next();

	    rule.apply( acuList, ctx );
	}
    }

    public void applyToFiles(List files, RuleContext ctx) {
        Iterator rs = rules.iterator();
        while (rs.hasNext()) {
            Rule rule = (Rule) rs.next();
            rule.applyToFiles( files, ctx );
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
