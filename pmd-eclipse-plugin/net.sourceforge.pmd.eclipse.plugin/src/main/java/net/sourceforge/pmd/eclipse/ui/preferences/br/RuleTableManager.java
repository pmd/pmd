package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;
import net.sourceforge.pmd.eclipse.ui.ColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleDupeChecker;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleSetSelectionDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.CreateRuleWizard;
import net.sourceforge.pmd.eclipse.util.IOUtil;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
public class RuleTableManager extends AbstractTreeTableManager<Rule> implements ValueChangeListener, RuleDupeChecker {

	private RuleSet						ruleSet;

	private RuleColumnDescriptor 		groupingColumn;

	protected String					groupColumnLabel;

	private RuleFieldAccessor 			checkedColumnAccessor;
//	private Map<RulePriority, MenuItem> priorityMenusByPriority;
//	private Map<String, MenuItem>       rulesetMenusByName;
	
	private RuleSelection               ruleSelection; // may hold rules and/or group nodes

//	private Menu                 		ruleListMenu;
	private	MenuItem					useDefaultsItem; 
	private Button			     		addRuleButton;
	private Button			     		removeRuleButton;
	private Button						exportRuleSetButton;
	private RuleSelectionListener		ruleSelectionListener;
	private ValueResetHandler			resetHandler;
    
