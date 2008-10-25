package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

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
	
	private void addListeners(final Text control, final StringProperty desc, final Control colourWindow) {
		
		addTextListeners(control, desc);
		
		control.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {				
				colourWindow.setBackground(
					colourManager.colourFor(control.getText())
					);
			}			
		});
	}
	
	/**
	 * @param parent Composite
	 * @param regexExclusionLabel String
	 * @param xpathExclusionLabel String
	 * @return Control
	 */
	public Control setupOn(Composite parent, String regexExclusionLabel, String xpathExclusionLabel) {
				
		colourManager = new ColourManager(parent.getDisplay());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		Composite panel = new Composite(parent, 0);
	    GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);
		
		Label labelA = new Label(panel, 0);
		labelA.setText(regexExclusionLabel);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalSpan = 2;
	    labelA.setLayoutData(gridData);
	    
	    gridData.grabExcessHorizontalSpace = true;
		excludeWidget = new Text(panel, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalSpan = 1;
		excludeWidget.setLayoutData(gridData);
				
		excludeColour = new Composite(panel, SWT.BORDER);
		addListeners(excludeWidget, Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, excludeColour);
				
		Label labelB = new Label(panel, 0);
		labelB.setText(xpathExclusionLabel);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
	    gridData.horizontalSpan = 2;
	    labelB.setLayoutData(gridData);
	    
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
	    gridData.grabExcessHorizontalSpace = true;
		xpathWidget = new Text(panel, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		xpathWidget.setLayoutData(gridData);
		
		xPathColour = new Composite(panel, SWT.BORDER);
		xPathColour.setSize(1,1);
		addListeners(xpathWidget, Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, xPathColour);
		
		panel.pack();
		
		return panel;
	}
	
	/**
	 * Method showRule.
	 * @param rule Rule
	 */
	public void showRule(Rule rule) {
		
		currentRule = rule;
		
		if (rule == null) {
			shutdown(excludeWidget);
			shutdown(xpathWidget);
			return;
		}
		
		show(excludeWidget, rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR));
		show(xpathWidget, rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR));
	}
}

