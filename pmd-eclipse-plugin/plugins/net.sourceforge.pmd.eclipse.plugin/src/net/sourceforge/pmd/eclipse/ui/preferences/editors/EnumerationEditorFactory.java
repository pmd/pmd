package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
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
    
    private static EnumeratedProperty<?> enumerationPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (EnumeratedProperty<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (EnumeratedProperty<?>)desc;
        }
    }
    
    public static int indexOf(Object item, Object[][] items) {
        for (int i=0; i<items.length; i++) if (items[i][0].equals(item)) return i;
        return -1;
    }
    
    public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
        
        if (columnIndex == 0) return addLabel(parent, desc);
        
        if (columnIndex == 1) {
            final Combo combo = new Combo(parent, SWT.READ_ONLY);
                        
            final EnumeratedProperty<?> ep = enumerationPropertyFrom(desc);
            Object value = rule.getProperty(desc);
            combo.setItems(SWTUtil.labelsIn(ep.choices(), 0));
            int selectionIdx = indexOf(value, ep.choices());
            if (selectionIdx >= 0) combo.select(selectionIdx);
            
            combo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    int selectionIdx = combo.getSelectionIndex();
                    Object newValue = ep.choices()[selectionIdx][1];                    
                    if (newValue == rule.getProperty(desc)) return;
                    
                    rule.setProperty(ep, newValue);
                    listener.changed(rule, desc, newValue);
                    adjustRendering(rule, desc, combo);
                }
              });  
            
            return combo;
        }
        
        return null;
    }

}