	public RuleTableManager(String theWidgetId, RuleColumnDescriptor[] theColumns, IPreferences thePreferences, ValueResetHandler aResetHandler) {
		super(theWidgetId, thePreferences, theColumns);
		
		columnSorter = RuleFieldAccessor.name;
		checkedColumnAccessor = createCheckedItemAccessor();
		resetHandler = aResetHandler;
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

	protected String nameFor(Object treeItemData) {
		return ((Rule)treeItemData).getName();
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

//	private void addRulesetMenuOptions(Menu menu) {
//
//        MenuItem rulesetMenu = new MenuItem(menu, SWT.CASCADE);
//        rulesetMenu.setText("Ruleset");
//        Menu rulesetSubMenu = new Menu(menu);
//        rulesetMenu.setMenu(rulesetSubMenu);
//        rulesetMenusByName = new HashMap<String, MenuItem>();
//
//        MenuItem demoItem = new MenuItem(rulesetSubMenu, SWT.PUSH);
//        demoItem.setText("---demo only---");    // NO API to re-parent rules to other rulesets (yet)
//
//        for (String rulesetName : rulesetNames()) {
//            MenuItem rulesetItem = new MenuItem(rulesetSubMenu, SWT.RADIO);
//            rulesetMenusByName.put(rulesetName, rulesetItem);
//            rulesetItem.setText(rulesetName);
//            final String rulesetStr = rulesetName;
//            rulesetItem.addSelectionListener( new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    setRuleset(rulesetStr);
//                    }
//                }
//            );
//         }
//	}

//    private void adjustMenuPrioritySettings() {
//
//        RulePriority priority = ruleSelection == null ? null : RuleUtil.commonPriority(ruleSelection);
//	    Iterator<Map.Entry<RulePriority, MenuItem>> iter = priorityMenusByPriority.entrySet().iterator();
//
//	    while (iter.hasNext()) {
//	        Map.Entry<RulePriority, MenuItem> entry = iter.next();
//	        MenuItem item = entry.getValue();
//	        if (entry.getKey() == priority) {
//	            item.setSelection(true);
//	            item.setEnabled(false);
//	        } else {
//	            item.setSelection(false);
//	            item.setEnabled(true);
//	            }
//	    }
//    }

	// if all the selected rules/ruleGroups reference a common ruleset name
	// then check that item and disable it, do the reverse for all others.
//    private void adjustMenuRulesetSettings() {
//
//        String rulesetName = ruleSelection == null ? null : ruleSetNameFrom(RuleUtil.commonRuleset(ruleSelection));
//        Iterator<Map.Entry<String, MenuItem>> iter = rulesetMenusByName.entrySet().iterator();
//
//        while (iter.hasNext()) {
//            Map.Entry<String, MenuItem> entry = iter.next();
//            MenuItem item = entry.getValue();
//            if (rulesetName == null) {	// allow all entries if none or conflicting
//            	 item.setSelection(false);
//                 item.setEnabled(true);
//                 continue;
//            	}
//            if (StringUtil.areSemanticEquals(entry.getKey(), rulesetName)) {
//                item.setSelection(true);
//                item.setEnabled(false);
//            } else {
//                item.setSelection(false);
//                item.setEnabled(true);
//                }
//        }
//    }

	protected void addTableSelectionOptions(Menu menu) {
		
		useDefaultsItem = new MenuItem(menu, SWT.PUSH);
	    useDefaultsItem.setText("Use defaults");
//	    useDefaultsItem.setEnabled(false);
	    useDefaultsItem.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent event) {
	        	resetHandler.resetValuesIn(ruleSelection);
	            }
	        });
	}
    
	protected void adjustTableMenuOptions() {
		boolean hasDefaults = ruleSelection.haveDefaultValues();
		useDefaultsItem.setEnabled( ! hasDefaults );
	}

	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildAddRuleButton(final Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_ADD, StringKeys.PREF_RULESET_BUTTON_ADDRULE);
		
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
			wiz.dialog(dialog);
			
			int result = dialog.open();

			if (result != Window.OK) return;
			
			Rule addedRule = wiz.rule();
			ruleSet.addRule(addedRule);
			
			added(addedRule);
			
			setModified();
			try {
				refresh();
				treeViewer.reveal(addedRule);
			} catch (Throwable t) {
				plugin.logError("Exception when refreshing the rule table", t);
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

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_DELETE, StringKeys.PREF_RULESET_BUTTON_REMOVERULE);

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

	    removed(ruleSelection.allRules());
	    
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
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_EXPORT, StringKeys.PREF_RULESET_BUTTON_EXPORTRULESET);
		
		button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
				exportSelectedRules();
			}
		});

		return button;
	}

	private void exportSelectedRules() {
		
		Shell shell = treeViewer.getTree().getShell();
		
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Export " + ruleSelection.allRules().size() + " rules");
		
		String fileName = dialog.open();
		if (StringUtil.isNotEmpty(fileName)) {
			try {
				exportTo(fileName, shell);
			} catch (Exception e) {
				plugin.showError(getMessage(StringKeys.ERROR_EXPORTING_RULESET), e);
			}
		}
	}
	
	private RuleSet ruleSelectionAsRuleSet() {

		RuleSet rs = new RuleSet();
		rs.setName( ruleSet.getName() );
		rs.setDescription( ruleSet.getDescription() );
		rs.setFileName( ruleSet.getFileName());
		rs.addExcludePatterns( ruleSet.getExcludePatterns() );
		rs.addIncludePatterns( ruleSet.getIncludePatterns() );

		for (Rule rule : ruleSelection.allRules()) {
			rs.addRule(rule);
		}		
		
		return rs;
	}
	
	private void exportTo(String fileName, Shell shell) throws FileNotFoundException, WriterException, IOException {
		
		File file = new File(fileName);
		boolean flContinue = true;
		
		if (file.exists()) {
			flContinue = MessageDialog.openConfirm(shell,
					getMessage(StringKeys.CONFIRM_TITLE),
					getMessage(StringKeys.CONFIRM_RULESET_EXISTS));
		}

		InputDialog input = null;
		
		RuleSet ruleSet = null;
		
		if (flContinue) {
			ruleSet = ruleSelectionAsRuleSet();
			
			input = new InputDialog(shell,
					getMessage(StringKeys.PREF_RULESET_DIALOG_TITLE),
					getMessage(StringKeys.PREF_RULESET_DIALOG_RULESET_DESCRIPTION),
					ruleSet.getDescription() == null ? "" : ruleSet.getDescription().trim(), null);
			flContinue = input.open() == Window.OK;
		}

		if (flContinue) {
			OutputStream out = null;
			try {
				ruleSet.setName(FileUtil.getFileNameWithoutExtension(file.getName()));
				ruleSet.setDescription(input.getValue());
				out = new FileOutputStream(fileName);
				IRuleSetWriter writer = plugin.getRuleSetWriter();
				writer.write(out, ruleSet);
			} finally { 
				IOUtil.closeQuietly(out);
				}
			MessageDialog.openInformation(shell, getMessage(StringKeys.INFORMATION_TITLE),
					getMessage(StringKeys.INFORMATION_RULESET_EXPORTED));
		}
	}
	
	private RuleColumnDescriptor newDupeIndicatorColumn() {
		return new SimpleColumnDescriptor("a", "", SWT.CENTER, 35, null, false, null) {

			public String stringValueFor(Rule rule) { return isDuplicate(rule) ? "X" : ""; }
			public Image imageFor(Rule rule) {
				return null;
			}			
		};
	}

	private RuleColumnDescriptor[] ruleImportColumns() {
		
		return new RuleColumnDescriptor[] {
				newDupeIndicatorColumn(),
				new SimpleColumnDescriptor("a", RuleTableColumns.name.label(), SWT.LEFT, 210, RuleTableColumns.name.accessor(), true, null), 
				new SimpleColumnDescriptor("a", RuleTableColumns.language.label(), SWT.LEFT, 40, RuleTableColumns.language.accessor(), true, null), 
				};
	}
	
	/**
	 * Build the import ruleset button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildImportRuleSetButton(final Composite parent) {

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_IMPORT, StringKeys.PREF_RULESET_BUTTON_IMPORTRULESET);
		
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	RuleColumnDescriptor[] rcd = ruleImportColumns();
				RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(parent.getShell(), "Import rules", rcd, RuleTableManager.this);
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					doImport(dialog.checkedRules(), dialog.isImportByReference());
				}
			}
		});

		return button;
	}
	
	public static boolean areSemanticEquals(Rule a, Rule b) {
		return
			a.getName().equals(b.getName()) &&
			a.getLanguage().equals(b.getLanguage());
	}
	
	/**
	 * Return whether adding the rule arg to the ruleset would
	 * constitute a duplicate or now.
	 * 
	 * @param rule
	 * @return
	 */
	public boolean isDuplicate(Rule otherRule) {
		
		for (Rule rule : ruleSet.getRules()) {
			if (areSemanticEquals(rule, otherRule)) return true;
		}
		return false;
	}
	
	private void add(RuleSet incomingRuleSet) {
		
		Iterator<Rule> iter = incomingRuleSet.getRules().iterator();
		
		while (iter.hasNext()) {
			Rule rule = iter.next();
			ruleSet.addRule(rule);
			rule.setRuleSetName(ruleSet.getName());
			added(rule);			
			}
		
		ruleSet.addIncludePatterns( incomingRuleSet.getIncludePatterns() );
		ruleSet.addExcludePatterns( incomingRuleSet.getExcludePatterns() );
	}
	
	private void doImport(RuleSet selectedRuleSet, boolean doByReference) {
		
		try {			
			if (doByReference) {
				RuleSet filteredRS = new RuleSet();
				filteredRS.setFileName(selectedRuleSet.getFileName());
				for (Rule rule : selectedRuleSet.getRules()) {
					filteredRS.addRule(rule);
				}
				ruleSet.addRuleSetByReference(filteredRS, false);
				ruleSet.addIncludePatterns(selectedRuleSet.getIncludePatterns());
				ruleSet.addExcludePatterns(selectedRuleSet.getExcludePatterns());
			} else {
				add(selectedRuleSet);
			}
			setModified();
			try {
				refresh();
			} catch (Throwable t) {
				plugin.logError("Exception when refreshing the rule table", t);
			}
		} catch (RuntimeException e) {
			plugin.showError(getMessage(StringKeys.ERROR_IMPORTING_RULESET), e);
		}
		
		updateCheckControls();
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

         buildActiveCountWidgets(panel);

	     return panel;
	 }

	/**
	 * Build the Rule Designer button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildRuleDesignerButton(Composite parent) {

		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_EDITOR, StringKeys.PREF_RULESET_BUTTON_RULEDESIGNER);

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
		exportRuleSetButton = buildExportRuleSetButton(composite);
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
	
	protected boolean hasIssue(TreeItem item) {
		
		Object data = item.getData(); 
        return data instanceof Rule && ((Rule)data).dysfunctionReason() != null;
	}
	
	protected void addIssueStyler(final Tree tree) {
		
		final Display display = tree.getDisplay();
		final Color issueColor = display.getSystemColor(SWT.COLOR_YELLOW);
		
		tree.addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(Event event) {
                
//                event.detail &= ~SWT.HOT;
//                
//                    GC gc = event.gc;
//
//                    gc.setAdvanced(true);
//                    if (gc.getAdvanced()) gc.setAlpha(127);
//                    Rectangle rect = event.getBounds();
//                    Color foreground = gc.getForeground();
//                    Color background = gc.getBackground();
//                    
//                    TreeItem item = (TreeItem)event.item;                   
//                    
//                    if (hasIssue(item)) {
//	                    gc.setBackground(issueColor);
//	                    gc.setForeground(issueColor);
//                    } else {
//                   // 	gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
//                    }
//	            //    gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
//	                gc.fillRectangle(event.x, rect.y, rect.width, rect.height);
//	                gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
//	                
//                    gc.setForeground(foreground);  // restore colors for subsequent drawing
//                    gc.setBackground(background);
//                    event.detail &= ~SWT.SELECTED;                  
                }
       
        });
		
	}
	
	/**
	 * Build rule table viewer
	 * @param parent Composite
	 * @return Tree
	 */
	public Tree buildRuleTreeViewer(Composite parent) {

		buildTreeViewer(parent);

		final Tree ruleTree = treeViewer.getTree();
		
//		ruleListMenu = createMenuFor(ruleTree);
//		ruleTree.setMenu(ruleListMenu);
//		ruleTree.addListener(SWT.MenuDetect, new Listener () {
//	        public void handleEvent (Event event) {
//	            popupRuleSelectionMenu(event);
//	        }
//	    });
		
		addIssueStyler(treeViewer.getTree());
		
		treeViewer.setCheckStateProvider(createCheckStateProvider());

		return ruleTree;
	}
	
    public void changed(PropertySource source, PropertyDescriptor<?> desc, Object newValue) {
        // TODO enhance to recognize default values
   // 	RuleUtil.modifiedPropertiesIn(rule);
    	
        treeViewer.update(source, null);
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

//	protected String[] columnLabels() {
//	    String[] names = new String[availableColumns.length];
//	    for (int i=0; i<availableColumns.length; i++) {
//	        names[i] = availableColumns[i].label();
//	    }
//	    return names;
//	}
	
	private ICheckStateProvider createCheckStateProvider() {

		return new ICheckStateProvider() {

			public boolean isChecked(Object item) {
				if (item instanceof Rule) {
					return isActive(((Rule)item).getName());
				} else {
					if (item instanceof RuleGroup) {
						SelectionStats stats = selectionRatioIn(((RuleGroup)item).rules());
						return (stats.selectedCount > 0) && stats.allSelected();
					}
				}
				return false;	// should never get here
			}

			public boolean isGrayed(Object item) {

				if (item instanceof Rule) return false;
				if (item instanceof RuleGroup) {
					SelectionStats stats = selectionRatioIn(((RuleGroup)item).rules());
					return (stats.selectedCount > 0) && (!stats.allSelected());
				}
				return false;
			}

		};
	}

//	private Menu createMenuFor(Control control) {
//
//	    Menu menu = new Menu(control);
//
//	    MenuItem priorityMenu = new MenuItem (menu, SWT.CASCADE);
//	    priorityMenu.setText(SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY));
//	    Menu subMenu = new Menu(menu);
//	    priorityMenu.setMenu (subMenu);
//	    priorityMenusByPriority = new HashMap<RulePriority, MenuItem>(RulePriority.values().length);
//
//	    for (RulePriority priority : RulePriority.values()) {
//    	    MenuItem priorityItem = new MenuItem (subMenu, SWT.RADIO);
//    	    priorityMenusByPriority.put(priority, priorityItem);
//    	    priorityItem.setText(priority.getName());  // TODO need to internationalize?
//    	 //   priorityItem.setImage(imageFor(priority));  not visible with radiobuttons
//    	    final RulePriority pri = priority;
//    	    priorityItem.addSelectionListener( new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    setPriority(pri);
//                    }
//                }
//    	    );
//	    }
//
//	    MenuItem hideItem = new MenuItem(menu, SWT.PUSH);
//	    hideItem.setText("Hide");
//	    hideItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//            	toggleColumnVisiblity("??");	// TODO
//            }
//        });
//	    hideItem.setEnabled(false);
//
//        useDefaultsItem = new MenuItem(menu, SWT.PUSH);
//        useDefaultsItem.setText("Use defaults");
//        useDefaultsItem.setEnabled(false);
//        useDefaultsItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                ruleSelection.useDefaultValues();
//            }
//        });
//
//        return menu;
//	}

	/**
	 * Method groupBy.
	 * @param chosenColumn RuleColumnDescriptor
	 */
	public void groupBy(RuleColumnDescriptor chosenColumn) {

		groupColumnLabel = chosenColumn == null ? null : chosenColumn.label();
		
		List<ColumnDescriptor> visibleColumns = new ArrayList<ColumnDescriptor>(availableColumns.length);
		for (ColumnDescriptor desc : availableColumns) {
		    if (desc == chosenColumn) continue;   // redundant, don't include it
		    if (isHidden(desc)) continue;
		    visibleColumns.add(desc);
		}

		setupTreeColumns(
		    visibleColumns.toArray(new RuleColumnDescriptor[visibleColumns.size()]),
		    chosenColumn == null ? null : chosenColumn.accessor()
		    );
		
		selectedItems(new Object[0]);	// selections are killed by grouping
	}

//	private boolean hasPriorityGrouping() {
//	    return
//	        groupingColumn == TextColumnDescriptor.priorityName ||
//	        groupingColumn == TextColumnDescriptor.priority;
//	}

	private String labelFor(TreeColumn tc) {

		return groupColumnLabel == null ?
				tc.getText() :
				groupColumnLabel + " / " + tc.getText();
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

//	private void popupRuleSelectionMenu(Event event) {
//
//	    // have to do it here or else the ruleset var is null in the menu setup - timing issue
////	    if (rulesetMenusByName == null) {
//	//        addRulesetMenuOptions(ruleListMenu);
//	//        new MenuItem(ruleListMenu, SWT.SEPARATOR);
// //           addColumnSelectionOptions(ruleListMenu);
////	    }
//
// //       adjustMenuPrioritySettings();
////        adjustMenuRulesetSettings();
////        adjustMenuUseDefaultsOption();
////	    ruleListMenu.setLocation(event.x, event.y);
////        ruleListMenu.setVisible(true);
//	}

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

//	private String[] rulesetNames() {
//
//	    Set<String> names = new HashSet<String>();
//	    for (Rule rule : ruleSet.getRules()) {
//	        names.add(ruleSetNameFrom(rule));  // if we strip out the 'Rules' portions then we don't get matches...need to rename rulesets
//	    }
//	    return names.toArray(new String[names.size()]);
//	}

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

	    boolean hasSelections = items.length > 0;
	    
	    if (removeRuleButton != null) removeRuleButton.setEnabled(hasSelections);
	    if (exportRuleSetButton != null) exportRuleSetButton.setEnabled(hasSelections);
	}

	private class SelectionStats {
		public int selectedCount;
		public int totalCount;
		public int dysfunctionCount;
		
		public SelectionStats(int theSelectedCount, int theTotalCount, int theDysfunctionCount) {
			selectedCount = theSelectedCount;
			totalCount = theTotalCount;
			dysfunctionCount = theDysfunctionCount;
		}
		
		public boolean allSelected() { return selectedCount == totalCount; }
	}
	
	private SelectionStats selectionRatioIn(Rule[] rules) {

		int selectedCount = 0;
		int dysfunctionCount = 0;
		for (Rule rule : rules) {
			if (isActive(rule.getName())) {
				selectedCount++;
				if (StringUtil.isNotEmpty(rule.dysfunctionReason())) dysfunctionCount++;
			}
		}
		return new SelectionStats(selectedCount, rules.length, dysfunctionCount) ;
	}

	protected void setAllItemsActive() {
		for (Rule rule : ruleSet.getRules()) {
			isActive(rule.getName(), true);
		}

		treeViewer().setCheckedElements(ruleSet.getRules().toArray());

		updateCheckControls();
		setModified();
	}

//	private void setPriority(RulePriority priority) {
//
//		if (ruleSelection == null) return;
//
//	    ruleSelection.setPriority(priority);
//
//	    if (hasPriorityGrouping()) {
//	        redrawTable();
//	    } else {
//	        treeViewer.update(ruleSelection.allRules().toArray(), null);
//	    }
//
//	    setModified();
//	}

//	private void setRuleset(String rulesetName) {
//	    // TODO - awaiting support in PMD itself
//	}
	
	/**
	 *
	 * @param columnDescs RuleColumnDescriptor[]
	 * @param groupingField RuleFieldAccessor
	 */
	private void setupTreeColumns(RuleColumnDescriptor[] columnDescs, RuleFieldAccessor groupingField) {

		Tree ruleTree = cleanupRuleTree();

		createCheckBoxColumn(ruleTree);
		
		for (int i=0; i<columnDescs.length; i++) {
			TreeColumn tc =columnDescs[i].newTreeColumnFor(ruleTree, i+1, this, paintListeners());
			if (i==0 && groupingColumn != null) {
				tc.setText( labelFor(tc) );
			}
		}

		treeViewer.setLabelProvider(new RuleLabelProvider(columnDescs));
		treeViewer.setContentProvider(
			new RuleSetTreeItemProvider(groupingField, "??", Util.comparatorFrom(columnSorter(), sortDescending))
			);

		treeViewer.setInput(ruleSet);
        checkSelections();

		TreeColumn[] columns = ruleTree.getColumns();
		for (TreeColumn column : columns) column.pack();
	}

	private RuleFieldAccessor columnSorter() {
		return (RuleFieldAccessor)columnSorter;
	}
	
	protected void sortByCheckedItems() {
		sortBy(checkedColumnAccessor, treeViewer.getTree().getColumn(0));
	}

	public void useRuleSet(RuleSet theSet) {
		ruleSet = theSet;
	}
	
	private boolean activeRulesHaveIssues(Rule[] rules) {
		for (Rule rule : rules) {
			if (isActive(rule.getName()) && StringUtil.isNotEmpty(rule.dysfunctionReason())) 
				return true;
		}
		return false;
	}
	
	protected void updateCheckControls() {

		Rule[] rules = new Rule[ruleSet.size()];
		rules = ruleSet.getRules().toArray(rules);
		SelectionStats stats = selectionRatioIn(rules);
		boolean hasIssues = stats.dysfunctionCount > 0;
		updateButtonsFor(stats.selectedCount, stats.totalCount);
		
		String label = SWTUtil.stringFor(StringKeys.PREF_RULESET_ACTIVE_RULE_COUNT);
		activeCountDetails(
				label + " " + activeItemCount() + " / " + ruleSet.size(),
				hasIssues ? ResourceManager.imageFor(PMDUiConstants.ICON_WARN) : null
				);
	}

	protected void updateTooltipFor(TreeItem item, int columnIndex) {

		RuleLabelProvider provider = (RuleLabelProvider)treeViewer.getLabelProvider();
		String txt = provider.getDetailText(item.getData(), columnIndex);
		treeViewer.getTree().setToolTipText(txt);
	}
}
