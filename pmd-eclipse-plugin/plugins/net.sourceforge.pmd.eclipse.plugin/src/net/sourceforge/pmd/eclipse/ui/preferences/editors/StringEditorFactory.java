package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
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
    
    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
        
        return new StringProperty(
                name, 
                description, 
                otherData == null ? "" : (String)valueFrom(otherData[1]), 
                0.0f
                );
    }
    
    
    protected Object valueFrom(Control valueControl) {
        return ((Text)valueControl).getText();
    }
    
	/**
	 * Method fillWidget.
	 * @param textWidget Text
	 * @param desc PropertyDescriptor<?>
	 * @param rule Rule
	 */
	protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
		String val = (String)valueFor(rule, desc);
		textWidget.setText(val == null ? "" : val);
		adjustRendering(rule, desc, textWidget);
	}
		
	private static StringProperty stringPropertyFrom(PropertyDescriptor<?> desc) {
	        
	    if (desc instanceof PropertyDescriptorWrapper) {
	       return (StringProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
	       } else {
	        return (StringProperty)desc;
	     }
	}

	private void setValue(Rule rule, StringProperty desc, String value) {
	        
	    if (!rule.hasDescriptor(desc)) return;
	    rule.setProperty(desc, value);
	}
	
   public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
           
            final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            fillWidget(text, desc, rule);
	                        
            final StringProperty sp = stringPropertyFrom(desc); // TODO - really necessary?
	            
            text.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
                    String newValue = text.getText().trim();                    
                    String existingValue = (String)valueFor(rule, sp);                
                    if (StringUtil.areSemanticEquals(existingValue, newValue)) return;              
	                    
                    setValue(rule, sp, newValue);
                    fillWidget(text, desc, rule);     // redraw
                    listener.changed(rule, desc, newValue);
                }
            });

            return text;
        }       
}
