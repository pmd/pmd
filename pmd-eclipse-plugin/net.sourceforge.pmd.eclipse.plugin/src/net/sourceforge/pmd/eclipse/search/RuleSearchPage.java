package net.sourceforge.pmd.eclipse.search;

import java.util.List;

import net.sourceforge.pmd.lang.Language;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Brian Remedios
 */
public class RuleSearchPage extends DialogPage implements ISearchPage {

	private Text idText;
	private Button caseSensitive;
	
	private String selected;

	private Button name;
	private Button description;
	private Button example;
	private Button xpath;
	private Combo language;
	
	public RuleSearchPage() {
	}

	public RuleSearchPage(String title) {
		super(title);
	}

	public RuleSearchPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public boolean performAction() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setContainer(ISearchPageContainer container) {
		if (container.getSelection() instanceof TextSelection) {
			selected = ((TextSelection) container.getSelection()).getText();
		}
	}

    public void buildLanguageCombo(Composite parent) {
   	 
    	final List<Language> languages = Language.findWithRuleSupport();

 		language = new Combo(parent, SWT.READ_ONLY);

 		Language deflt = Language.getDefaultLanguage();
 		int selectionIndex = -1;

 		for (int i = 0; i < languages.size(); i++) {
 			if (languages.get(i) == deflt) selectionIndex = i;
 			language.add(languages.get(i).getName());
 		}
 		
 		language.select(selectionIndex);
     }
	
	private void addButtons(Composite parent, int horizSpan) {
		
		Group group = new Group(parent, SWT.BORDER);
		group.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true, horizSpan, 1));
		group.setLayout( new GridLayout(2, false));
		group.setText("Scope");
		
		name = new Button(group, SWT.CHECK);
		name.setText("Names");
		
		description = new Button(group, SWT.CHECK);
		description.setText("Descriptions");
		
		example = new Button(group, SWT.CHECK);
		example.setText("Examples");
		
		xpath = new Button(group, SWT.CHECK);
		xpath.setText("XPaths");
	}
	
	public void createControl(Composite parent) {
		
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL, true, true, 2, 1));
		panel.setLayout( new GridLayout(3, false));
		
		Composite textPanel = new Composite(panel, SWT.None);
		textPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, true, true, 3, 1));
		textPanel.setLayout(new GridLayout(4, false));
		
			Label label = new Label(textPanel, SWT.BORDER);
			label.setText("Containing text:");
			label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 4, 1));
		
			idText = new Text(textPanel, SWT.BORDER);
		//	idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, true, false, 3, 1));
			
		    GridData gridData2 = new GridData();
		    gridData2.horizontalSpan=3;
	//	    gridData2.grabExcessHorizontalSpace = true;
	//	    gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		    idText.setLayoutData(gridData2);
			
			
			if (selected != null) {
				idText.setText(selected);
				idText.setSelection(0, selected.length());
			}
			
			caseSensitive = new Button(textPanel, SWT.CHECK);
			caseSensitive.setText("Case sensitive");
			caseSensitive.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
		
//		addButtons(panel, 4);
//		
//		Label langLabel = new Label(panel, SWT.None);
//		langLabel.setText("Language");
//		buildLanguageCombo(panel);
		
		setControl(panel);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		idText.setFocus();
	}


}
