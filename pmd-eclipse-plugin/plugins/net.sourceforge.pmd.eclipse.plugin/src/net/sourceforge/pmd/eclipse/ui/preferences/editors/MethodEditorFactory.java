package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.lang.reflect.Method;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

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
	        "java.lang.",
	        "java.util."
	        };
	
	private MethodEditorFactory() { }
	   
	protected void fillWidget(MethodPicker widget, PropertyDescriptor<?> desc, Rule rule) {
		
		Method method = (Method)rule.getProperty(desc);
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
	
    /**
     * 
     * @param parent Composite
     * @param columnIndex int
     * @param desc PropertyDescriptor
     * @param rule Rule
     * @param listener ValueChangeListener
     * @return Control
     * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#newEditorOn(Composite, int, PropertyDescriptor, Rule)
     */
    public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
        
        if (columnIndex == 0) return addLabel(parent, desc);
        
        if (columnIndex == 1) {
            
            final MethodPicker picker = new MethodPicker(parent, SWT.SINGLE | SWT.BORDER, UnwantedPrefixes);
            picker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            fillWidget(picker, desc, rule);
                        
            final MethodProperty mp = methodPropertyFrom(desc);
            
            picker.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    Method newValue = picker.getMethod();
                    if (newValue == null) return;
                    
                    Method existingValue = rule.getProperty(mp);                
                    if (existingValue == newValue) return;              
                    
                    rule.setProperty(mp, newValue);
                    fillWidget(picker, desc, rule);     // redraw
                    listener.changed(rule, desc, newValue);
                }
            });

            return picker;
        }
        
        return null;
    }

}
