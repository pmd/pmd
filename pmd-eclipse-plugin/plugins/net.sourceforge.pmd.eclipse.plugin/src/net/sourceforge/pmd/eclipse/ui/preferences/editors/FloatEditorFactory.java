package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * @author Brian Remedios
 */
public class FloatEditorFactory extends AbstractEditorFactory {

	public static final FloatEditorFactory instance = new FloatEditorFactory();
	
	private static final int digits = 3;
	private static final double scale = Math.pow(10, digits);
	
	private FloatEditorFactory() { }

    private static FloatProperty floatPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (FloatProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (FloatProperty)desc;
        }
    }
	
	public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);
		
		if (columnIndex == 1) {
			
		    final Spinner spinner = new Spinner(parent, SWT.SINGLE | SWT.BORDER);
	           	         			
			double value = ((Number)rule.getProperty(desc)).doubleValue();

			final FloatProperty fp = floatPropertyFrom(desc);
			
			spinner.setDigits(digits);
			spinner.setMinimum((int)(fp.lowerLimit().doubleValue() * scale));
            spinner.setMaximum((int)(fp.upperLimit().doubleValue() * scale));
            spinner.setSelection((int)(value * scale));
			
			spinner.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
					float newValue = (float)(spinner.getSelection() / scale);
					if (newValue == rule.getProperty(fp)) return;
					
					rule.setProperty(fp, newValue);
					listener.changed(rule, fp, newValue);
				}
			});
			
			return spinner;
		}
		
		return null;
	}

}
