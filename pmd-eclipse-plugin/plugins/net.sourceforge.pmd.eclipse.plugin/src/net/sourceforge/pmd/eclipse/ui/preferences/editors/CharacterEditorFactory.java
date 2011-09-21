package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
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
public class CharacterEditorFactory extends AbstractEditorFactory {

    public static final CharacterEditorFactory instance = new CharacterEditorFactory();

    private CharacterEditorFactory() {  }

    public PropertyDescriptor<?> createDescriptor(String name, String description, Control[] otherData) {

        return new CharacterProperty(
                name,
                description,
                'a',
                0);
    }

    protected Object valueFrom(Control valueControl) {

        String value = ((Text)valueControl).getText().trim();

        return (StringUtil.isEmpty(value) || value.length() > 1) ?
                null :
                Character.valueOf(value.charAt(0));
    }

    /**
     * Method fillWidget.
     * @param textWidget Text
     * @param desc PropertyDescriptor<?>
     * @param rule Rule
     */
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, PropertySource source) {
        Character val = (Character)valueFor(source, desc);
        textWidget.setText(val == null ? "" : val.toString());
    }

    private static Character charValueIn(Text textControl) {
        String newValue = textControl.getText().trim();
        if (newValue.length() == 0) return null;
        return Character.valueOf(newValue.charAt(0));
    }

    private static CharacterProperty characterPropertyFrom(PropertyDescriptor<?> desc) {

        if (desc instanceof PropertyDescriptorWrapper<?>) {
           return (CharacterProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (CharacterProperty)desc;
        }
    }

    public Control newEditorOn(Composite parent, final PropertyDescriptor<?> desc, final PropertySource source, final ValueChangeListener listener, SizeChangeListener sizeListener) {

        final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);

        fillWidget(text, desc, source);

        final CharacterProperty cp = characterPropertyFrom(desc); // TODO - really necessary?

        text.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                Character newValue = charValueIn(text);
                Character existingValue = (Character)valueFor(source, cp);
                if (existingValue.equals(newValue)) return;

                source.setProperty(cp, newValue);
                listener.changed(source, cp, newValue);
                adjustRendering(source, desc, text);
                }
            });

        return text;
        }
}
