package net.sourceforge.pmd;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public class RuleSet
{
    private Set rules = new HashSet();
    private String name;

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

    public void apply( List acuList,
		       RuleContext ctx ) {
	Iterator rs = rules.iterator();
	while (rs.hasNext()) {
	    Rule rule = (Rule) rs.next();

	    rule.apply( acuList, ctx );
	}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
