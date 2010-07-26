package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;

/**
 * Accepts notifications for changes to a single rule or a number of them.
 * 
 * @author BrianRemedios
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
