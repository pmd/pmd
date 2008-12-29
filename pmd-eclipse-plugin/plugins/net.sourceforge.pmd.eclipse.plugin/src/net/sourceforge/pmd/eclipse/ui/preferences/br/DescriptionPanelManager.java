package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
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
public class DescriptionPanelManager extends AbstractRulePanelManager {

    private Text descriptionBox;
    
    public DescriptionPanelManager(ValueChangeListener theListener) {
        super(theListener);
    }

    public Control setupOn(Composite parent) {
        
        descriptionBox = buildDescriptionBox(parent);       
      
        descriptionBox.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                               
                if (currentRule == null) return;
                
                String cleanValue = descriptionBox.getText().trim();
                String existingValue = currentRule.getDescription();
                
                if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;
                
                currentRule.setDescription(cleanValue);
                changeListener.changed(currentRule, null, cleanValue);                
            }
        }); 
        
        return descriptionBox;    
    }
    
    /**
     * Method buildDescriptionBox.
     * @param parent Composite
     * @return Text
     */
    private Text buildDescriptionBox(Composite parent) {
        
        return new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI);
    }
    
    public void showRule(Rule rule) {
        
        currentRule = rule;
        
        if (rule == null) {
            shutdown(descriptionBox);
        } else {
            show(descriptionBox, rule.getDescription().trim());
        }
    }

}
