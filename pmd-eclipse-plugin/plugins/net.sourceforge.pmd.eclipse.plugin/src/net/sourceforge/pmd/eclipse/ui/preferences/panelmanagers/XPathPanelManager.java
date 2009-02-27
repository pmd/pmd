package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.EnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class XPathPanelManager extends AbstractRulePanelManager {

    private Text  xpathField;
    private Combo xpathVersionField;
    
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
        xpathVersionField.setVisible(flag);
    }
    
    protected void updateOverridenFields() {
        
        Rule rule = soleRule();
        
        if (rule instanceof RuleReference) {
            RuleReference ruleReference = (RuleReference)rule;
            xpathField.setBackground(ruleReference.hasOverriddenProperty(XPathRule.XPATH_DESCRIPTOR) ? overridenColour: null);
        }
    }
    
    public Control setupOn(Composite parent) {
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        
        Composite panel = new Composite(parent, 0);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);
        
        xpathField = newTextField(panel); 
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;
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

        Label versionLabel = new Label(panel, 0);
        versionLabel.setText("XPath version:");
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        versionLabel.setLayoutData(gridData);        
        
        final EnumeratedProperty<String> ep = XPathRule.VERSION_DESCRIPTOR;        
        xpathVersionField = new Combo(panel, SWT.READ_ONLY); 
        xpathVersionField.setItems(SWTUtil.labelsIn(ep.choices(), 0));        
        
        xpathVersionField.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Rule rule = soleRule();
                int selectionIdx = xpathVersionField.getSelectionIndex();
                Object newValue = ep.choices()[selectionIdx][1];                    
                if (newValue.equals(rule.getProperty(ep))) return;
                
                rule.setProperty(ep, newValue);
    //          adjustRendering(rule, ep, xpathVersionField);   TODO - won't compile?
            }
          }); 
                
        return panel;    
    }
    
    private void configureVersionFieldFor(Rule rule) {
        
        Object value = rule.getProperty(XPathRule.VERSION_DESCRIPTOR);
        int selectionIdx = EnumerationEditorFactory.indexOf(value, XPathRule.VERSION_DESCRIPTOR.choices());
        if (selectionIdx >= 0) xpathVersionField.select(selectionIdx);
    }
    
    protected void adapt() {
        
        Rule soleRule = soleRule();
        
        if (soleRule == null) {
            shutdown(xpathField);
        } else {
            show(xpathField, soleRule.getProperty(XPathRule.XPATH_DESCRIPTOR).trim());
            configureVersionFieldFor(soleRule);
        }
    }

}
