package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;

/**
 *
 * @author Brian Remedios
 */
public class BasicRuleSetFieldAccessor implements RuleSetFieldAccessor {

    public BasicRuleSetFieldAccessor() {
    }

    public String labelFor(RuleSet ruleSet) {
       Comparable<?> value = valueFor(ruleSet);
       return value == null ? "" : value.toString();
    }

    public Comparable<?> valueFor(RuleSet ruleSet) {
        throw new RuntimeException("unimplemented method");
    }
    
//	public Comparable<?> valueFor(RuleSetCollection collection) {
//		Comparable<?> aspect = RuleUtil.commonAspect(collection, this);
//		if (aspect != null) return aspect;
//		return asString( RuleUtil.uniqueAspects(collection, this) );
//	}
//    
//    public Set<Comparable<?>> uniqueValuesFor(RuleSet/Collection collection) {
//		return RuleUtil.uniqueAspects(collection, this);
//    }
    
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
