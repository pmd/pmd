package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractRulePanelManager implements RulePropertyManager {

    protected Rule                      currentRule;
    final protected ValueChangeListener changeListener;
    
    public AbstractRulePanelManager(ValueChangeListener theListener) {
        changeListener = theListener;
    }

    protected void addTextListeners(final Text control, final StringProperty desc) {
        
        control.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                changed(desc, control.getText());
            }
        });
    }
    
    /**
     * @param property StringProperty
     * @param newValue String
     */
    protected void changed(StringProperty property, String newValue) {
        
        if (currentRule == null) return;
        
        String cleanValue = newValue.trim();
        String existingValue = currentRule.getProperty(property);
        
        if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;
        
        currentRule.setProperty(property, cleanValue);
        changeListener.changed(currentRule, property, cleanValue);
    }
    
    protected void shutdown(Text control) {
        control.setText("");
        control.setEnabled(false);
    }
    
    protected void show(Text control, String value) {
        control.setText(value == null ? "" : value);
        control.setEnabled(true);
    }
}
