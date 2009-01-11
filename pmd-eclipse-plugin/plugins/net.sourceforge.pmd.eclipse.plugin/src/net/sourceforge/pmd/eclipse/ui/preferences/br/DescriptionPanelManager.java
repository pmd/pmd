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

    protected boolean canManageMultipleRules() { return false; }
    
    protected void clearControls() {
        descriptionBox.setText("");
    }
    
    public Control setupOn(Composite parent) {
        
        descriptionBox = buildDescriptionBox(parent);       
      
        descriptionBox.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                               
                Rule soleRule = soleRule();
                
                String cleanValue = descriptionBox.getText().trim();
                String existingValue = soleRule.getDescription();
                
                if (StringUtil.areSemanticEquals(existingValue, cleanValue)) return;
                
                soleRule.setDescription(cleanValue);
                changeListener.changed(rules, null, cleanValue);                
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
    
    protected void adapt() {
        
        Rule soleRule = soleRule();
        
        if (soleRule == null) {
            shutdown(descriptionBox);
        } else {
            show(descriptionBox, soleRule.getDescription().trim());
        }
    }

}
