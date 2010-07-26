package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleSetSelectionDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.CreateRuleWizard;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.designer.Designer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Instantiates and manages a tree table widget holding all the rules in a ruleset.
 *
 * @author Brian Remedios
 */
public class RuleTableManager extends AbstractTreeTableManager implements RuleSortListener, ValueChangeListener {

	private RuleSet						ruleSet;

	private RuleFieldAccessor 			columnSorter = RuleFieldAccessor.name;
	private RuleColumnDescriptor 		groupingColumn;
	private RuleFieldAccessor 			checkedColumnAccessor;
	private Map<RulePriority, MenuItem> priorityMenusByPriority;
	private Map<String, MenuItem>       rulesetMenusByName;
	
	private RuleSelection               ruleSelection; // may hold rules and/or group nodes

	private Menu                 		ruleListMenu;
private	MenuItem useDefaultsItem; 
	private Button			     		addRuleButton;
	private Button			     		removeRuleButton;

	private final RuleColumnDescriptor[] 		availableColumns;	// columns shown in the rule treetable in the desired order
	private RuleSelectionListener				ruleSelectionListener;

	public static String ruleSetNameFrom(Rule rule) {
		return ruleSetNameFrom( rule.getRuleSetName() );
	}

    public static String ruleSetNameFrom(String rulesetName) {

    	if (rulesetName == null) return null;
    	
        int pos = rulesetName.toUpperCase().indexOf("RULES");
        return pos < 0 ? rulesetName : rulesetName.substring(0, pos-1);
    }
    
	public RuleTableManager(RuleColumnDescriptor[] theColumns, IPreferences thePreferences) {
		super(thePreferences);
		
		availableColumns = theColumns;
		checkedColumnAccessor = createCheckedItemAccessor();
	}
	
	protected boolean isQualifiedItem(Object item) {
		return item instanceof Rule;
	}
	
	public List<Rule> activeRules() {

		Object[] checkedItems = treeViewer.getCheckedElements();
		List<Rule> activeOnes = new ArrayList<Rule>(checkedItems.length);

		for (Object item : checkedItems) {
			if (isQualifiedItem(item)) {
				activeOnes.add((Rule)item);
			}
		}

		return activeOnes;
	}

	public void selectionListener(RuleSelectionListener theListener) {
		ruleSelectionListener = theListener;
	}

	private RuleFieldAccessor createCheckedItemAccessor() {

		return new BasicRuleFieldAccessor() {
			public Comparable<Boolean> valueFor(Rule rule) {
				return isActive(rule.getName());
			}
		};
	}

