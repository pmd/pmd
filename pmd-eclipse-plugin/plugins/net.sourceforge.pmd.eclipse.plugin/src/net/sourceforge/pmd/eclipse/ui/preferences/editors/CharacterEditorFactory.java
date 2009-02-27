package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

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

    /**
     * Method fillWidget.
     * @param textWidget Text
     * @param desc PropertyDescriptor<?>
     * @param rule Rule
     */
    protected void fillWidget(Text textWidget, PropertyDescriptor<?> desc, Rule rule) {
        Character val = (Character)rule.getProperty(desc);
        textWidget.setText(val == null ? "" : val.toString());
    }
    
    private static Character charValueIn(Text textControl) {
        String newValue = textControl.getText().trim();
        if (newValue.length() == 0) return null;
        return Character.valueOf(newValue.charAt(0));
    }
    
    private static CharacterProperty characterPropertyFrom(PropertyDescriptor<?> desc) {
        
        if (desc instanceof PropertyDescriptorWrapper) {
           return (CharacterProperty) ((PropertyDescriptorWrapper<?>)desc).getPropertyDescriptor();
        } else {
            return (CharacterProperty)desc;
        }
    }
    
    public Control newEditorOn(Composite parent, int columnIndex, final PropertyDescriptor<?> desc, final Rule rule, final ValueChangeListener listener, SizeChangeListener sizeListener) {
       
        if (columnIndex == 0) return addLabel(parent, desc);    
        
        if (columnIndex == 1) {
            
            final Text text =  new Text(parent, SWT.SINGLE | SWT.BORDER);

            fillWidget(text, desc, rule);
                        
            final CharacterProperty cp = characterPropertyFrom(desc); // TODO - really necessary?
            
            text.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
                    Character newValue = charValueIn(text);
                    Character existingValue = rule.getProperty(cp);                
                    if (existingValue.equals(newValue)) return;              
                    
                    rule.setProperty(cp, newValue);
                    listener.changed(rule, cp, newValue);

                    adjustRendering(rule, desc, text);
                }
            });

            return text;
        
        
        }
        
        return null;
    }

}
