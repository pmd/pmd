package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.EnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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

/**
 * 
 * @author Brian Remedios
 */
public class XPathPanelManager extends AbstractRulePanelManager {

    private StyledText      xpathField;
    private Combo           xpathVersionField;
    private List<String>    unknownVariableNames;
    
    public XPathPanelManager(ValueChangeListener theListener) {
        super(theListener);
    }

    protected boolean canManageMultipleRules() { return false; }
    
    protected boolean canWorkWith(Rule rule) {
        return rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR);
    }
    
    protected String[] fieldErrors() {
        
        if (StringUtil.isEmpty(xpathField.getText().trim())) return  new String[] { "Missing XPATH code" };
        
        if (unknownVariableNames == null || unknownVariableNames.isEmpty()) return StringUtil.EMPTY_STRINGS;
        
        return new String[] {
            "Unknown variables: " + unknownVariableNames 
            };
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
        
        xpathField = new StyledText(panel, SWT.BORDER); 
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
                updateVariablesField();
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
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        xpathVersionField.setLayoutData(gridData); 
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
    
    private static StyleRange styleFor(Rule rule, String source, int[] position, List<String> unknownVars) {
        
        String varName = source.substring(position[0], position[0] + position[1]);
        PropertyDescriptor<?> desc = rule.getPropertyDescriptor(varName);
        
        if (desc == null) unknownVars.add(varName);
        
        return new StyleRange(
                position[0], position[1], 
                desc == null ? errorColour : null, 
                null, 
                SWT.BOLD
                );
    }
    
    private void updateVariablesField() {
                
        xpathField.setStyleRange(null);     // clear all

        Rule rule = soleRule();
        unknownVariableNames = new ArrayList<String>();
        
        String xpath = rule.getProperty(XPathRule.XPATH_DESCRIPTOR).trim();
        List<int[]> positions = Util.referencedNamePositionsIn(xpath, '$');
        for (int[] position : positions) {
            StyleRange range = styleFor(rule, xpath, position, unknownVariableNames);        
            xpathField.setStyleRange(range);
        }
    }
    
    protected void adapt() {
        
        Rule soleRule = soleRule();
        
        if (soleRule == null) {
            shutdown(xpathField);
        } else {
            show(xpathField, soleRule.getProperty(XPathRule.XPATH_DESCRIPTOR).trim());
            configureVersionFieldFor(soleRule);
            updateVariablesField();
        }
    }

}
