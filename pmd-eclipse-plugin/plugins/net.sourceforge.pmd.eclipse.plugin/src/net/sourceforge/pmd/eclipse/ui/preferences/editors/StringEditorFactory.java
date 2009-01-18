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
	
	/**
	 * Method fillWidget.
	 * @param textWidget Text
	 * @param desc PropertyDescriptor<?>
	 * @param rule Rule
	 */
	protected void fillWidget(final Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
		String val = (String)rule.getProperty(desc);
		textWidget.setText(val == null ? "" : val);
	}
		
	private static StringProperty stringPropertyFrom(PropertyDescriptor<?> desc) {
	        
	    if (desc instanceof PropertyDescriptorWrapper) {
	       return (StringProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
	       } else {
	        return (StringProperty)desc;
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
			
			final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);
	        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            fillWidget(text, desc, rule);
                        
			final StringProperty sp = stringPropertyFrom(desc);	// TODO - really necessary?
			
			text.addListener(SWT.FocusOut, new Listener() {
				public void handleEvent(Event event) {
					String newValue = text.getText().trim();					
					String existingValue = rule.getProperty(sp);				
					if (StringUtil.areSemanticEquals(existingValue, newValue)) return;				
					
					rule.setProperty(sp, newValue);
		            fillWidget(text, desc, rule);     // redraw
					listener.changed(rule, desc, newValue);
				}
			});

			return text;
		}
		
		return null;
	}

}
