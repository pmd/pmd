package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class MultiIntegerEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiIntegerEditorFactory instance = new MultiIntegerEditorFactory();
    
    private static final Integer[] emptyIntSet = new Integer[0];
    
    private MultiIntegerEditorFactory() {   }
    
    private Integer[] currentIntegers(Text textWidget) {
        
        String[] numberStrings = textWidgetValues(textWidget);
        if (numberStrings.length == 0) return emptyIntSet;
        
        List<Integer> ints = new ArrayList<Integer>(numberStrings.length);
        
        Integer intrg = null;
        
        for (int i=0; i<numberStrings.length; i++) {
            try {
                intrg = Integer.parseInt(numberStrings[i]);
            } catch (Exception e) {
               // just eat it for now
            }
            if (intrg != null) ints.add(intrg);
        }
        return (Integer[]) ints.toArray(new Integer[ints.size()]);
    }
    
    private static IntegerMultiProperty multiIntegerPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (IntegerMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (IntegerMultiProperty)desc;
        }
    }
    
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
                
        final IntegerMultiProperty tmp = multiIntegerPropertyFrom(desc);
        
        textWidget.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Integer[] newValue = currentIntegers(textWidget);               
                Integer[] existingValue = rule.getProperty(tmp);             
                if (Util.areSemanticEquals(existingValue, newValue)) return;                
                
                rule.setProperty(tmp, newValue);
                fillWidget(textWidget, desc, rule);   // display the accepted values
                listener.changed(desc, newValue);
            }
        });
    }
}
