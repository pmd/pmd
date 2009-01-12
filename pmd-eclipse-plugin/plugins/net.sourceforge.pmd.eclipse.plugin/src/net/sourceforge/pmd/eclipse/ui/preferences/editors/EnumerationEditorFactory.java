package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author Brian Remedios
 */
public class EnumerationEditorFactory extends AbstractEditorFactory {

    public static final EnumerationEditorFactory instance = new EnumerationEditorFactory();

    private EnumerationEditorFactory() { }

    private static String[] labelsIn(Object[][] items) {
        
        String[] labels = new String[items.length];
        for (int i=0; i<labels.length; i++) labels[i] = items[i][0].toString();
        return labels;
    }
    
    private static EnumeratedProperty<?> enumerationPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (EnumeratedProperty<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (EnumeratedProperty<?>)desc;
        }
    }
    
    public Control newEditorOn(Composite parent, int columnIndex, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener) {
        
        if (columnIndex == 0) return addLabel(parent, desc);
        
        if (columnIndex == 1) {
            final Combo combo = new Combo(parent, SWT.READ_ONLY);
            
            if (desc instanceof PropertyDescriptorWrapper) {
                
                // TODO
                return combo;
            }
            
            final EnumeratedProperty<?> ep = enumerationPropertyFrom(desc);
            
            combo.setItems(labelsIn(ep.choices()));
            
            combo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                  

                }
              });  
            
            return combo;
        }
        
        return null;
    }

}
