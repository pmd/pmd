/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolation;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;

public class XmlRuleViolation extends AbstractRuleViolation {
    public XmlRuleViolation(Rule rule, RuleContext ctx, XmlNode node) {
	super(rule, ctx, node);
    }

    public XmlRuleViolation(Rule rule, RuleContext ctx, XmlNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);
    }
}
