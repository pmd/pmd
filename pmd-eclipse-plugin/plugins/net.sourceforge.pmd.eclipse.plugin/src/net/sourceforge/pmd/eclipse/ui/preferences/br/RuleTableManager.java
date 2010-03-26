package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.io.File;
import java.io.FileOutputStream;
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
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleSetSelectionDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.Configuration;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.designer.Designer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * Instantiates and manages a tree table widget holding all the rules in a ruleset.
 * 
 * @author Brian Remedios
 */
public class RuleTableManager implements RuleSortListener, ValueChangeListener {

	private RuleSet						ruleSet;
	
	private ContainerCheckedTreeViewer  ruleTreeViewer;
	
	private boolean						sortDescending;
	private RuleFieldAccessor 			columnSorter = RuleFieldAccessor.name;	// initial sort
	private RuleColumnDescriptor  		groupingColumn;

	private Set<String>          		 hiddenColumnNames = new HashSet<String>();
    private Map<Integer, List<Listener>> paintListeners = new HashMap<Integer, List<Listener>>();
    
	private Map<RulePriority, MenuItem> priorityMenusByPriority;
	private Map<String, MenuItem>       rulesetMenusByName;

	private RuleFieldAccessor			checkedColumnAccessor;
	private RuleSelection               ruleSelection; // may hold rules and/or group nodes
	
	private Menu                 		ruleListMenu;
	private Button			     		addRuleButton;
	private Button			     		removeRuleButton;
	private Button						sortByCheckedButton;
	private Button						selectAllButton;
	private Button						unSelectAllButton;
	private Label 						activeCountLabel;
	
	private final RuleColumnDescriptor[] 		availableColumns;	// columns shown in the rule treetable in the desired order
	private final Map<Class<?>, ValueFormatter> formattersByType;
	private final IPreferences 					preferences;
	
	private ModifyListener						modifyListener;
	private RuleSelectionListener				ruleSelectionListener;
	
	protected static PMDPlugin		plugin = PMDPlugin.getDefault(); 
	
	public static String ruleSetNameFrom(Rule rule) {
		return ruleSetNameFrom( rule.getRuleSetName() );
	}

	public List<Rule> activeRules() {
					
		Object[] checkedItems = ruleTreeViewer.getCheckedElements();
		List<Rule> activeOnes = new ArrayList<Rule>(checkedItems.length);
		
		for (Object item : checkedItems) {
			if (item instanceof Rule) {
				activeOnes.add((Rule)item);
			}
		}
		
		return activeOnes;
	}
	
	public int activeRuleCount() {
		
		Object[] checkedItems = ruleTreeViewer.getCheckedElements();
		int count = 0;
		
		for (Object item : checkedItems) {
			if (item instanceof Rule) count++;
		}
		
		return count;
	}
	
	public void modifyListener(ModifyListener theListener) {
		modifyListener = theListener;
	}
	
	public void selectionListener(RuleSelectionListener theListener) {
		ruleSelectionListener = theListener;
	}
	
    public static String ruleSetNameFrom(String rulesetName) {

        int pos = rulesetName.toUpperCase().indexOf("RULES");
        return pos < 0 ? rulesetName : rulesetName.substring(0, pos-1);
    }
	
	public void formatValueOn(StringBuilder target, Object value, Class<?> datatype) {

	    ValueFormatter formatter = formattersByType.get(datatype);
	    if (formatter != null) {
	        formatter.format(value, target);
	        return;
	    }

		target.append(value);     // should not get here..breakpoint here
	}

