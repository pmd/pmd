package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.lang.reflect.Method;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class MultiMethodEditorFactory extends AbstractMultiValueEditorFactory {

	public static final MultiMethodEditorFactory instance = new MultiMethodEditorFactory();
				
	private MultiMethodEditorFactory() { }
	
	public static String[] signaturesFor(Method[] methods) {
	    String[] typeNames = new String[methods.length];
        for (int i=0; i<typeNames.length; i++) {
            typeNames[i] = Util.signatureFor(methods[i], MethodEditorFactory.UnwantedPrefixes);
        }
        return typeNames;
	}
	
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
        
        Method[] values = (Method[])rule.getProperty(desc);
        if (values == null) {
            textWidget.setText("");
            return;
        }
        
        String[] methodSigs = signaturesFor(values);
        textWidget.setText(values == null ? "" : StringUtil.asString(methodSigs, delimiter + ' '));
    }
	
//	private Class<?>[] currentTypes(Text textWidget) {
//	    
//	    String[] typeNames = textWidgetValues(textWidget);
//	    if (typeNames.length == 0) return ClassUtil.EMPTY_CLASS_ARRAY;
//	    
//	    List<Class<?>> types = new ArrayList<Class<?>>(typeNames.length);
//	    
//	    for (int i=0; i<typeNames.length; i++) {
//	        Class<?> newType = TypeEditorFactory.typeFor(typeNames[i]);
//	        if (newType != null) types.add(newType);
//	    }
//	    return (Class[]) types.toArray(new Class[types.size()]);
//	}
	
//   private static MethodMultiProperty multiTypePropertyFrom(PropertyDescriptor<?> desc) {
//	        
//        if (desc instanceof PropertyDescriptorWrapper) {
//           return (MethodMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
//        } else {
//            return (MethodMultiProperty)desc;
//        }
//    }
	
    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule) {
        MethodPicker widget = new MethodPicker(parent, SWT.SINGLE | SWT.BORDER, MethodEditorFactory.UnwantedPrefixes);
        setValue(widget, value);
        return widget;
    }
    
    protected void setValue(Control widget, Object value) {
        Method method = value == null ? null : (Method)value;
        ((MethodPicker)widget).setMethod(method);
    }
   
    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener) {
        textWidget.setEditable(false);
    }
    
    protected void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues) {
        rule.setProperty((MethodMultiProperty)desc, newValues.toArray(new Method[newValues.size()]));
    }
    
    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule) {
        
        Method newValue = ((MethodPicker)widget).getMethod();
        if (newValue == null) return null;
        
        Method[] currentValues = (Method[])rule.getProperty(desc);
        Method[] newValues = Util.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;  // nothing changed
        
        rule.setProperty((MethodMultiProperty)desc, newValues);
        return newValue;
    }
}
