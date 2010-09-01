package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 *
 * @author Brian Remedios
 */
public abstract class AbstractNumericEditorFactory extends AbstractEditorFactory {

    public static final int defaultMinimum = 0;
    public static final int defaultMaximum = 1000;

    protected AbstractNumericEditorFactory() {
    }

    protected static Spinner newSpinnerFor(Composite parent, int digits) {

        Spinner spinner = new Spinner(parent, SWT.SINGLE | SWT.BORDER);
        spinner.setDigits(digits);
        return spinner;
    }

    protected int digitPrecision() {
        return 0;
    }

    public Control[] createOtherControlsOn(Composite parent, PropertyDescriptor<?> desc, Rule rule, ValueChangeListener listener, SizeChangeListener sizeListener) {

        Label defaultLabel = newLabel(parent, SWTUtil.stringFor(StringKeys.RULEEDIT_LABEL_DEFAULT));
        Control valueControl = newEditorOn(parent, desc, rule, listener, sizeListener);

        Label minLabel = newLabel(parent, SWTUtil.stringFor(StringKeys.RULEEDIT_LABEL_MIN));
        Spinner minWidget = newSpinnerFor(parent, digitPrecision());
        Label maxLabel = newLabel(parent, SWTUtil.stringFor(StringKeys.RULEEDIT_LABEL_MAX));
        Spinner maxWidget = newSpinnerFor(parent, digitPrecision());

        linkup(minWidget, (Spinner)valueControl, maxWidget);

        return new Control[] {
            defaultLabel, valueControl,
            minLabel,     minWidget,
            maxLabel,     maxWidget
            };
    }

    private void linkup(final Spinner minWidget, final Spinner valueWidget, final Spinner maxWidget) {
        minWidget.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                adjustForMin(minWidget, valueWidget, maxWidget);
            }
        });

        valueWidget.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                adjustForValue(minWidget, valueWidget, maxWidget);
            }
        });

        maxWidget.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                adjustForMax(minWidget, valueWidget, maxWidget);
            }
        });
    }

    private void adjustForMin(Spinner minWidget, Spinner valueWidget, Spinner maxWidget) {
        int min = minWidget.getSelection();
        if (valueWidget.getSelection() < min) valueWidget.setSelection(min);
        if (maxWidget.getSelection() < min) maxWidget.setSelection(min);
    }

    private void adjustForValue(Spinner minWidget, Spinner valueWidget, Spinner maxWidget) {
        int value = valueWidget.getSelection();
        if (minWidget.getSelection() > value) minWidget.setSelection(value);
        if (maxWidget.getSelection() < value) maxWidget.setSelection(value);
    }

    private void adjustForMax(Spinner minWidget, Spinner valueWidget, Spinner maxWidget) {
        int max = maxWidget.getSelection();
        if (valueWidget.getSelection() > max) valueWidget.setSelection(max);
        if (minWidget.getSelection() > max) minWidget.setSelection(max);
    }

    protected Number defaultIn(Control[] controls) {

        return controls == null ?
                Integer.valueOf(defaultMinimum) :
                (Number)valueFrom(controls[1]);
    }

    protected Number minimumIn(Control[] controls) {

        return controls == null ?
                Integer.valueOf(0) :
                (Number)valueFrom(controls[3]);
    }

    protected Number maximumIn(Control[] controls) {

        return controls == null ?
                Integer.valueOf(defaultMaximum) :
                (Number)valueFrom(controls[5]);
    }
}