	/**
	 * @param rule Rule
	 * @return String
	 */
	public String propertyStringFrom(Rule rule) {

		Map<PropertyDescriptor<?>, Object> valuesByProp = Configuration.filteredPropertiesOf(rule);

		if (valuesByProp.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();

		Iterator<PropertyDescriptor<?>> iter = valuesByProp.keySet().iterator();

		PropertyDescriptor<?> desc = iter.next();
		sb.append(desc.name()).append(": ");
		formatValueOn(sb, rule.getProperty(desc), desc.type());

		while (iter.hasNext()) {
			desc = iter.next();
			sb.append(", ").append(desc.name()).append(": ");
			formatValueOn(sb, rule.getProperty(desc), desc.type());
		}
		return sb.toString();
	}
    
	public RuleTableManager(RuleColumnDescriptor[] theColumns, Map<Class<?>, ValueFormatter> theFormattersByType, IPreferences thePreferences) {
		
		availableColumns = theColumns;
		formattersByType = theFormattersByType;
		preferences = thePreferences;

		hiddenColumnNames = PreferenceUIStore.instance.hiddenColumnNames();
		checkedColumnAccessor = createCheckedItemAccessor();
	}
	
	private RuleFieldAccessor createCheckedItemAccessor() {
		
		return new BasicRuleFieldAccessor() {
			public Comparable<Boolean> valueFor(Rule rule) {
				return preferences.isActive(rule.getName());
			}
		};
	}
	
	private void addColumnSelectionOptions(Menu menu) {
	    
	    MenuItem showMenu = new MenuItem(menu, SWT.CASCADE);
	    showMenu.setText("Show");
        Menu columnsSubMenu = new Menu(menu);
        showMenu.setMenu(columnsSubMenu);
         
        for (String columnLabel : columnLabels()) {
            MenuItem columnItem = new MenuItem(columnsSubMenu, SWT.CHECK);
            columnItem.setSelection(!hiddenColumnNames.contains(columnLabel));
            columnItem.setText(columnLabel);
            final String nameStr = columnLabel;
            columnItem.addSelectionListener( new SelectionAdapter() {                
                public void widgetSelected(SelectionEvent e) { 
                    toggleColumnVisiblity(nameStr); 
                    }
                }
            );
         }  
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
        
        RulePriority priority = ruleSelection == null ? null : ruleSelection.commonPriority();
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
        
        String rulesetName = ruleSelection == null ? null : ruleSetNameFrom(ruleSelection.commonRuleset());
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
		
	}
	
	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildAddRuleButton(final Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_ADD));
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_ADDRULE));
		button.setEnabled(true);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				RuleDialog dialog = new RuleDialog(parent.getShell());
				int result = dialog.open();
				if (result == RuleDialog.OK) {
					Rule addedRule = dialog.getRule();
					ruleSet.addRule(addedRule);
					setModified();
					try {
						refresh();
					} catch (Throwable t) {
						plugin.logError("Exception when refreshing the rule table", t);
					}
				}
			}
		});

		return button;
	}

	/**
	 * Helper method to shorten message access
	 * @param key a message key
	 * @return requested message
	 */
	protected String getMessage(String key) {
		return PMDPlugin.getDefault().getStringTable().getString(key);
	}
	
	private void setModified() {
		if (modifyListener != null) modifyListener.setModified();
	}
	
	/**
	 * Build the remove rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildRemoveRuleButton(Composite parent) {
		
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_DELETE));
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_REMOVERULE));

		button.addSelectionListener(new SelectionAdapter() {
			@Override
           public void widgetSelected(SelectionEvent event) {
				removeSelectedRules();
			}
		});
		return button;
	}
	
	private void removeSelectedRules() {
	    
		if (ruleSelection == null) return;
		
	    int removeCount = ruleSelection.removeAllFrom(ruleSet);
	    if (removeCount == 0) return;
	    	    
        setModified();
        
        try {
            refresh();
        } catch (Throwable t) {
            ruleTreeViewer.setSelection(null);
        }
	}
		
	/**
	 * Build the export rule set button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildExportRuleSetButton(final Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_EXPORT));
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);
				String fileName = dialog.open();
				if (fileName != null) {
					try {
						File file = new File(fileName);
						boolean flContinue = true;
						if (file.exists()) {
							flContinue = MessageDialog.openConfirm(parent.getShell(),
									getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
									getMessage(StringKeys.MSGKEY_CONFIRM_RULESET_EXISTS));
						}

						InputDialog input = null;
						if (flContinue) {
							input = new InputDialog(parent.getShell(),
									getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE),
									getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_RULESET_DESCRIPTION),
									ruleSet.getDescription() == null ? "" : ruleSet.getDescription().trim(), null);
							flContinue = input.open() == InputDialog.OK;
						}

						if (flContinue) {
							ruleSet.setName(FileUtil.getFileNameWithoutExtension(file.getName()));
							ruleSet.setDescription(input.getValue());
							OutputStream out = new FileOutputStream(fileName);
							IRuleSetWriter writer = plugin.getRuleSetWriter();
							writer.write(out, ruleSet);
							out.close();
							MessageDialog.openInformation(parent.getShell(), getMessage(StringKeys.MSGKEY_INFORMATION_TITLE),
									getMessage(StringKeys.MSGKEY_INFORMATION_RULESET_EXPORTED));
						}
					} catch (Exception e) {
						plugin.showError(getMessage(StringKeys.MSGKEY_ERROR_EXPORTING_RULESET), e);
					}
				}
			}
		});

		return button;
	}
	
	/**
	 * Build the import ruleset button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildImportRuleSetButton(final Composite parent) {
		
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_IMPORT));
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET));
		button.setEnabled(true);
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(parent.getShell());
				dialog.open();
				if (dialog.getReturnCode() == RuleSetSelectionDialog.OK) {
					try {
						RuleSet selectedRuleSet = dialog.getSelectedRuleSet();
						if (dialog.isImportByReference()) {
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
			}
		});

		return button;
	}
	
	public Composite buildGroupCombo(Composite parent, String comboLabelKey, final Object[][] groupingChoices) {

	     Composite panel = new Composite(parent, 0);
	     GridLayout layout = new GridLayout(6, false);
	     panel.setLayout(layout);

	     sortByCheckedButton = buildSortByCheckedItemsButton(panel);
	     selectAllButton = buildSelectAllButton(panel);
	     unSelectAllButton = buildUnselectAllButton(panel);
	     
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
	        
        activeCountLabel = new Label(panel, 0);
        activeCountLabel.setText("---");
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.RIGHT;
	    activeCountLabel.setLayoutData(data);
        
	     return panel;
	 }
	
	/**
	 * Build the Rule Designer button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildRuleDesignerButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_EDITOR));
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_RULEDESIGNER));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
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

		int treeStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK;
		ruleTreeViewer = new ContainerCheckedTreeViewer(parent, treeStyle);

		final Tree ruleTree = ruleTreeViewer.getTree();
		ruleTree.setLinesVisible(true);
		ruleTree.setHeaderVisible(true);

		ruleTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedItems(selection.toArray());
			}
		});

		ruleListMenu = createMenuFor(ruleTree);
		ruleTree.setMenu(ruleListMenu);
		ruleTree.addListener(SWT.MenuDetect, new Listener () {		    
	        public void handleEvent (Event event) {
	            popupRuleSelectionMenu(event);
	        }
	    });		
		
		ruleTree.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
	            if (event.detail == SWT.CHECK) {
	                TreeItem item = (TreeItem) event.item;
	                boolean checked = item.getChecked();
	                checkItems(item, checked);
	                checkPath(item.getParentItem(), checked, false);
	            }
	         //   if (!checkedRules.isEmpty()) System.out.println(checkedRules.iterator().next());
	        }
	    });

		ruleTree.addListener(SWT.MouseMove, new Listener() {
	        public void handleEvent(Event event) {
	        	Point point = new Point(event.x, event.y);
	            TreeItem item = ruleTree.getItem(point);
	            if (item != null) {
	            	int columnIndex = columnIndexAt(item, event.x);
	            	updateTooltipFor(item, columnIndex);
	            }
	        }
	    });
		
		ruleTreeViewer.setCheckStateProvider(createCheckStateProvider());
		
		return ruleTree;
	}
	
	/**
	 * 
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildSelectAllButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_CHECK_ALL));
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_CHECK_ALL));
		
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				setAllRulesActive();
			}
		});

		return button;
	}
	
	private Button buildSortByCheckedItemsButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setToolTipText("Sort by checked items");
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_SORT_CHECKED));
		
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				sortByCheckedItems();
			}
		});

		return button;
	}
	
	/**
	 * 
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildUnselectAllButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_UNCHECK_ALL));
		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_UNCHECK_ALL));
		
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				preferences.getActiveRuleNames().clear();
				treeViewer().setCheckedElements(new Object[0]);
				setModified();
				updateCheckControls();
			}
		});

		return button;
	}
	
    public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) {
        // TODO enhance to recognize default values                
        ruleTreeViewer.update(rule, null);
        setModified();
    }
	
	public void changed(RuleSelection selection, PropertyDescriptor<?> desc, Object newValue) {
		// TODO enhance to recognize default values
					
		for (Rule rule : selection.allRules()) {
			if (newValue != null) {		// non-reliable update behaviour, alternate trigger option - weird
				ruleTreeViewer.getTree().redraw();
		//		System.out.println("doing redraw");
			} else {
				ruleTreeViewer.update(rule, null);
		//		System.out.println("viewer update");
			}
		}
		setModified();
	}
	
	/**
	 * Method checkPath.
	 * @param item TreeItem
	 * @param checked boolean
	 * @param grayed boolean
	 */
	private void checkPath(TreeItem item, boolean checked, boolean grayed) {
	    if (item == null) return;
	    if (grayed) {
	        checked = true;
	    } else {
	        int index = 0;
	        TreeItem[] items = item.getItems();
	        while (index < items.length) {
	            TreeItem child = items[index];
	            if (child.getGrayed() || checked != child.getChecked()) {
	                checked = grayed = true;
	                break;
	            }
	            index++;
	        }
	    }
	    check(item, checked);
	    item.setGrayed(grayed);
	    checkPath(item.getParentItem(), checked, grayed);
	}

	/**
	 * @param item TreeItem
	 * @param checked boolean
	 */
	private void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    check(item, checked);
	    TreeItem[] items = item.getItems();
	    for (TreeItem item2 : items) {
	        checkItems(item2, checked);
	    }
	    updateCheckControls();
	}

	/**
	 * @param item TreeItem
	 * @param checked boolean
	 */
	private void check(TreeItem item, boolean checked) {

		item.setChecked(checked);
		Object itemData = item.getData();
		if (itemData == null || itemData instanceof RuleGroup) return;

		String name = ((Rule)itemData).getName();
		
		preferences.isActive(name, checked);

		updateCheckControls();
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
	
	/**
	 * Remove all rows, columns, and column painters in preparation
	 * for new columns.
	 * 
	 * @return Tree
	 */
	private Tree cleanupRuleTree() {
	    
	    Tree ruleTree = ruleTreeViewer.getTree();

        ruleTree.clearAll(true);
        for(;ruleTree.getColumns().length>0;) { // TODO also dispose any heading icons?
            ruleTree.getColumns()[0].dispose();
        }

        // ensure we don't have any previous per-column painters left over
        for (Map.Entry<Integer, List<Listener>> entry : paintListeners.entrySet()) {
            int eventCode = entry.getKey().intValue();
            List<Listener> listeners = entry.getValue();
            for (Listener listener : listeners) {
                ruleTree.removeListener(eventCode, listener);
            }
            listeners.clear();
        }
        
        return ruleTree;
	}
	
	private int columnIndexAt(TreeItem item, int xPosition) {
		
		TreeColumn[] cols = ruleTreeViewer.getTree().getColumns();
		Rectangle bounds = null;
		
		for(int i = 0; i < cols.length; i++){
			bounds = item.getBounds(i);
			if (bounds.x < xPosition &&  xPosition < (bounds.x + bounds.width)) {
				return i;
			}
		}
		return -1;
	}
	
	private String[] columnLabels() {
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
					return preferences.isActive(((Rule)item).getName());
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
    	    priorityItem.addSelectionListener( new SelectionListener() {    	        
                public void widgetSelected(SelectionEvent e) { 
                    setPriority(pri); 
                    }
                public void widgetDefaultSelected(SelectionEvent e) {  }}
    	    );
	    }
	    
//	    MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
//	    removeItem.setText("Remove");
//	    removeItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                removeSelectedRules();
//            }
//        });
	    
        MenuItem useDefaultsItem = new MenuItem(menu, SWT.PUSH);
        useDefaultsItem.setText("Use defaults");
        useDefaultsItem.setEnabled(false);
        useDefaultsItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
           //     useDefaultValues();
            }
        });
	   	                   
        return menu;
	}
	
    private TreeColumn columnFor(String tooltipText) {
    	for (TreeColumn column : ruleTreeViewer.getTree().getColumns()) {
    		if (column.getToolTipText().equals(tooltipText)) return column;
    	}
    	return null;
    }
    
	/**
	 * Method groupBy.
	 * @param chosenColumn RuleColumnDescriptor
	 */
	public void groupBy(RuleColumnDescriptor chosenColumn) {
		
		List<RuleColumnDescriptor> visibleColumns = new ArrayList<RuleColumnDescriptor>(availableColumns.length);
		for (RuleColumnDescriptor desc : availableColumns) {
		    if (desc == chosenColumn) continue;   // redundant, don't include it
		    if (hiddenColumnNames.contains(desc.label())) continue;
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
		ruleTreeViewer.setInput(ruleSet);
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
	
    private void redrawTable() {
    	redrawTable("-", -1);
    }
    
	private void redrawTable(String sortColumnLabel, int sortDir) {
		groupBy(groupingColumn);
		
		TreeColumn sortColumn = columnFor(sortColumnLabel);
		ruleTreeViewer.getTree().setSortColumn(sortColumn);
		ruleTreeViewer.getTree().setSortDirection(sortDir);
	}
	

	/**
	 * Refresh the list
	 */
	protected void refresh() {
		try {
			ruleTreeViewer.getControl().setRedraw(false);
			ruleTreeViewer.refresh();
		} catch (ClassCastException e) {
			plugin.logError("Ignoring exception while refreshing table", e);
		} finally {
			ruleTreeViewer.getControl().setRedraw(true);
		}
	}
	
	private void restoreSavedRuleSelections() {
		
		Set<String> names = PreferenceUIStore.instance.selectedRuleNames();
		List<Rule> rules = new ArrayList<Rule>();
		for (String name : names) rules.add(ruleSet.getRuleByName(name));
		
		IStructuredSelection selection = new StructuredSelection(rules);
		ruleTreeViewer.setSelection(selection);
	}
	
	public RuleSet ruleSet() { return ruleSet; }
	
	private String[] rulesetNames() {
	    
	    Set<String> names = new HashSet<String>();
	    for (Rule rule : ruleSet.getRules()) {
	        names.add(ruleSetNameFrom(rule));  // if we strip out the 'Rules' portions then we don't get matches...need to rename rulesets
	    }
	    return names.toArray(new String[names.size()]);
	}
	
	private void saveRuleSelections() {
		
		IStructuredSelection selection = (IStructuredSelection)ruleTreeViewer.getSelection();
		
		List<String> ruleNames = new ArrayList<String>();
		for (Object item : selection.toList()) {
			if (item instanceof Rule)
				ruleNames.add(((Rule)item).getName());
		}

		PreferenceUIStore.instance.selectedRuleNames(ruleNames);
	}
	
	public void saveUIState() {
		saveRuleSelections();
	}
	
	/**
	 * @param item Object[]
	 */
	private void selectedItems(Object[] items) {

	    ruleSelection = new RuleSelection(items);
	    if (ruleSelectionListener != null) {
	    	ruleSelectionListener.selection(ruleSelection);
	    }
	    
	    if (removeRuleButton != null) removeRuleButton.setEnabled(items.length > 0);
	}
	
	private int[] selectionRatioIn(Rule[] rules) {
		
		int selectedCount = 0;
		for (Rule rule : rules) {
			if (preferences.isActive(rule.getName())) selectedCount++;
		}
		return new int[] { selectedCount , rules.length };
	}
	
	private void setAllRulesActive() {
		for (Rule rule : ruleSet.getRules()) {
			preferences.isActive(rule.getName(), true);
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
	        ruleTreeViewer.update(ruleSelection.allRules().toArray(), null);
	    }
	    
	    setModified();
	}
	
	private void setRuleset(String rulesetName) {
	    // TODO - awaiting support in PMD itself
	}
	/**
	 * Method setupTreeColumns.
	 * @param columnDescs RuleColumnDescriptor[]
	 * @param groupingField RuleFieldAccessor
	 */
	private void setupTreeColumns(RuleColumnDescriptor[] columnDescs, RuleFieldAccessor groupingField) {

		Tree ruleTree = cleanupRuleTree();
		
		for (int i=0; i<columnDescs.length; i++) columnDescs[i].newTreeColumnFor(ruleTree, i, this, paintListeners);

		ruleTreeViewer.setLabelProvider(new RuleLabelProvider(columnDescs));
		ruleTreeViewer.setContentProvider(
				new RuleSetTreeItemProvider(groupingField, "??", Util.comparatorFrom(columnSorter, sortDescending))
				);

		ruleTreeViewer.setInput(ruleSet);
        checkSelections();

		TreeColumn[] columns = ruleTree.getColumns();
		for (TreeColumn column : columns) column.pack();
	}
	
	private void sortByCheckedItems() {
		sortBy(checkedColumnAccessor, ruleTreeViewer.getTree().getColumn(0));
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
	
	private CheckboxTreeViewer treeViewer() { return ruleTreeViewer; }
	
	private void toggleColumnVisiblity(String columnName) {
	    
	    if (hiddenColumnNames.contains(columnName)) {
	        hiddenColumnNames.remove(columnName);
	    } else {
	        hiddenColumnNames.add(columnName);
	    }

	    PreferenceUIStore.instance.hiddenColumnNames(hiddenColumnNames);
	    redrawTable();
	}
	
	public void useRuleSet(RuleSet theSet) {
		ruleSet = theSet;
	}
	
	public void updated(Rule rule) {
		ruleTreeViewer.update(rule, null);
	}
	
	private void updateCheckControls() {
		
		Rule[] rules = new Rule[ruleSet.size()];
		rules = ruleSet.getRules().toArray(rules);
		int[] selectionRatio = selectionRatioIn(rules);
		
		selectAllButton.setEnabled( selectionRatio[0] < selectionRatio[1]);
		unSelectAllButton.setEnabled( selectionRatio[0] > 0);
		sortByCheckedButton.setEnabled( (selectionRatio[0] != 0) && (selectionRatio[0] != selectionRatio[1]));
		
		String label = SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_ACTIVE_RULE_COUNT);
		activeCountLabel.setText(label + " " + activeRuleCount() + " / " + ruleSet.size());
	}
	
	private void updateTooltipFor(TreeItem item, int columnIndex) {
		
		RuleLabelProvider provider = (RuleLabelProvider)ruleTreeViewer.getLabelProvider();		
		String txt = provider.getDetailText(item.getData(), columnIndex);		
		ruleTreeViewer.getTree().setToolTipText(txt);
	}
}
