package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class DoubleEditorFactory extends AbstractEditorFactory {

	public static final DoubleEditorFactory instance = new DoubleEditorFactory();
		
	private DoubleEditorFactory() { }

    private static DoubleProperty doublePropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (DoubleProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (DoubleProperty)desc;
        }
    }
	
    private static Double doubleFrom(Text widget) {
        String str = widget.getText();
        if (StringUtil.isEmpty(str)) return null;
        
        try {
            return Double.valueOf(str);
            } catch (Exception ex) {
                return null;
            }
    }
    
	public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
		
		if (columnIndex == 0) return addLabel(parent, desc);
		
		if (columnIndex == 1) {
			
			final Text text =  new Text(parent, SWT.SINGLE);   // TODO use a number-only widget
				         			
			Number val = (Number)rule.getProperty(desc);
			text.setText(val == null ? "" : val.toString());

			final DoubleProperty dp = doublePropertyFrom(desc);
			
			text.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
					Double newValue = doubleFrom(text);
					if (newValue.equals(rule.getProperty(dp))) return;
					
					rule.setProperty(dp, newValue);
					listener.changed(rule, dp, newValue);
				}
			});
			
			return text;
		}
		
		return null;
	}

}
