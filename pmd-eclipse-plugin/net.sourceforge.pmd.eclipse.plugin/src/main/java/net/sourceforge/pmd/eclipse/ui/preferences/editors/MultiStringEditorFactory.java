package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.util.CollectionUtil;
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
public class MultiStringEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiStringEditorFactory instance = new MultiStringEditorFactory();

	private MultiStringEditorFactory() { }

    public PropertyDescriptor<?> createDescriptor(String name, String optionalDescription, Control[] otherData) {
        return new StringMultiProperty(name, "String value " + name, new String[] {""}, 0.0f, StringMultiProperty.DEFAULT_DELIMITER);
    }

    private static StringMultiProperty multiStringPropertyFrom(PropertyDescriptor<?> desc) {

        if (desc instanceof PropertyDescriptorWrapper<?>) {
           return (StringMultiProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (StringMultiProperty)desc;
        }
    }

    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, PropertySource source) {
        Text textWidget = new Text(parent, SWT.SINGLE | SWT.BORDER);
        setValue(textWidget,value);
        return textWidget;
    }

    protected void setValue(Control widget, Object value) {
        ((Text)widget).setText(value == null ? "" : value.toString());
    }

    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final PropertySource source, final ValueChangeListener listener) {

        final StringMultiProperty smp = multiStringPropertyFrom(desc);

        Listener widgetListener = new Listener() {
        	public void handleEvent(Event event) {
        		String[] newValues = textWidgetValues(textWidget);
        		String[] existingValues = (String[])valueFor(source, smp);
        		if (CollectionUtil.areSemanticEquals(existingValues, newValues)) return;

        		source.setProperty(smp, newValues);
        		fillWidget(textWidget, desc, source);    // reload with latest scrubbed values
        		listener.changed(source, desc, newValues);
        		}
        	};

        textWidget.addListener(SWT.FocusOut, widgetListener);
//        textWidget.addListener(SWT.DefaultSelection, widgetListener);
    }

    @Override
    protected void update(PropertySource source, PropertyDescriptor<?> desc, List<Object> newValues) {
    	source.setProperty((StringMultiProperty)desc, newValues.toArray(new String[newValues.size()]));
    }

    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, PropertySource source) {

        String newValue = ((Text)widget).getText().trim();
        if (StringUtil.isEmpty(newValue)) return null;

        String[] currentValues = (String[])valueFor(source, desc);
        String[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;

        source.setProperty((StringMultiProperty)desc, newValues);
        return newValue;
    }

    protected Object valueFrom(Control valueControl) {	// not necessary for this type
        return null;
    }
}
