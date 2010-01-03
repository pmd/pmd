package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * @author Brian Remedios
 * 
 *  ! PLACEHOLDER ONLY - NOT FINISHED YET !
 */
public class MultiEnumerationEditorFactory extends AbstractMultiValueEditorFactory {

    public static final MultiEnumerationEditorFactory instance = new MultiEnumerationEditorFactory();
    
	private MultiEnumerationEditorFactory() { }

    private static EnumeratedMultiProperty<?> enumerationPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (EnumeratedMultiProperty<?>) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (EnumeratedMultiProperty<?>)desc;
        }
    }
	
	@Override
	protected Object addValueIn(Control widget, PropertyDescriptor<?> desc, Rule rule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Control addWidget(Composite parent, Object value, PropertyDescriptor<?> desc, Rule rule) {
		
        final Combo combo = new Combo(parent, SWT.READ_ONLY);
        
        final EnumeratedMultiProperty<?> ep = enumerationPropertyFrom(desc);
  // TODO remove all choices already chosen by previous widgets
        combo.setItems(SWTUtil.labelsIn(ep.choices(), 0));
        int selectionIdx = EnumerationEditorFactory.indexOf(value, ep.choices());
        if (selectionIdx >= 0) combo.select(selectionIdx);
		
        return combo;
	}

	@Override
	protected void configure(Text text, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setValue(Control widget, Object value) {
		// not necessary, set in addWidget method?
	}

	@Override
	protected void update(Rule rule, PropertyDescriptor<?> desc, List<Object> newValues) {
		rule.setProperty((EnumeratedMultiProperty<?>)desc, newValues.toArray(new String[newValues.size()]));
	}

	@Override
	protected Object valueFrom(Control valueControl) {
		// unreferenced method?
		return null;
	}

	public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {
		// TODO Auto-generated method stub
		return null;
	}

}
