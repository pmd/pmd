package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.util.CollectionUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
/**
 * Behaviour:
 *           Provide a set of widgets that allows the user to select any number of items from a fixed set of choices. Each item can only appear once.
 *
 * Provide a combo box for each value selected while ensuring that their choices only contain unselected values. If the last combo box holds the only
 * remaining choice then ensure it gets disabled, the user can only delete it or the previous ones. If the user deletes a previous one then re-enable
 * the last one and add the deleted value to its set of choices.
 *
 * @author Brian Remedios
 *
 *  ! PLACEHOLDER ONLY - NOT FINISHED YET !
 */
public class MultiEnumerationEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiEnumerationEditorFactory instance = new MultiEnumerationEditorFactory();

	private MultiEnumerationEditorFactory() { }

    private static EnumeratedMultiProperty<?> enumerationPropertyFrom(PropertyDescriptor<?> desc) {

        if (desc instanceof PropertyDescriptorWrapper<?>) {
           return (EnumeratedMultiProperty<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (EnumeratedMultiProperty<?>)desc;
        }
    }

	@Override
	protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, PropertySource source) {

		int idx = ((Combo)widget).getSelectionIndex();
		if (idx < 0) return null;

		String newValue = ((Combo)widget).getItem(idx);

	    String[] currentValues = (String[])valueFor(source, desc);
	    String[] newValues = CollectionUtil.addWithoutDuplicates(currentValues, newValue);
	    if (currentValues.length == newValues.length) return null;

	    source.setProperty((EnumeratedMultiProperty<?>)desc, newValues);
	    return newValue;
	}

	/**
	 * Only add a new widget row if there are any remaining choices to make
	 */
	@Override
    protected boolean canAddNewRowFor(PropertyDescriptor<?> desc, PropertySource source) {

    	Object[] choices = desc.choices();
		Object[] values = (Object[])source.getProperty(desc);

		return choices.length > values.length;
    }

	@Override
	protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, final PropertySource source) {

        final Combo combo = new Combo(parent, SWT.READ_ONLY);

        final EnumeratedMultiProperty<?> ep = enumerationPropertyFrom(desc);

  // TODO remove all choices already chosen by previous widgets
        combo.setItems(SWTUtil.labelsIn(ep.choices(), 0));
        int selectionIdx = EnumerationEditorFactory.indexOf(value, ep.choices());
        if (selectionIdx >= 0) combo.select(selectionIdx);
        
        return combo;
	}

	@Override
	protected void setValue(Control widget, Object value) {
		// not necessary, set in addWidget method?
	}

	@Override
	protected void update(PropertySource source, PropertyDescriptor<?> desc, List<Object> newValues) {
		source.setProperty((EnumeratedMultiProperty<?>)desc, newValues.toArray(new String[newValues.size()]));
	}

	@Override
	protected Object valueFrom(Control valueControl) {			// unreferenced method?
		return null;
	}

	public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
		return null;
	}

	@Override
	protected void configure(Text text, PropertyDescriptor<?> desc, PropertySource source, ValueChangeListener listener) {
		text.setEditable(false);
	}

}
