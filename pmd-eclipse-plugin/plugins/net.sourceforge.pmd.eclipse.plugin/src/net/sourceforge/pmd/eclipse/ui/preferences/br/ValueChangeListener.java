package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;

/**
 */
public interface ValueChangeListener {

	/**
	 * Method changed.
	 * @param rule RuleSelection
	 * @param desc PropertyDescriptor<?>
	 * @param newValue Object
	 */
	void changed(RuleSelection rule, PropertyDescriptor<?> desc, Object newValue);
	
	   /**
     * Method changed.
     * @param rule Rule
     * @param desc PropertyDescriptor<?>
     * @param newValue Object
     */
    void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue);
}
