package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Brian Remedios
 */
public interface EditorFactory {

	/**
	 * Return the total number of columns required to position all the widgets
	 * for the editor in a row.
	 * 
	 * @return int
	 */
	int columnsRequired();

	/**
	 * Creates and parks a new editor widget(s) on the parent for the specified descriptor
	 * and rule. It does not perform any layout operations or set form attachments.
	 * 
	 * @param parent Composite
	 * @param columnIndex int
	 * @param desc PropertyDescriptor
	 * @param rule Rule
	 * @param listener ValueChangeListener
	 * @return Control
	 */
	Control newEditorOn(Composite parent, int columnIndex, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener, SizeChangeListener sizeListener);
}
