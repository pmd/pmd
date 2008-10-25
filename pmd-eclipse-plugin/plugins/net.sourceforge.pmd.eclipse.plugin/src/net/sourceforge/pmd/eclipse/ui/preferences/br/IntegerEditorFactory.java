package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * @author Brian Remedios
 */
public class IntegerEditorFactory extends AbstractEditorFactory {

	public static final IntegerEditorFactory instance = new IntegerEditorFactory();
	
	private IntegerEditorFactory() { }

	/**
	 *
	 * @param parent Composite
	 * @param columnIndex int
	 * @param desc PropertyDescriptor
	 * @param rule Rule
	 * @return Control
	 * @see net.sourceforge.pmd.ui.preferences.br.EditorFactory#newEditorOn(Composite, int, PropertyDescriptor, Rule)
	 */
	public Control newEditorOn(Composite parent, int columnIndex, final	PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);
		
		if (columnIndex == 1) {		    

            final Spinner spinner =  new Spinner(parent, SWT.SINGLE | SWT.BORDER);
			
            if (desc instanceof PropertyDescriptorWrapper) {
                
                // TODO
                return spinner;
            }
            
            
			final IntegerProperty ip = (IntegerProperty)desc;	// TODO - do I really have to do this?			

			int val = ((Number)rule.getProperty(desc)).intValue();
			
			spinner.setMinimum(ip.lowerLimit().intValue());
			spinner.setMaximum(ip.upperLimit().intValue());
			spinner.setSelection(val);
		
			spinner.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					int newValue = spinner.getSelection();
					if (newValue == rule.getProperty(ip)) return;
					
					rule.setProperty(ip, Integer.valueOf(newValue));
					listener.changed(desc, Integer.valueOf(newValue));
				}
			});		
						
			return spinner;
		}
		return null;
	}

}
