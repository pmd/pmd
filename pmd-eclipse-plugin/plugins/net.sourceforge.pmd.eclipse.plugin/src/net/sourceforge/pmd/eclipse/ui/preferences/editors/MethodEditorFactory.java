package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.lang.reflect.Method;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.util.ClassUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * 
 * @author Brian Remedios
 */
public class MethodEditorFactory extends AbstractEditorFactory {

	public static final MethodEditorFactory instance = new MethodEditorFactory();
	public static final String[] UnwantedPrefixes = new String[] {
            "java.lang.reflect.",
	        "java.lang.",
	        "java.util."
	        };
	   
	public static final Method stringLength = ClassUtil.methodFor(String.class, "length", ClassUtil.EMPTY_CLASS_ARRAY);
	   
	private MethodEditorFactory() { }	
	
    public PropertyDescriptor<?> createDescriptor(String name, String optionalDescription, Control[] otherData) {
        return new MethodProperty(name, "Method value " + name, stringLength, new String[] { "java.lang" }, 0.0f);
    } 
	
    protected Object valueFrom(Control valueControl) {
        
        return ((MethodPicker)valueControl).getMethod();
    }
    
	protected void fillWidget(MethodPicker widget, PropertyDescriptor<?> desc, Rule rule) {
		
		Method method = (Method)valueFor(rule, desc);
		widget.setMethod(method);
        adjustRendering(rule, desc, widget);
	}
		
    private static MethodProperty methodPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (MethodProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (MethodProperty)desc;
        }
    }
	
    public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
        
        final MethodPicker picker = new MethodPicker(parent, SWT.SINGLE | SWT.BORDER, UnwantedPrefixes);
        picker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fillWidget(picker, desc, rule);
                        
        final MethodProperty mp = methodPropertyFrom(desc);
            
        picker.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Method newValue = picker.getMethod();
                if (newValue == null) return;
                    
                Method existingValue = (Method)valueFor(rule, mp);                
                if (existingValue == newValue) return;              
                    
                rule.setProperty(mp, newValue);
                fillWidget(picker, desc, rule);     // redraw
                listener.changed(rule, desc, newValue);
            }
        });

        return picker;
    }        
}
