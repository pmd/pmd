package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.TypeMultiProperty;
import net.sourceforge.pmd.util.ClassUtil;
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
public class MultiTypeEditorFactory extends AbstractMultiValueEditorFactory {

	public static final MultiTypeEditorFactory instance = new MultiTypeEditorFactory();
				
	private MultiTypeEditorFactory() { }
	
	public static String[] shortNamesFor(Class<?>[] types) {
	    String[] typeNames = new String[types.length];
        for (int i=0; i<typeNames.length; i++) {
            typeNames[i] = ClassUtil.asShortestName(types[i]);
        }
        return typeNames;
	}
	
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
        
        Class<?>[] values = (Class[])rule.getProperty(desc);
        if (values == null) {
            textWidget.setText("");
            return;
        }
        
        String[] typeNames = shortNamesFor(values);

        textWidget.setText(values == null ? "" : StringUtil.asString(typeNames, delimiter + ' '));
    }
	
	private Class<?>[] currentTypes(Text textWidget) {
	    
	    String[] typeNames = textWidgetValues(textWidget);
	    if (typeNames.length == 0) return ClassUtil.EMPTY_CLASS_ARRAY;
	    
	    List<Class<?>> types = new ArrayList<Class<?>>(typeNames.length);
	    
	    for (int i=0; i<typeNames.length; i++) {
	        Class<?> newType = TypeEditorFactory.typeFor(typeNames[i]);
	        if (newType != null) types.add(newType);
	    }
	    return (Class[]) types.toArray(new Class[types.size()]);
	}
	
   private static TypeMultiProperty multiTypePropertyFrom(PropertyDescriptor<?> desc) {
	        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (TypeMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (TypeMultiProperty)desc;
        }
    }
	
    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule) {
        Text textWidget = new Text(parent, SWT.SINGLE | SWT.BORDER);
        setValue(textWidget, value);
        return textWidget;
    }
    
    protected void setValue(Control widget, Object value) {
        String strValue = value == null ? "" : ClassUtil.asShortestName((Class<?>)value);
        ((Text)widget).setText(strValue);
    }
   
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
                
        final TypeMultiProperty tmp = multiTypePropertyFrom(desc);  // TODO - really necessary?
        
        textWidget.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Class<?>[] newValue = currentTypes(textWidget);               
                Class<?>[] existingValue = rule.getProperty(tmp);             
                if (Util.areSemanticEquals(existingValue, newValue)) return;                
                
                rule.setProperty(tmp, newValue);
                fillWidget(textWidget, desc, rule);   // display the accepted values
                listener.changed(rule, desc, newValue);
            }
        });
    }
    
    protected void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues) {
        rule.setProperty((TypeMultiProperty)desc, newValues.toArray(new Class[newValues.size()]));
    }
    
    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule) {
        
        Class<?>[] enteredValues = currentTypes((Text) widget);
        if (enteredValues.length == 0) return null;
        
        Class<?>[] currentValues = (Class[])rule.getProperty(desc);
        Class<?>[] newValues = Util.addWithoutDuplicates(currentValues, enteredValues);
        if (currentValues.length == newValues.length) return null;
        
        rule.setProperty((TypeMultiProperty)desc, newValues);
        return newValues;
    }
}
