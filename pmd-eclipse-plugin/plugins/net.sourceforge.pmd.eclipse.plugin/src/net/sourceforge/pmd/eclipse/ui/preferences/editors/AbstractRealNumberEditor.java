package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.Rule;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractRealNumberEditor extends AbstractNumericEditorFactory {

    protected static final int digits = 3;
    
    protected static final double scale = Math.pow(10, digits);
    
    protected AbstractRealNumberEditor() {
    }

    protected Spinner newSpinnerFor(Composite parent, Rule rule, NumericPropertyDescriptor<?> numDesc) {
        
        Spinner spinner = newSpinnerFor(parent, digits);
        int min = (int)(numDesc.lowerLimit().doubleValue() * scale);
        int max = (int)(numDesc.upperLimit().doubleValue() * scale);
        spinner.setMinimum(min);
        spinner.setMaximum(max);
        
        Number value = ((Number)valueFor(rule, numDesc));
        if (value != null) {
        	int intVal = (int)(value.doubleValue() * scale);
        	spinner.setSelection(intVal);
        }
        
        return spinner;
    }

}
