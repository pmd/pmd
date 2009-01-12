package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Brian Remedios
 */
public class BooleanEditorFactory extends AbstractEditorFactory {

	public static final BooleanEditorFactory instance = new BooleanEditorFactory();

	
	private BooleanEditorFactory() { }
	
    private static BooleanProperty booleanPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (BooleanProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
           } else {
            return (BooleanProperty)desc;
         }
    }    
	
	/**
	 * Method newEditorOn.
	 * @param parent Composite
	 * @param columnIndex int
	 * @param desc PropertyDescriptor
	 * @param rule Rule
	 * @return Control
	 * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#newEditorOn(Composite, int, PropertyDescriptor, Rule)
	 */
	public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);	
		
    	if (columnIndex == 1) {
    		    
    		final Button butt =  new Button(parent, SWT.CHECK);
            butt.setText("");
                		
    		final BooleanProperty bp = booleanPropertyFrom(desc);	// TODO - do I really have to do this?
    		
    		boolean set = ((Boolean)rule.getProperty(desc)).booleanValue();
    		butt.setSelection(set);
    		
    		butt.addSelectionListener(new SelectionAdapter() {
    			public void widgetSelected(SelectionEvent event) {
    				boolean selected = butt.getSelection();
    				if (selected == rule.getProperty(bp)) return;
    				
    	//			adjustRendering(butt, selected == bp.defaultValue().booleanValue());
    				rule.setProperty(bp, Boolean.valueOf(selected));
    				listener.changed(rule, desc, Boolean.valueOf(selected));
    			}
    		});
    		
    		
    		return butt;
    	}
        return null;
	}

}