	private void addRulesetMenuOptions(Menu menu) {

        MenuItem rulesetMenu = new MenuItem(menu, SWT.CASCADE);
        rulesetMenu.setText("Ruleset");
        Menu rulesetSubMenu = new Menu(menu);
        rulesetMenu.setMenu(rulesetSubMenu);
        rulesetMenusByName = new HashMap<String, MenuItem>();

        MenuItem demoItem = new MenuItem(rulesetSubMenu, SWT.PUSH);
        demoItem.setText("---demo only---");    // NO API to re-parent rules to other rulesets (yet)

        for (String rulesetName : rulesetNames()) {
            MenuItem rulesetItem = new MenuItem(rulesetSubMenu, SWT.RADIO);
            rulesetMenusByName.put(rulesetName, rulesetItem);
            rulesetItem.setText(rulesetName);
            final String rulesetStr = rulesetName;
            rulesetItem.addSelectionListener( new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setRuleset(rulesetStr);
                    }
                }
            );
         }
	}

    private void adjustMenuPrioritySettings() {

        RulePriority priority = ruleSelection == null ? null : RuleUtil.commonPriority(ruleSelection);
	    Iterator<Map.Entry<RulePriority, MenuItem>> iter = priorityMenusByPriority.entrySet().iterator();

	    while (iter.hasNext()) {
	        Map.Entry<RulePriority, MenuItem> entry = iter.next();
	        MenuItem item = entry.getValue();
	        if (entry.getKey() == priority) {
	            item.setSelection(true);
	            item.setEnabled(false);
	        } else {
	            item.setSelection(false);
	            item.setEnabled(true);
	            }
	    }
    }

	// if all the selected rules/ruleGroups reference a common ruleset name
	// then check that item and disable it, do the reverse for all others.
    private void adjustMenuRulesetSettings() {

        String rulesetName = ruleSelection == null ? null : ruleSetNameFrom(RuleUtil.commonRuleset(ruleSelection));
        Iterator<Map.Entry<String, MenuItem>> iter = rulesetMenusByName.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, MenuItem> entry = iter.next();
            MenuItem item = entry.getValue();
            if (rulesetName == null) {	// allow all entries if none or conflicting
            	 item.setSelection(false);
                 item.setEnabled(true);
                 continue;
            	}
            if (StringUtil.areSemanticEquals(entry.getKey(), rulesetName)) {
                item.setSelection(true);
                item.setEnabled(false);
            } else {
                item.setSelection(false);
                item.setEnabled(true);
                }
        }
    }

	private void adjustMenuUseDefaultsOption() {
		useDefaultsItem.setEnabled( ! ruleSelection.haveDefaultValues() );
	}

	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildAddRuleButton(final Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_ADD, StringKeys.MSGKEY_PREF_RULESET_BUTTON_ADDRULE);
		
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				createRule(parent.getShell());
				}
			});

		return button;
	}

	private void createRule(Shell shell) {
//		RuleDialog dialog = new RuleDialog(parent.getShell());
//		int result = dialog.open();

		try {
			CreateRuleWizard wiz = new CreateRuleWizard();
			WizardDialog dialog = new WizardDialog(shell, wiz);
			int result = dialog.open();

			if (result == Window.OK) {
				Rule addedRule = wiz.rule();
				ruleSet.addRule(addedRule);
				setModified();
				try {
					refresh();
					treeViewer.reveal(addedRule);
				} catch (Throwable t) {
					plugin.logError("Exception when refreshing the rule table", t);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Build the remove rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildRemoveRuleButton(Composite parent) {

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_DELETE, StringKeys.MSGKEY_PREF_RULESET_BUTTON_REMOVERULE);

		button.addSelectionListener(new SelectionAdapter() {
           public void widgetSelected(SelectionEvent event) {
				removeSelectedItems();
				}
			});
		return button;
	}

	protected void removeSelectedItems() {

		if (ruleSelection == null) return;

	    int removeCount = ruleSelection.removeAllFrom(ruleSet);
	    if (removeCount == 0) return;

        setModified();

        try {
            refresh();
        } catch (Throwable t) {
            treeViewer.setSelection(null);
        }
	}
	
	/**
	 * Build the export rule set button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildExportRuleSetButton(final Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_EXPORT, StringKeys.MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET);
		
		button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);
				String fileName = dialog.open();
				if (fileName != null) {
					try {
						exportTo(fileName, parent.getShell());
					} catch (Exception e) {
						plugin.showError(getMessage(StringKeys.MSGKEY_ERROR_EXPORTING_RULESET), e);
					}
				}
			}

		});

		return button;
	}

	private void exportTo(String fileName, Shell shell) throws FileNotFoundException, WriterException, IOException {
		
		File file = new File(fileName);
		boolean flContinue = true;
		
		if (file.exists()) {
			flContinue = MessageDialog.openConfirm(shell,
					getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
					getMessage(StringKeys.MSGKEY_CONFIRM_RULESET_EXISTS));
		}

		InputDialog input = null;
		if (flContinue) {
			input = new InputDialog(shell,
					getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE),
					getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_RULESET_DESCRIPTION),
					ruleSet.getDescription() == null ? "" : ruleSet.getDescription().trim(), null);
			flContinue = input.open() == Window.OK;
		}

		if (flContinue) {
			ruleSet.setName(FileUtil.getFileNameWithoutExtension(file.getName()));
			ruleSet.setDescription(input.getValue());
			OutputStream out = new FileOutputStream(fileName);
			IRuleSetWriter writer = plugin.getRuleSetWriter();
			writer.write(out, ruleSet);
			out.close();
			MessageDialog.openInformation(shell, getMessage(StringKeys.MSGKEY_INFORMATION_TITLE),
					getMessage(StringKeys.MSGKEY_INFORMATION_RULESET_EXPORTED));
		}
	}
	
	/**
	 * Build the import ruleset button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildImportRuleSetButton(final Composite parent) {

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_IMPORT, StringKeys.MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET);
		
		button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
				RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(parent.getShell());
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					doImport(dialog.getSelectedRuleSet(), dialog.isImportByReference());
				}
			}
		});

		return button;
	}
	
	private void doImport(RuleSet selectedRuleSet, boolean doByReference) {
		
		try {			
			if (doByReference) {
				ruleSet.addRuleSetByReference(selectedRuleSet, false);
			} else {
				// Set pmd-eclipse as new RuleSet name and add the Rule
				Iterator<Rule> iter = selectedRuleSet.getRules().iterator();
				while (iter.hasNext()) {
					Rule rule = iter.next();
					rule.setRuleSetName("pmd-eclipse");
					ruleSet.addRule(rule);
				}
			}
			setModified();
			try {
				refresh();
			} catch (Throwable t) {
				plugin.logError("Exception when refreshing the rule table", t);
			}
		} catch (RuntimeException e) {
			plugin.showError(getMessage(StringKeys.MSGKEY_ERROR_IMPORTING_RULESET), e);
		}
	}
	
	public Composite buildGroupCombo(Composite parent, String comboLabelKey, final Object[][] groupingChoices) {

	     Composite panel = new Composite(parent, 0);
	     GridLayout layout = new GridLayout(6, false);
	     panel.setLayout(layout);

	     buildCheckButtons(panel);
	     
	     Label label = new Label(panel, 0);
	     GridData data = new GridData();
	     data.horizontalAlignment = SWT.LEFT;
	     data.verticalAlignment = SWT.CENTER;
	     label.setLayoutData(data);
	     label.setText(SWTUtil.stringFor(comboLabelKey));

        final Combo combo = new Combo(panel, SWT.READ_ONLY);
        combo.setItems(SWTUtil.i18lLabelsIn(groupingChoices, 1));
        combo.select(groupingChoices.length - 1);  // picks last one by default TODO make it a persistent preference

        combo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int selectionIdx = combo.getSelectionIndex();
                Object[] choice = groupingChoices[selectionIdx];
                groupingColumn = (RuleColumnDescriptor)choice[0];
                redrawTable();
            }
          });

         buildActiveCountLabel(panel);

	     return panel;
	 }

	/**
	 * Build the Rule Designer button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildRuleDesignerButton(Composite parent) {

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_EDITOR, StringKeys.MSGKEY_PREF_RULESET_BUTTON_RULEDESIGNER);

		button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
				// TODO Is this cool from Eclipse?  Is there a nicer way to spawn a J2SE Application?
				new Thread(new Runnable() {
					public void run() {
						Designer.main(new String[] { "-noexitonclose" });
					}
				}).start();
			}
		});

		return button;
	}

	/**
	 * Create buttons for rule table management
	 * @param parent Composite
	 * @return Composite
	 */
	public Composite buildRuleTableButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 3;
		composite.setLayout(gridLayout);

		addRuleButton = buildAddRuleButton(composite);
		removeRuleButton = buildRemoveRuleButton(composite);
		Button importRuleSetButton = buildImportRuleSetButton(composite);
		Button exportRuleSetButton = buildExportRuleSetButton(composite);
		Button ruleDesignerButton = buildRuleDesignerButton(composite);

		GridData data = new GridData();
		addRuleButton.setLayoutData(data);

		data = new GridData();
		importRuleSetButton.setLayoutData(data);

		data = new GridData();
		exportRuleSetButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.END;
		ruleDesignerButton.setLayoutData(data);

		return composite;
	}
	
	/**
	 * Build rule table viewer
	 * @param parent Composite
	 * @return Tree
	 */
	public Tree buildRuleTreeViewer(Composite parent) {

		buildTreeViewer(parent);

		Tree ruleTree = treeViewer.getTree();
		
		ruleListMenu = createMenuFor(ruleTree);
		ruleTree.setMenu(ruleListMenu);
		ruleTree.addListener(SWT.MenuDetect, new Listener () {
	        public void handleEvent (Event event) {
	            popupRuleSelectionMenu(event);
	        }
	    });

		treeViewer.setCheckStateProvider(createCheckStateProvider());

		return ruleTree;
	}
	
    public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) {
        // TODO enhance to recognize default values
   // 	RuleUtil.modifiedPropertiesIn(rule);
    	
        treeViewer.update(rule, null);
        setModified();
    }

	public void changed(RuleSelection selection, PropertyDescriptor<?> desc, Object newValue) {
		// TODO enhance to recognize default values

		for (Rule rule : selection.allRules()) {
			if (newValue != null) {		// non-reliable update behaviour, alternate trigger option - weird
				treeViewer.getTree().redraw();
//				System.out.println("doing redraw");
			} else {
				treeViewer.update(rule, null);
//				System.out.println("viewer update");
			}
		}
		setModified();
	}


	private void checkSelections() {

//		List<Rule> activeRules = new ArrayList<Rule>();
//
//		for (Rule rule : ruleSet.getRules()) {
//			if (preferences.isActive(rule.getName())) {
//				activeRules.add(rule);
//			}
//		}
//
//		ruleTreeViewer.setCheckedElements(activeRules.toArray());
	}

	protected String[] columnLabels() {
	    String[] names = new String[availableColumns.length];
	    for (int i=0; i<availableColumns.length; i++) {
	        names[i] = availableColumns[i].label();
	    }
	    return names;
	}

	private ICheckStateProvider createCheckStateProvider() {

		return new ICheckStateProvider() {

			public boolean isChecked(Object item) {
				if (item instanceof Rule) {
					return isActive(((Rule)item).getName());
				} else {
					if (item instanceof RuleGroup) {
						int[] fraction = selectionRatioIn(((RuleGroup)item).rules());
						return (fraction[0] > 0) && (fraction[0] == fraction[1]);
					}
				}
				return false;	// should never get here
			}

			public boolean isGrayed(Object item) {

				if (item instanceof Rule) return false;
				if (item instanceof RuleGroup) {
					int[] fraction = selectionRatioIn(((RuleGroup)item).rules());
					return (fraction[0] > 0) && (fraction[0] != fraction[1]);
				}
				return false;
			}

		};
	}

	private Menu createMenuFor(Control control) {

	    Menu menu = new Menu(control);

	    MenuItem priorityMenu = new MenuItem (menu, SWT.CASCADE);
	    priorityMenu.setText(SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY));
	    Menu subMenu = new Menu(menu);
	    priorityMenu.setMenu (subMenu);
	    priorityMenusByPriority = new HashMap<RulePriority, MenuItem>(RulePriority.values().length);

	    for (RulePriority priority : RulePriority.values()) {
    	    MenuItem priorityItem = new MenuItem (subMenu, SWT.RADIO);
    	    priorityMenusByPriority.put(priority, priorityItem);
    	    priorityItem.setText(priority.getName());  // TODO need to internationalize?
    	 //   priorityItem.setImage(imageFor(priority));  not visible with radiobuttons
    	    final RulePriority pri = priority;
    	    priorityItem.addSelectionListener( new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setPriority(pri);
                    }
                }
    	    );
	    }

