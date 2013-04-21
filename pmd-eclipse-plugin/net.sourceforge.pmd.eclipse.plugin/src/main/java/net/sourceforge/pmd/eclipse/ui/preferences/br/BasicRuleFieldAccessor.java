package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.Iterator;
import java.util.Set;

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
    
	public Comparable<?> valueFor(RuleCollection collection) {
		Comparable<?> aspect = RuleUtil.commonAspect(collection, this);
		if (aspect != null) return aspect;
		return asString( RuleUtil.uniqueAspects(collection, this) );
	}
    
    public Set<Comparable<?>> uniqueValuesFor(RuleCollection collection) {
		return RuleUtil.uniqueAspects(collection, this);
    }
    
	protected String format(Object item) {
		return item == null ? "" : String.valueOf(item);
	}
	
	protected String asString(Set<Comparable<?>> items) {
		
		Iterator<Comparable<?>> iter = items.iterator();
		if (items.size() == 1) return format(iter.next());
		
		StringBuilder sb = new StringBuilder(format(iter.next()));
		while (iter.hasNext()) {
			sb.append(", ").append(format(iter.next()));
		}
		return sb.toString();
	}
}
