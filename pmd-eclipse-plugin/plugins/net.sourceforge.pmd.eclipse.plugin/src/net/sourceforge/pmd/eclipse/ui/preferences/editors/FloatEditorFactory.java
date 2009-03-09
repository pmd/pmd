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
public class FloatEditorFactory extends AbstractRealNumberEditor {

	public static final FloatEditorFactory instance = new FloatEditorFactory();
		
	private FloatEditorFactory() { }
	
    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
        
        return new FloatProperty(
                name, 
                description,
                defaultIn(otherData).floatValue(),
                minimumIn(otherData).floatValue(),
                maximumIn(otherData).floatValue(),
                0.0f
                );
    }
	
    private static FloatProperty floatPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (FloatProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (FloatProperty)desc;
        }
    }
    
    protected Object valueFrom(Control valueControl) {
        
        return new Float(((Spinner)valueControl).getSelection() / scale);
    }
    
	public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {

        final FloatProperty fp = floatPropertyFrom(desc);
        final Spinner spinner = newSpinnerFor(parent, rule, fp);
	            
        spinner.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
               Float newValue = new Float(spinner.getSelection() / scale);
               if (newValue.equals(valueFor(rule, fp))) return;
	                    
               rule.setProperty(fp, newValue);
               listener.changed(rule, fp, newValue);

               adjustRendering(rule, desc, spinner);
               }
           });
	            
        return spinner;
        }
}
