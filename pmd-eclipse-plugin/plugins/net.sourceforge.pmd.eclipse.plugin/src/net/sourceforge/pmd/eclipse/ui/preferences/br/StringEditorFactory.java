package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class StringEditorFactory extends AbstractEditorFactory {

	public static final StringEditorFactory instance = new StringEditorFactory();
	
	protected StringEditorFactory() { }
	
	/**
	 * Method fillWidget.
	 * @param textWidget Text
	 * @param desc PropertyDescriptor<?>
	 * @param rule Rule
	 */
	protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
		String val = (String)rule.getProperty(desc);
		textWidget.setText(val == null ? "" : val);
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
	public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);
		
		if (columnIndex == 1) {
			
			final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);
			GridData gridData = new GridData();
	        gridData.horizontalAlignment = SWT.FILL;
	        gridData.grabExcessHorizontalSpace = true;
	        text.setLayoutData(gridData);

            fillWidget(text, desc, rule);
            
            if (desc instanceof PropertyDescriptorWrapper) {
                
                final PropertyDescriptorWrapper descWrapper = (PropertyDescriptorWrapper)desc;
                
                text.addListener(SWT.FocusOut, new Listener() {
                    public void handleEvent(Event event) {
                        String newValue = text.getText().trim();                    
                        String existingValue = (String)rule.getProperty(descWrapper);                
                        if (StringUtil.areSemanticEquals(existingValue, newValue)) return;             
                        
                        rule.setProperty(descWrapper, newValue);
                        fillWidget(text, desc, rule);       // redraw 
                        listener.changed(desc, newValue);
                    }
                });               
                
                return text;
            }
            
			final StringProperty sp = (StringProperty)desc;	// TODO - really necessary?
			
			text.addListener(SWT.FocusOut, new Listener() {
				public void handleEvent(Event event) {
					String newValue = text.getText().trim();					
					String existingValue = rule.getProperty(sp);				
					if (StringUtil.areSemanticEquals(existingValue, newValue)) return;				
					
					rule.setProperty(sp, newValue);
		            fillWidget(text, desc, rule);     // redraw
					listener.changed(desc, newValue);
				}
			});

			return text;
		}
		
		return null;
	}

}
