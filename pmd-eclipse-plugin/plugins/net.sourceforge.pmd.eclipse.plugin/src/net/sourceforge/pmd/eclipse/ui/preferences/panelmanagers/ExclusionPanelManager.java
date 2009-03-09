package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.ColourManager;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Manages the UI form for rule exclusion fields for a designated property rule.
 * 
 * @author Brian Remedios
 */
public class ExclusionPanelManager extends AbstractRulePanelManager {
	
	private Text 						excludeWidget;
	private Text 						xpathWidget;
	private Composite					excludeColour;
	private Composite					xPathColour;
	private ColourManager				colourManager;	
	
	/**
	 * Constructor for ExclusionPanelManager.
	 * @param listener ValueChangeListener
	 */
	public ExclusionPanelManager(ValueChangeListener listener) {
		super(listener);
	}
	
	protected boolean canManageMultipleRules() { return true; }	 
    
    protected void clearControls() {
        excludeWidget.setText("");
        xpathWidget.setText("");
    }
	
    protected void setVisible(boolean flag) {
        excludeWidget.setVisible(flag);
        xpathWidget.setVisible(flag);
    }
    
    protected String[] fieldErrors() {
        return StringUtil.EMPTY_STRINGS;
    }
    
	private void addListeners(final Text control, final StringProperty desc, final Control colourWindow) {
		
		addTextListeners(control, desc);
		
		control.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {				
				colourWindow.setBackground(
					colourManager.colourFor(control.getText())
					);

                changeListener.changed(rules, null, null); 
			}			
		});
	}
	
	private Composite newColourPanel(Composite parent, String label) {
	    
	    Composite panel = new Composite(parent, SWT.None);
	    
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 0;     layout.horizontalSpacing = 0;
        layout.marginHeight = 0;        layout.marginWidth = 0;
        panel.setLayout(layout);
	    
	    Label labelWidget = new Label(panel, SWT.None);
	    labelWidget.setText(label);
	    
	    Composite clrPanel = new Composite(panel, SWT.BORDER);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.heightHint = 15;
        gridData.widthHint = 15;
        gridData.grabExcessHorizontalSpace = false;
        clrPanel.setLayoutData(gridData);
        
        return clrPanel;
	}
	
	/**
	 * @param parent Composite
	 * @param regexExclusionLabel String
	 * @param xpathExclusionLabel String
	 * @return Control
	 */
	public Control setupOn(Composite parent, String regexExclusionLabel, String xpathExclusionLabel, String colourBoxLabel) {
				
		colourManager = ColourManager.managerFor(parent.getDisplay());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		Composite panel = new Composite(parent, 0);
	    GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);
		
		Label labelA = new Label(panel, 0);
		labelA.setText(regexExclusionLabel);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
	    labelA.setLayoutData(gridData);
	    
        excludeColour = newColourPanel(panel, colourBoxLabel);
               
		excludeWidget = newTextField(panel);
		gridData = new GridData(GridData.FILL_BOTH);
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalSpan = 2;
		excludeWidget.setLayoutData(gridData);
				
		addListeners(excludeWidget, Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, excludeColour);
				
		Label labelB = new Label(panel, 0);
		labelB.setText(xpathExclusionLabel);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalSpan = 1;
	    labelB.setLayoutData(gridData);

        xPathColour = newColourPanel(panel, colourBoxLabel);
	    
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
		xpathWidget = newTextField(panel);
		xpathWidget.setLayoutData(gridData);
		
		addListeners(xpathWidget, Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, xPathColour);
		
		panel.pack();
		
		return panel;
	}
	
	protected void adapt() {
				
		if (rules == null) {
			shutdown(excludeWidget);
			shutdown(xpathWidget);
			return;
		}
		
		show(excludeWidget, rules.commonStringValue(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR));
		show(xpathWidget, rules.commonStringValue(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR));
	}
}

