package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;


public abstract class AbstractNumericEditorFactory extends AbstractEditorFactory {

    protected static final int digits = 3;
    protected static final double scale = Math.pow(10, digits);
    
    protected AbstractNumericEditorFactory() {
    }

    
    protected Spinner newSpinnerFor(Composite parent, Rule rule, NumericPropertyDescriptor<?> numDesc) {
        
        Spinner spinner = new Spinner(parent, SWT.SINGLE | SWT.BORDER);
        spinner.setDigits(digits);
        spinner.setMinimum((int)(numDesc.lowerLimit().doubleValue() * scale));
        spinner.setMaximum((int)(numDesc.upperLimit().doubleValue() * scale));
        
        double value = ((Number)rule.getProperty(numDesc)).doubleValue();        
        spinner.setSelection((int)(value * scale));
        
        return spinner;
    }
}
