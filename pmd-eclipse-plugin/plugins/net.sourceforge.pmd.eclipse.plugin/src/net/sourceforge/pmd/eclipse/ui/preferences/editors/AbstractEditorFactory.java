package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractEditorFactory implements EditorFactory {
	
	protected AbstractEditorFactory() { }

	/**
	 * @return int
	 * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#columnsRequired()
	 */
	public int columnsRequired() { return 2; };
	
	/**
	 * Method addLabel.
	 * @param parent Composite
	 * @param desc PropertyDescriptor
	 * @return Label
	 */
	protected Label addLabel(Composite parent, PropertyDescriptor<?> desc) {
		
		Label label = new Label(parent, SWT.NONE);
		label.setText(desc.description());
		GridData data = new GridData();
		data.horizontalAlignment = SWT.LEFT;
		data.verticalAlignment = SWT.CENTER;  // CENTER is preferred only when showing a single row value widget...hmm
		label.setLayoutData(data);
		return label;
	}
	
	/**
	 * Return the value as a string that can be easily recognized and parsed
	 * when we see it again.
	 *
	 * @param value Object
	 * @return String
	 */
	protected static String asString(Object value) {
		return value == null ? "" : value.toString();
	}
	
	/**
	 * Adjust the display of the control to denote whether it holds 
	 * onto the default value or not.
	 * 
	 * @param control
	 * @param hasDefaultValue
	 */
	protected void adjustRendering(Control control, boolean hasDefaultValue) {
	    
	    Display display = control.getDisplay();
	    
	    control.setBackground( 
	       display.getSystemColor(hasDefaultValue ? SWT.COLOR_WHITE : SWT.COLOR_CYAN) 
	       );
	}
	
	/**
	 * Return the specified values as a single string using the delimiter.
	 * @param values Object
	 * @param delimiter char
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	public static String asDelimitedString(Object values, char delimiter) {
		
		if (values == null) {
		    return "";
		}
	
		if (values instanceof Object[]) {
			Object[] valueSet = (Object[])values;
			if (valueSet.length == 0) {
			    return "";
			}
			if (valueSet.length == 1) {
			    return asString(valueSet[0]);
			}
	
			StringBuilder sb = new StringBuilder(asString(valueSet[0]));
			for (int i=1; i<valueSet.length; i++) {
				sb.append(delimiter).append(asString(valueSet[i]));
			}
			return sb.toString();
			}
	
		return asString(values);
	}
}
