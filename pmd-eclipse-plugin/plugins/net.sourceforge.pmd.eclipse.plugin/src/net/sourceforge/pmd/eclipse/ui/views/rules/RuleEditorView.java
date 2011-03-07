package net.sourceforge.pmd.eclipse.ui.views.rules;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.ui.ModifyListener;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.PMDPreferencePage2;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelectionListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleTableManager;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueResetHandler;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.RulePropertyManager;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

/**
 * @deprecated  - temporary (don't add code here, demo/testing view only)
 * 
 * @author br
 *
 */
public class RuleEditorView extends ViewPart implements RuleSelectionListener, ModifyListener, ValueChangeListener, ValueResetHandler {

	private TabFolder 		     	tabFolder;
	private RulePropertyManager[]   rulePropertyManagers;
	private RuleTableManager		tableManager;
	    
    private IPreferences preferences = PMDPlugin.getDefault().loadPreferences();

	protected static PMDPlugin		plugin = PMDPlugin.getDefault();
	
	// columns shown in the rule treetable in the desired order
	private static final RuleColumnDescriptor[] availableColumns = PMDPreferencePage2.availableColumns;

	// last item in this list is the grouping used at startup
	private static final Object[][] groupingChoices = PMDPreferencePage2.groupingChoices;
	
	
	public RuleEditorView() {

	}
	
	protected String descriptionId() {
		return StringKeys.PREF_RULESET_TITLE;
	}

	@Override
	public void createPartControl(Composite parent) {

		tableManager = new RuleTableManager("rules", availableColumns, PMDPlugin.getDefault().loadPreferences(), this);
		tableManager.modifyListener(this);
		tableManager.selectionListener(this);

	    populateRuleset();

		Composite composite = new Composite(parent, SWT.NULL);
		layoutControls(composite);

		tableManager.populateRuleTable();
		int i =  PreferenceUIStore.instance.selectedPropertyTab() ;
		tabFolder.setSelection( i );

	}

	private Composite createRuleSection(Composite parent) {

	    Composite ruleSection = new Composite(parent, SWT.NULL);

	    // Create the controls (order is important !)
        Composite groupCombo = tableManager.buildGroupCombo(ruleSection, StringKeys.PREF_RULESET_RULES_GROUPED_BY, groupingChoices);

	    Tree ruleTree = tableManager.buildRuleTreeViewer(ruleSection);
	    tableManager.groupBy(null);

        Composite ruleTableButtons = tableManager.buildRuleTableButtons(ruleSection);
        Composite rulePropertiesTableButtons = PMDPreferencePage2.buildRulePropertiesTableButtons(ruleSection);

        // Place controls on the layout
        GridLayout gridLayout = new GridLayout(3, false);
        ruleSection.setLayout(gridLayout);

        GridData data = new GridData();
        data.horizontalSpan = 3;
        groupCombo.setLayoutData(data);

        data = new GridData();
        data.heightHint = 200;                          data.widthHint = 350;
        data.horizontalSpan = 1;
        data.horizontalAlignment = GridData.FILL;       data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;          data.grabExcessVerticalSpace = true;
        ruleTree.setLayoutData(data);

        data = new GridData();
        data.horizontalSpan = 1;
        data.horizontalAlignment = GridData.FILL;       data.verticalAlignment = GridData.FILL;
        ruleTableButtons.setLayoutData(data);

        data = new GridData();
        data.horizontalSpan = 1;
        data.horizontalAlignment = GridData.FILL;       data.verticalAlignment = GridData.FILL;
        rulePropertiesTableButtons.setLayoutData(data);

        return ruleSection;
	}

	/**
	 * Method buildTabFolder.
	 * @param parent Composite
	 * @return TabFolder
	 */
	private TabFolder buildTabFolder(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.TOP);

		rulePropertyManagers = PMDPreferencePage2.buildPropertyManagersOn(tabFolder, this);

