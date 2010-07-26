package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.CollectionUtil;

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

    public PropertyDescriptor<?> createDescriptor(String name, String optionalDescription, Control[] otherData) {
        return new MethodMultiProperty(name, "Method value " + name, new Method[] {MethodEditorFactory.stringLength}, new String[] { "java.lang" }, 0.0f);
    }

	public static String[] signaturesFor(Method[] methods) {
	    String[] typeNames = new String[methods.length];
        for (int i=0; i<typeNames.length; i++) {
            typeNames[i] = Util.signatureFor(methods[i], MethodEditorFactory.UnwantedPrefixes);
        }
        return typeNames;
	}

	private static String asString(Map<String, List<Method>> methodGroups) {

	    if (methodGroups.isEmpty()) return "";

	    StringBuilder sb = new StringBuilder();
	    Iterator<Entry<String, List<Method>>> iter = methodGroups.entrySet().iterator();
	    Entry<String, List<Method>> entry = iter.next();

	    sb.append(entry.getKey()).append('[');
	    allSignaturesOn(sb, entry.getValue(), ",");
	    sb.append(']');

	    while (iter.hasNext()) {
	        entry = iter.next();
	        sb.append("  ").append(entry.getKey()).append('[');
	        allSignaturesOn(sb, entry.getValue(), ", ");
	        sb.append(']');
	    }

	    return sb.toString();
	}

	private static void allSignaturesOn(StringBuilder sb, List<Method> methods, String delimiter) {

	    sb.append(
	        Util.signatureFor(methods.get(0), MethodEditorFactory.UnwantedPrefixes)
	        );

	    for (int i=1; i<methods.size(); i++) {
	        sb.append(delimiter).append(
	            Util.signatureFor(methods.get(i), MethodEditorFactory.UnwantedPrefixes)
	            );
	    }
	}

    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {

        Method[] values = (Method[])valueFor(rule, desc);
        if (values == null) {
            textWidget.setText("");
            return;
        }

        Map<String, List<Method>> methodMap = ClassUtil.asMethodGroupsByTypeName(values);
        textWidget.setText(values == null ? "" : asString(methodMap));

        adjustRendering(rule, desc, textWidget);
    }

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

        Method[] currentValues = (Method[])valueFor(rule, desc);
        Method[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;  // nothing changed

        rule.setProperty((MethodMultiProperty)desc, newValues);
        return newValue;
    }

    protected Object valueFrom(Control valueControl) {	// not necessary for this type
        return null;
    }
}
