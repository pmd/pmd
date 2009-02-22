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
public class FloatEditorFactory extends AbstractNumericEditorFactory {

	public static final FloatEditorFactory instance = new FloatEditorFactory();
		
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

            final FloatProperty fp = floatPropertyFrom(desc);
		    final Spinner spinner = newSpinnerFor(parent, rule, fp);
		    
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
