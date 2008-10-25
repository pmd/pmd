package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class MultiStringEditorFactory extends AbstractMultiValueEditorFactory {

	public static final MultiStringEditorFactory instance = new MultiStringEditorFactory();
		
	private MultiStringEditorFactory() { }
	
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
                
        final StringMultiProperty smp = (StringMultiProperty)desc;	// TODO - really necessary?
        
        textWidget.addListener(SWT.FocusOut, new Listener() {
        	public void handleEvent(Event event) {
        		String[] newValues = textWidgetValues(textWidget);					
        		String[] existingValues = rule.getProperty(smp);				
        		if (Util.areSemanticEquals(existingValues, newValues)) return;				
        		
        		rule.setProperty(smp, newValues);        		
        		fillWidget(textWidget, desc, rule);    // reload with latest scrubbed values        		
        		listener.changed(desc, newValues);
        	}
        });
    }
}
