package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;

/**
 * 
 * @author Brian Remedios
 */
public class BasicRuleFieldAccessor implements RuleFieldAccessor {

    public BasicRuleFieldAccessor() {
    }

    public String labelFor(Rule rule) {
       Comparable<?> value = valueFor(rule);
       return value == null ? "" : value.toString();
    }

    public Comparable<?> valueFor(Rule rule) {
        throw new RuntimeException("unimplemented method");
    }
}
