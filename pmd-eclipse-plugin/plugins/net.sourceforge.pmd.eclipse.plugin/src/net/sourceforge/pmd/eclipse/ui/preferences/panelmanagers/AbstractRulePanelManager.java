package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractRulePanelManager implements RulePropertyManager {

    protected RuleSelection             rules;
    final protected ValueChangeListener changeListener;
    
    public AbstractRulePanelManager(ValueChangeListener theListener) {
        changeListener = theListener;
    }

    public void manage(RuleSelection theRules) {
        rules = theRules;
        
        if (rules.hasMultipleRules() && !canManageMultipleRules()) {
            setVisible(false);
            clearControls();
            return;
        }
        
        setVisible(true);
        adapt();
    }
    
    protected void addTextListeners(final Text control, final StringProperty desc) {
        
        control.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                changed(desc, control.getText());
            }
        });
    }
    
    protected abstract boolean canManageMultipleRules();
    
    protected abstract void adapt();
    
    protected abstract void clearControls();
    
    protected abstract void setVisible(boolean flag);
    
    protected Rule soleRule() {
        return rules.soleRule();
    }
    
    /**
     * @param property StringProperty
     * @param newValue String
     */
    protected void changed(StringProperty property, String newValue) {
        
        if (rules == null) return;
        
        String cleanValue = newValue.trim();
        String existingValue = rules.commonStringValue(property);
        
        if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;
        
        rules.setValue(property, cleanValue);
        changeListener.changed(rules, property, cleanValue);
    }
    
    protected void shutdown(Text control) {
        control.setText("");
        control.setEnabled(false);
    }
  
    protected void shutdown(Link control) {
        control.setText("");
        control.setEnabled(false);
    }
    
    protected void show(Text control, String value) {
        control.setText(value == null ? "" : value);
        control.setEnabled(true);
    }
    
    protected void show(Link control, String value) {
        control.setText(value == null ? "" : value);
        control.setEnabled(true);
    }
}