//	    MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
//	    removeItem.setText("Remove");
//	    removeItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                removeSelectedRules();
//            }
//        });

        useDefaultsItem = new MenuItem(menu, SWT.PUSH);
        useDefaultsItem.setText("Use defaults");
        useDefaultsItem.setEnabled(false);
        useDefaultsItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ruleSelection.useDefaultValues();
            }
        });

        return menu;
	}

	/**
	 * Method groupBy.
	 * @param chosenColumn RuleColumnDescriptor
	 */
	public void groupBy(RuleColumnDescriptor chosenColumn) {

		List<RuleColumnDescriptor> visibleColumns = new ArrayList<RuleColumnDescriptor>(availableColumns.length);
		for (RuleColumnDescriptor desc : availableColumns) {
		    if (desc == chosenColumn) continue;   // redundant, don't include it
		    if (isHidden(desc.label())) continue;
		    visibleColumns.add(desc);
		}

		setupTreeColumns(
		    visibleColumns.toArray(new RuleColumnDescriptor[visibleColumns.size()]),
		    chosenColumn == null ? null : chosenColumn.accessor()
		    );
	}

	private boolean hasPriorityGrouping() {
	    return
	        groupingColumn == TextColumnDescriptor.priorityName ||
	        groupingColumn == TextColumnDescriptor.priority;
	}

	/**
	 * Populate the rule table
	 */
	public void populateRuleTable() {
		treeViewer.setInput(ruleSet);
		checkSelections();

		restoreSavedRuleSelections();
		updateCheckControls();
	}

	private void popupRuleSelectionMenu(Event event) {

	    // have to do it here or else the ruleset var is null in the menu setup - timing issue
	    if (rulesetMenusByName == null) {
	        addRulesetMenuOptions(ruleListMenu);
	        new MenuItem(ruleListMenu, SWT.SEPARATOR);
            addColumnSelectionOptions(ruleListMenu);
	    }

        adjustMenuPrioritySettings();
        adjustMenuRulesetSettings();
        adjustMenuUseDefaultsOption();
	    ruleListMenu.setLocation(event.x, event.y);
        ruleListMenu.setVisible(true);
	}

	protected void redrawTable(String sortColumnLabel, int sortDir) {
		groupBy(groupingColumn);

		super.redrawTable(sortColumnLabel, sortDir);
	}

	private void restoreSavedRuleSelections() {

		Set<String> names = PreferenceUIStore.instance.selectedRuleNames();
		List<Rule> rules = new ArrayList<Rule>();
		for (String name : names) rules.add(ruleSet.getRuleByName(name));

		IStructuredSelection selection = new StructuredSelection(rules);
		treeViewer.setSelection(selection);
	}

	public RuleSet ruleSet() { return ruleSet; }

	private String[] rulesetNames() {

	    Set<String> names = new HashSet<String>();
	    for (Rule rule : ruleSet.getRules()) {
	        names.add(ruleSetNameFrom(rule));  // if we strip out the 'Rules' portions then we don't get matches...need to rename rulesets
	    }
	    return names.toArray(new String[names.size()]);
	}

	protected void saveItemSelections() {

		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

		List<String> ruleNames = new ArrayList<String>();
		for (Object item : selection.toList()) {
			if (item instanceof Rule)
				ruleNames.add(((Rule)item).getName());
		}

		PreferenceUIStore.instance.selectedRuleNames(ruleNames);
	}

	/**
	 * @param item Object[]
	 */
	protected void selectedItems(Object[] items) {

	    ruleSelection = new RuleSelection(items);
	    if (ruleSelectionListener != null) {
	    	ruleSelectionListener.selection(ruleSelection);
	    }

	    if (removeRuleButton != null) removeRuleButton.setEnabled(items.length > 0);
	}

	private int[] selectionRatioIn(Rule[] rules) {

		int selectedCount = 0;
		for (Rule rule : rules) {
			if (isActive(rule.getName())) selectedCount++;
		}
		return new int[] { selectedCount , rules.length };
	}

	protected void setAllItemsActive() {
		for (Rule rule : ruleSet.getRules()) {
			isActive(rule.getName(), true);
		}

		treeViewer().setCheckedElements(ruleSet.getRules().toArray());

		updateCheckControls();
		setModified();
	}

	private void setPriority(RulePriority priority) {

		if (ruleSelection == null) return;

	    ruleSelection.setPriority(priority);

	    if (hasPriorityGrouping()) {
	        redrawTable();
	    } else {
	        treeViewer.update(ruleSelection.allRules().toArray(), null);
	    }

	    setModified();
	}

	private void setRuleset(String rulesetName) {
	    // TODO - awaiting support in PMD itself
	}
	/**
	 *
	 * @param columnDescs RuleColumnDescriptor[]
	 * @param groupingField RuleFieldAccessor
	 */
	private void setupTreeColumns(RuleColumnDescriptor[] columnDescs, RuleFieldAccessor groupingField) {

		Tree ruleTree = cleanupRuleTree();

		for (int i=0; i<columnDescs.length; i++) columnDescs[i].newTreeColumnFor(ruleTree, i, this, paintListeners());

		treeViewer.setLabelProvider(new RuleLabelProvider(columnDescs));
		treeViewer.setContentProvider(
				new RuleSetTreeItemProvider(groupingField, "??", Util.comparatorFrom(columnSorter, sortDescending))
				);

		treeViewer.setInput(ruleSet);
        checkSelections();

		TreeColumn[] columns = ruleTree.getColumns();
		for (TreeColumn column : columns) column.pack();
	}

	protected void sortByCheckedItems() {
		sortBy(checkedColumnAccessor, treeViewer.getTree().getColumn(0));
	}

	public void sortBy(RuleFieldAccessor accessor, Object context) {

		TreeColumn column = (TreeColumn)context;

		if (columnSorter == accessor) {
			sortDescending = !sortDescending;
		} else {
			columnSorter = accessor;
		}

		redrawTable(column.getToolTipText(), sortDescending ? SWT.DOWN : SWT.UP);
	}

	public void useRuleSet(RuleSet theSet) {
		ruleSet = theSet;
	}
	
	protected void updateCheckControls() {

		Rule[] rules = new Rule[ruleSet.size()];
		rules = ruleSet.getRules().toArray(rules);
		int[] selectionRatio = selectionRatioIn(rules);

		updateButtonsFor(selectionRatio);
		
		String label = SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_ACTIVE_RULE_COUNT);
		activeCountText(label + " " + activeItemCount() + " / " + ruleSet.size());
	}

	protected void updateTooltipFor(TreeItem item, int columnIndex) {

		RuleLabelProvider provider = (RuleLabelProvider)treeViewer.getLabelProvider();
		String txt = provider.getDetailText(item.getData(), columnIndex);
		treeViewer.getTree().setToolTipText(txt);
	}
}
