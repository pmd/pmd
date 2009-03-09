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
        spinner.setMinimum((int)(numDesc.lowerLimit().doubleValue() * scale));
        spinner.setMaximum((int)(numDesc.upperLimit().doubleValue() * scale));
        
        double value = ((Number)valueFor(rule, numDesc)).doubleValue();        
        spinner.setSelection((int)(value * scale));
        
        return spinner;
    }

}
