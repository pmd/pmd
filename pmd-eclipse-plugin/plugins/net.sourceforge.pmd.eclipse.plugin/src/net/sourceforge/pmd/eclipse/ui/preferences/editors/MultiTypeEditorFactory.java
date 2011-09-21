package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.TypeMultiProperty;
import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * TODO - use new TypeText widget
 *
 * @author Brian Remedios
 */
public class MultiTypeEditorFactory extends AbstractMultiValueEditorFactory {

	public static final MultiTypeEditorFactory instance = new MultiTypeEditorFactory();

	private MultiTypeEditorFactory() { }

    public PropertyDescriptor<?> createDescriptor(String name, String optionalDescription, Control[] otherData) {
        return new TypeMultiProperty(name, "Type value " + name, new Class[] {String.class}, new String[] { "java.lang" } , 0.0f);
    }

	public static String[] shortNamesFor(Class<?>[] types) {
	    String[] typeNames = new String[types.length];
        for (int i=0; i<typeNames.length; i++) {
            typeNames[i] = ClassUtil.asShortestName(types[i]);
        }
        return typeNames;
	}

    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, PropertySource source) {

        Class<?>[] values = (Class[])valueFor(source, desc);
        if (values == null) {
            textWidget.setText("");
            return;
        }

        textWidget.setText(values == null ? "" : asString(values));
        adjustRendering(source, desc, textWidget);
    }

    private String asString(Class<?>[] types) {
        String[] typeNames = shortNamesFor(types);
        return StringUtil.asString(typeNames, delimiter + ' ');
    }

	private Class<?>[] currentTypes(Text textWidget) {

	    String[] typeNames = textWidgetValues(textWidget);
	    if (typeNames.length == 0) return ClassUtil.EMPTY_CLASS_ARRAY;

	    List<Class<?>> types = new ArrayList<Class<?>>(typeNames.length);

	    for (String typeName : typeNames) {
	        Class<?> newType = TypeEditorFactory.typeFor(typeName);
	        if (newType != null) types.add(newType);
	    }
	    return types.toArray(new Class[types.size()]);
	}

   private static TypeMultiProperty multiTypePropertyFrom(PropertyDescriptor<?> desc) {

        if (desc instanceof PropertyDescriptorWrapper<?>) {
           return (TypeMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (TypeMultiProperty)desc;
        }
    }

    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, PropertySource source) {
        TypeText typeWidget = new TypeText(parent, SWT.SINGLE | SWT.BORDER, true, "Enter type name");
        setValue(typeWidget, value);
        return typeWidget;
    }

    protected void setValue(Control widget, Object value) {

        Class<?> type = (Class<?>)value;
        ((TypeText)widget).setType(type);
    }

    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final PropertySource source, final ValueChangeListener listener) {

        final TypeMultiProperty tmp = multiTypePropertyFrom(desc);  // TODO - really necessary?

        textWidget.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Class<?>[] newValue = currentTypes(textWidget);
                Class<?>[] existingValue = (Class[])valueFor(source, tmp);
                if (CollectionUtil.areSemanticEquals(existingValue, newValue)) return;

                source.setProperty(tmp, newValue);
                fillWidget(textWidget, desc, source);   // display the accepted values
                listener.changed(source, desc, newValue);

                adjustRendering(source, desc, textWidget);
            }
        });
    }

    protected void update(PropertySource source, PropertyDescriptor<?> desc, List<Object> newValues) {
    	source.setProperty((TypeMultiProperty)desc, newValues.toArray(new Class[newValues.size()]));
    }

    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, PropertySource source) {

        Class<?> enteredValue = ((TypeText) widget).getType(true);
        if (enteredValue == null) return null;

        Class<?>[] currentValues = (Class[])valueFor(source, desc);
        Class<?>[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, enteredValue);
        if (currentValues.length == newValues.length) return null;

        source.setProperty((TypeMultiProperty)desc, newValues);
        return enteredValue;
    }

    protected Object valueFrom(Control valueControl) {	// not necessary for this type
        return null;
    }
}
