package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
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
 * Behaviour:
 *           Provide a set of widgets that allows the user to pick a range of integer values. The selected values can only exist once.
 *
 * Provide a spin box for each value selected while ensuring that their choices only contain unselected values. If the last spin box holds the only
 * remaining choice then ensure it gets disabled, the user can only delete it or the previous ones. If the user deletes a previous one then re-enable
 * the last one and add the deleted value to its set of choices.
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
        return ints.toArray(new Integer[ints.size()]);
    }

    private static NumericPropertyDescriptor<?> numericPropertyFrom(PropertyDescriptor<?> desc) {

        if (desc instanceof PropertyDescriptorWrapper<?>) {
           return (NumericPropertyDescriptor<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (NumericPropertyDescriptor<?>)desc;
        }
    }

    protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, PropertySource source) {

        NumericPropertyDescriptor<?> ip = numericPropertyFrom(desc);   // TODO - do I really have to do this?
        return IntegerEditorFactory.newSpinner(parent, ip, value);
    }

    protected void setValue(Control widget, Object valueIn) {

        Spinner spinner = (Spinner)widget;
        int value = valueIn == null ? spinner.getMinimum() : ((Number)valueIn).intValue();
        spinner.setSelection(value);
    }

    protected void configure(final Text textWidget, final PropertyDescriptor<?> desc, final PropertySource source, final ValueChangeListener listener) {

        final IntegerMultiProperty imp = (IntegerMultiProperty)numericPropertyFrom(desc);

        textWidget.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Integer[] newValue = currentIntegers(textWidget);
                Integer[] existingValue = (Integer[])valueFor(source, imp);
                if (CollectionUtil.areSemanticEquals(existingValue, newValue)) return;

                source.setProperty(imp, newValue);
                fillWidget(textWidget, desc, source);   // display the accepted values
                listener.changed(source, desc, newValue);
            }
        });
    }

    protected void update(PropertySource source, PropertyDescriptor<?> desc, List<Object> newValues) {
        source.setProperty((IntegerMultiProperty)desc, newValues.toArray(new Integer[newValues.size()]));
    }

    @Override
    protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, PropertySource source) {

        Integer newValue= Integer.valueOf(((Spinner)widget).getSelection());

        Integer[] currentValues = (Integer[])valueFor(source, desc);
        Integer[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
        if (currentValues.length == newValues.length) return null;

        source.setProperty((IntegerMultiProperty)desc, newValues);
        return newValue;
    }
}
