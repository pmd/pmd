package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.NumericConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * @author Brian Remedios
 */
public class MultiIntegerEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiIntegerEditorFactory instance = new MultiIntegerEditorFactory();
    
    private static final Integer[] emptyIntSet = new Integer[0];
    
    private MultiIntegerEditorFactory() {   }
    
    public PropertyDescriptor<?> createDescriptor(String name, String optionalDescription, Control[] otherData) {
        return new IntegerMultiProperty(name, "Integer values " + name, NumericConstants.ZERO, Integer.valueOf(10), new Integer[] {NumericConstants.ZERO}, 0.0f);
    }
    
    protected Object valueFrom(Control valueControl) {
        
        return currentIntegers((Text)valueControl);
    }
    
    private Integer[] currentIntegers(Text textWidget) {
        
        String[] numberStrings = textWidgetValues(textWidget);
        if (numberStrings.length == 0) return emptyIntSet;
        
        List<Integer> ints = new ArrayList<Integer>(numberStrings.length);
        
        Integer intrg = null;
        
        for (String numString : numberStrings) {
            try {
                intrg = Integer.parseInt(numString);
            } catch (Exception e) {
               // just eat it for now
            }
            if (intrg != null) ints.add(intrg);
        }
        return (Integer[]) ints.toArray(new Integer[ints.size()]);
    }
    
    private static NumericPropertyDescriptor<?> numericPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (NumericPropertyDescriptor<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (NumericPropertyDescriptor<?>)desc;
        }
    }
    
    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule) {
       
        NumericPropertyDescriptor<?> ip = numericPropertyFrom(desc);   // TODO - do I really have to do this?       
        return IntegerEditorFactory.newSpinner(parent, ip, value);
    }  
    
    protected void setValue(Control widget, Object valueIn) {
        
        Spinner spinner = (Spinner)widget;
        int value = valueIn == null ? spinner.getMinimum() : ((Number)valueIn).intValue();
        spinner.setSelection(value);
    }
    
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
                
        final IntegerMultiProperty imp = (IntegerMultiProperty)numericPropertyFrom(desc);
        
        textWidget.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Integer[] newValue = currentIntegers(textWidget);               
                Integer[] existingValue = (Integer[])valueFor(rule, imp);             
                if (CollectionUtil.areSemanticEquals(existingValue, newValue)) return;                
                
                rule.setProperty(imp, newValue);
                fillWidget(textWidget, desc, rule);   // display the accepted values
                listener.changed(rule, desc, newValue);
            }
        });
    }
    
    protected void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues) {
        rule.setProperty((IntegerMultiProperty)desc, newValues.toArray(new Integer[newValues.size()]));
    }
    
    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule) {
                
        Integer newValue= Integer.valueOf(((Spinner)widget).getSelection());
        
        Integer[] currentValues = (Integer[])valueFor(rule, desc);
        Integer[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;
        
        rule.setProperty((IntegerMultiProperty)desc, newValues);
        return newValue;
    }
}
