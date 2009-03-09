package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * @author Brian Remedios
 */
public class IntegerEditorFactory extends AbstractNumericEditorFactory {

	public static final IntegerEditorFactory instance = new IntegerEditorFactory();
	
	private IntegerEditorFactory() { }
	
    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
        
        return new IntegerProperty(
                name, 
                description,
                minimumIn(otherData).intValue(),
                maximumIn(otherData).intValue(),
                defaultIn(otherData).intValue(),
                0.0f
                );
    } 
	
    protected Object valueFrom(Control valueControl) {
        
        int value = ((Spinner)valueControl).getSelection();        
        return Integer.valueOf(value);
    }
    
	private static IntegerProperty intPropertyFrom(PropertyDescriptor<?> desc) {
	    
	    if (desc instanceof PropertyDescriptorWrapper) {
           return (IntegerProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (IntegerProperty)desc;
        }
	}
	
	public static Spinner newSpinner(Composite parent, NumericPropertyDescriptor<?> desc, Object valueIn) {
	    
	    Spinner spinner = newSpinnerFor(parent, 0);
	    
        spinner.setMinimum(desc.lowerLimit().intValue());
        spinner.setMaximum(desc.upperLimit().intValue());
        
        int value = valueIn == null ? spinner.getMinimum() : ((Number)valueIn).intValue();
        spinner.setSelection(value);
        return spinner;
	}

	protected void setValue(Rule rule, IntegerProperty desc, Integer value) {
	    
	    if (!rule.hasDescriptor(desc)) return;
	    rule.setProperty(desc, value);
	}
	
	public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
	        
	      final IntegerProperty ip = intPropertyFrom(desc);   // TODO - do I really have to do this?          

	      final Spinner spinner = newSpinner(parent, ip, valueFor(rule, desc));
	            
	      spinner.addModifyListener(new ModifyListener() {
	           public void modifyText(ModifyEvent event) {
	                Integer newValue = Integer.valueOf(spinner.getSelection());
	                if (newValue.equals(valueFor(rule, ip))) return;
	                    
	                setValue(rule, ip, newValue);
	                listener.changed(rule, desc, newValue);
	                adjustRendering(rule, desc, spinner);
	                }
	            });     
	                        
	       return spinner;
	   }     
}
