package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;

/**
 */
public interface ValueChangeListener {

	/**
	 * Method changed.
	 * @param desc PropertyDescriptor<?>
	 * @param newValue Object
	 */
	void changed(PropertyDescriptor<?> desc, Object newValue);
}
