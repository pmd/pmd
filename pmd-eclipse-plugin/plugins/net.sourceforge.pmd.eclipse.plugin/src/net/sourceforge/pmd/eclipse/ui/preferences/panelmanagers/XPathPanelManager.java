package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class XPathPanelManager extends AbstractRulePanelManager {

    private Text xpathField;
    
    public XPathPanelManager(ValueChangeListener theListener) {
        super(theListener);
    }

    protected boolean canManageMultipleRules() { return false; }
    
    protected boolean canWorkWith(Rule rule) {
        return rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR);
    }
    
    protected String[] fieldErrors() {
        
        return StringUtil.isEmpty(xpathField.getText().trim()) ?
              new String[] { "Missing XPATH code" } :
              StringUtil.EMPTY_STRINGS;
    }
    
    protected void clearControls() {
        xpathField.setText("");
    }
    
    protected void setVisible(boolean flag) {        
        xpathField.setVisible(flag);
    }
    
    public Control setupOn(Composite parent) {
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        
        Composite panel = new Composite(parent, 0);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);
        
        xpathField = newTextField(panel); 
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 1;
        xpathField.setLayoutData(gridData);              
      
        xpathField.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                               
                Rule soleRule = soleRule();
                
                String newValue = xpathField.getText().trim();
                String existingValue = soleRule.getProperty(XPathRule.XPATH_DESCRIPTOR).trim();
                
                if (StringUtil.areSemanticEquals(existingValue, newValue)) return;
                
                soleRule.setProperty(XPathRule.XPATH_DESCRIPTOR, newValue);
                valueChanged(XPathRule.XPATH_DESCRIPTOR, newValue);        
            }
        }); 
        
        return panel;    
    }
    
    protected void adapt() {
        
        Rule soleRule = soleRule();
        
        if (soleRule == null) {
            shutdown(xpathField);
        } else {
            show(xpathField, soleRule.getProperty(XPathRule.XPATH_DESCRIPTOR).trim());
        }
    }

}
