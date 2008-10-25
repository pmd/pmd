package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class RealNumberEditorFactory extends AbstractEditorFactory {

	public static final RealNumberEditorFactory instance = new RealNumberEditorFactory();
	
	
	private RealNumberEditorFactory() { }

	public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);
		
		if (columnIndex == 1) {
			
			final Text text =  new Text(parent, SWT.SINGLE);   // TODO use a number-only widget
				         
            if (desc instanceof PropertyDescriptorWrapper) {

                // TODO
                return text;
            }
			
			Number val = (Number)rule.getProperty(desc);
			text.setText(val == null ? "" : val.toString());

			final FloatProperty fp = (FloatProperty)desc;
			
			text.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
					String newValue = text.getText();
					
					rule.setProperty(fp, Float.valueOf(newValue));
					listener.changed(fp, Float.valueOf(newValue));
				}
			});
			
			return text;
		}
		
		return null;
	}

}
