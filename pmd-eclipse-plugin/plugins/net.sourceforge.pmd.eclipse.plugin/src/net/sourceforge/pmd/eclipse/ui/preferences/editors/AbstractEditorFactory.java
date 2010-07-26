package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.AbstractRulePanelManager;
import net.sourceforge.pmd.eclipse.util.ColourManager;
import net.sourceforge.pmd.lang.rule.RuleReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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

    protected static ColourManager  colourManager;
    protected static Color          overriddenColour;

	protected AbstractEditorFactory() { }

	private static ColourManager colourManager() {

	    if (colourManager != null) return colourManager;
	    colourManager = ColourManager.managerFor(Display.getCurrent());
	    return colourManager;
	}

	private Color overriddenColour() {

	    if (overriddenColour != null) return overriddenColour;

	    overriddenColour = colourManager().colourFor(AbstractRulePanelManager.overridenColourValues);
	    return overriddenColour;
	}

	protected Label newLabel(Composite parent, String text) {
	    Label label = new Label(parent, SWT.None);
	    label.setText(text);
	    return label;
	}

	/**
	 * Generic control that provides a label/widget pair for the default value. Subclasses can override this to provide additional
	 * labels & widgets as necessary but must be able to extract the values held by them when the property is created.
	 */
	public Control[] createOtherControlsOn(Composite parent, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener, SizeChangeListener sizeListener) {
	    return new Control[] {
	        newLabel(parent,"Default"),
	        newEditorOn(parent, desc, rule, listener, sizeListener)
	        };
	}

	protected abstract Object valueFrom(Control valueControl);

    protected Object valueFor(Rule rule, PropertyDescriptor<?> desc) {

        return rule.hasDescriptor(desc) ?
                rule.getProperty(desc) :
                desc.defaultValue();
    }

	/**
	 * Method addLabel.
	 * @param parent Composite
	 * @param desc PropertyDescriptor
	 * @return Label
	 */
	public Label addLabel(Composite parent, PropertyDescriptor<?> desc) {

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

	protected void adjustRendering(Rule rule, PropertyDescriptor<?> desc, Control control) {

	        if (!(rule instanceof RuleReference)) return;

	        boolean isOverridden = ((RuleReference)rule).hasOverriddenProperty(desc);
	        Display display = control.getDisplay();
	        Color clr = isOverridden ? overriddenColour() : display.getSystemColor(SWT.COLOR_WHITE);

	        control.setBackground(clr);
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