		tabFolder.pack();
		return tabFolder;
	}

	public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) {
	        // TODO enhance to recognize default values
	     setModified();
	     tableManager.updated(rule);
	}

	public void changed(RuleSelection selection, PropertyDescriptor<?> desc, Object newValue) {
			// TODO enhance to recognize default values

		for (Rule rule : selection.allRules()) {
			if (newValue != null) {		// non-reliable update behaviour, alternate trigger option - weird
				tableManager.changed(selection, desc, newValue);
			//		System.out.println("doing redraw");
			} else {
				tableManager.changed(rule, desc, newValue);
			//		System.out.println("viewer update");
			}
		}
		for (RulePropertyManager manager : rulePropertyManagers) {
		    manager.validate();
		}

		setModified();
	}

	/**
     * Main layout
     * @param parent Composite
     */
    private void layoutControls(Composite parent) {

        parent.setLayout(new FormLayout());
        int ruleTableFraction = 55;	//PreferenceUIStore.instance.tableFraction();

        // Create the sash first, so the other controls can be attached to it.
        final Sash sash = new Sash(parent, SWT.HORIZONTAL);
        FormData data = new FormData();
        data.left = new FormAttachment(0, 0);                   // attach to left
        data.right = new FormAttachment(100, 0);                // attach to right
        data.top = new FormAttachment(ruleTableFraction, 0);
        sash.setLayoutData(data);
        sash.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            // Re-attach to the top edge, and we use the y value of the event to determine the offset from the top
            ((FormData)sash.getLayoutData()).top = new FormAttachment(0, event.y);
//            PreferenceUIStore.instance.tableFraction(event.y);
            sash.getParent().layout();
          }
        });

        // Create the first text box and attach its bottom edge to the sash
        Composite ruleSection = createRuleSection(parent);
        data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(sash, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        ruleSection.setLayoutData(data);

        // Create the second text box and attach its top edge to the sash
        TabFolder propertySection = buildTabFolder(parent);
        data = new FormData();
        data.top = new FormAttachment(sash, 0);
        data.bottom = new FormAttachment(100, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        propertySection.setLayoutData(data);
    }

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */

    public void performOk() {

		saveUIState();

//		if (isModified()) {
//			updateRuleSet();
//			rebuildProjects();
//			storeActiveRules();
//		}
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */

    protected void performDefaults() {
		tableManager.populateRuleTable();
	}

	private void populateRuleset() {

	    RuleSet defaultRuleSet = plugin.getPreferencesManager().getRuleSet();
        RuleSet ruleSet = new RuleSet();
        ruleSet.addRuleSet(defaultRuleSet);
        ruleSet.setName(defaultRuleSet.getName());
        ruleSet.setDescription(Util.asCleanString(defaultRuleSet.getDescription()));
        ruleSet.addExcludePatterns(defaultRuleSet.getExcludePatterns());
        ruleSet.addIncludePatterns(defaultRuleSet.getIncludePatterns());

        tableManager.useRuleSet(ruleSet);
	}

	public void selection(RuleSelection selection) {

		if (rulePropertyManagers == null) return;
		
		for (RulePropertyManager manager : rulePropertyManagers) {
			manager.manage(selection);
		    manager.validate();
		}
	}

	/**
	 * If user wants to, rebuild all projects
	 */
	private void rebuildProjects() {
//		if (MessageDialog.openQuestion(getShell(), getMessage(StringKeys.MSGKEY_QUESTION_TITLE),
//				getMessage(StringKeys.MSGKEY_QUESTION_RULES_CHANGED))) {
//			try {
//				ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
//				monitorDialog.run(true, true, new IRunnableWithProgress() {
//					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//						try {
//							ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
//						} catch (CoreException e) {
//							plugin.logError("Exception building all projects after a preference change", e);
//						}
//					}
//				});
//			} catch (Exception e) {
//				plugin.logError("Exception building all projects after a preference change", e);
//			}
//		}
	}

	private void saveUIState() {
		tableManager.saveUIState();
		int i =  tabFolder.getSelectionIndex();
		PreferenceUIStore.instance.selectedPropertyTab( i );
		PreferenceUIStore.instance.save();
	}


	private void storeActiveRules() {

		List<Rule> chosenRules = tableManager.activeRules();
		for (Rule rule : chosenRules) {
			preferences.isActive(rule.getName(), true);
		}

		System.out.println("Active rules: " + preferences.getActiveRuleNames());
	}

	/**
	 * Update the configured rule set
	 * Update also all configured projects
	 */
	private void updateRuleSet() {
//		try {
//			ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
//			monitorDialog.run(true, true, new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//					plugin.getPreferencesManager().setRuleSet(tableManager.ruleSet());
//				}
//			});
//		} catch (Exception e) {
//			plugin.logError("Exception updating all projects after a preference change", e);
//		}
	}
	
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

	public void setModified() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void resetValuesIn(RuleSelection rules) {
		// TODO Auto-generated method stub
		
	}
}
