package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleSetSelectionDialog;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.Configuration;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.DescriptionPanelManager;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.ExamplePanelManager;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.ExclusionPanelManager;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.PerRulePropertyPanelManager;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.RulePropertyManager;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.XPathPanelManager;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.designer.Designer;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This page is used to modify preferences only. They are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can be accessed directly via the preference store.
 *
 * @author Philippe Herlin
 * @author Brian Remedios
 */

public class PMDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, ValueChangeListener, RuleSortListener {

	public static PMDPreferencePage activeInstance = null;

	// columns shown in the rule treetable in the desired order
	private static final RuleColumnDescriptor[] availableColumns = new RuleColumnDescriptor[] {
		TextColumnDescriptor.name,
		//TextColumnDescriptor.priorityName,
		IconColumnDescriptor.priority,
		TextColumnDescriptor.since,
		TextColumnDescriptor.ruleSetName,
		TextColumnDescriptor.ruleType,
		TextColumnDescriptor.minLangVers,		
		TextColumnDescriptor.language,
		ImageColumnDescriptor.filterViolationRegex,    // regex text -> compact color squares (for comparison)
		ImageColumnDescriptor.filterViolationXPath,    // xpath text -> compact color circles (for comparison)
		TextColumnDescriptor.properties,
		};

	// last item in this list is the grouping used at startup
	private static final Object[][] groupingChoices = new Object[][] {
		{ TextColumnDescriptor.ruleSetName,       StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULESET},   
		{ TextColumnDescriptor.since,             StringKeys.MSGKEY_PREF_RULESET_GROUPING_PMD_VERSION },
		{ TextColumnDescriptor.priorityName,      StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY },
		{ TextColumnDescriptor.ruleType,          StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULE_TYPE },
		{ TextColumnDescriptor.language,		  StringKeys.MSGKEY_PREF_RULESET_COLUMN_LANGUAGE },
        { ImageColumnDescriptor.filterViolationRegex, StringKeys.MSGKEY_PREF_RULESET_GROUPING_REGEX },
		{ null, 								  StringKeys.MSGKEY_PREF_RULESET_GROUPING_NONE }
		};

//	private static final int RuleTableFraction = 55;       // percent of screen height vs property tabs
	private static final Map<Class<?>, ValueFormatter> formattersByType = new HashMap<Class<?>, ValueFormatter>();

	static {   // used to render property values in short form in main table
	    formattersByType.put(String.class,      ValueFormatter.StringFormatter);
        formattersByType.put(String[].class,    ValueFormatter.MultiStringFormatter);
        formattersByType.put(Boolean.class,     ValueFormatter.BooleanFormatter);
        formattersByType.put(Boolean[].class,   ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Integer.class,     ValueFormatter.NumberFormatter);
        formattersByType.put(Integer[].class,   ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Long.class,        ValueFormatter.NumberFormatter);
        formattersByType.put(Long[].class,      ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Float.class,       ValueFormatter.NumberFormatter);
        formattersByType.put(Float[].class,     ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Double.class,      ValueFormatter.NumberFormatter);
        formattersByType.put(Double[].class,    ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Character.class,   ValueFormatter.ObjectFormatter);
        formattersByType.put(Character[].class, ValueFormatter.ObjectArrayFormatter);
        formattersByType.put(Class.class,       ValueFormatter.TypeFormatter);
        formattersByType.put(Class[].class,     ValueFormatter.MultiTypeFormatter);        
        formattersByType.put(Method.class,      ValueFormatter.MethodFormatter);
        formattersByType.put(Method[].class,    ValueFormatter.MultiMethodFormatter);
        formattersByType.put(Object[].class,    ValueFormatter.ObjectArrayFormatter);
	}

	private CheckboxTreeViewer   ruleTreeViewer;
	private Button			     addRuleButton;
	private Button 			     removeRuleButton;
//	private Button 			     editRuleButton;
	private RuleSet 			 ruleSet;       // TODO - what is this used for?  - br
	private TabFolder 		     tabFolder;
	private Set<Object> 		 checkedRules = new HashSet<Object>();
	private Menu                 ruleListMenu;
	private Set<String>          hiddenColumnNames = new HashSet<String>();
	
	private RulePropertyManager[]   rulePropertyManagers;

	private boolean					sortDescending;
	private RuleFieldAccessor 		columnSorter = RuleFieldAccessor.name;	// initial sort
	private RuleColumnDescriptor  	groupingColumn;

    private Map<Integer, List<Listener>> paintListeners = new HashMap<Integer, List<Listener>>();
	
	private RuleSelection               ruleSelection; // may hold rules and/or group nodes
	private Map<RulePriority, MenuItem> priorityMenusByPriority;
	private Map<String, MenuItem>       rulesetMenusByName;
    
	private boolean 			modified = false;
	private static PMDPlugin	plugin = PMDPlugin.getDefault(); 
	
	/**
	 * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setDescription(getMessage(StringKeys.MSGKEY_PREF_RULESET_TITLE));
		activeInstance = this;
		
//		hiddenColumnNames.add(TextColumnDescriptor.since.label());
//		hiddenColumnNames.add(TextColumnDescriptor.externalURL.label());
//		hiddenColumnNames.add(TextColumnDescriptor.minLangVers.label());
//		hiddenColumnNames.add(TextColumnDescriptor.exampleCount.label());
		
		hiddenColumnNames = PreferenceUIStore.instance.hiddenColumnNames();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
    protected void performDefaults() {
		populateRuleTable();
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
    public boolean performOk() {
		
		PreferenceUIStore.instance.save();
		
		if (modified) {
			updateRuleSet();
			rebuildProjects();
		}

		return super.performOk();
	}

	@Override
	public boolean performCancel() {
		
		PreferenceUIStore.instance.save();
		
		return super.performCancel();
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	@Override
    protected Control createContents(Composite parent) {
	    
	    populateRuleset();
	    
		Composite composite = new Composite(parent, SWT.NULL);
		layoutControls(composite);
		return composite;
	}

	private Composite createRuleSection(Composite parent) {
	    
	    Composite ruleSection = new Composite(parent, SWT.NULL);
	    
	    // Create the controls (order is important !)
        Composite groupCombo = buildGroupCombo(ruleSection, StringKeys.MSGKEY_PREF_RULESET_RULES_GROUPED_BY);

        Tree ruleTree = buildRuleTreeViewer(ruleSection);
        groupBy(null);

        Composite ruleTableButtons = buildRuleTableButtons(ruleSection);
        Composite rulePropertiesTableButtons = buildRulePropertiesTableButtons(ruleSection);

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

	public static void formatValueOn(StringBuilder target, Object value, Class<?> datatype) {

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
	public static String propertyStringFrom(Rule rule) {

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

	public static String ruleSetNameFrom(Rule rule) {
		return ruleSetNameFrom( rule.getRuleSetName() );
	}

    public static String ruleSetNameFrom(String rulesetName) {

        int pos = rulesetName.toUpperCase().indexOf("RULES");
        return pos < 0 ? rulesetName : rulesetName.substring(0, pos-1);
    }
	
	private void redrawTable() {
		groupBy(groupingColumn);
	}

	private Composite buildGroupCombo(Composite parent, String comboLabelKey) {

	     Composite panel = new Composite(parent, 0);
	     GridLayout layout = new GridLayout(2, false);
	     panel.setLayout(layout);

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
	        
	     return panel;
	 }
	
	/**
	 * Method buildTabFolder.
	 * @param parent Composite
	 * @return TabFolder
	 */
	private TabFolder buildTabFolder(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.TOP);

		rulePropertyManagers = new RulePropertyManager[] {
		    buildPropertyTab(tabFolder,    0, SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_TAB_PROPERTIES)),
		    buildDescriptionTab(tabFolder, 1, SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_TAB_DESCRIPTION)),
		    buildUsageTab(tabFolder,       2, SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_TAB_FILTERS)),
		    buildXPathTab(tabFolder,       3, SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_TAB_XPATH)),    
		    buildExampleTab(tabFolder,     4, SWTUtil.stringFor(StringKeys.MSGKEY_PREF_RULESET_TAB_EXAMPLES)),
		    };

		tabFolder.pack();
		return tabFolder;
	}

	/**
	 * @param parent TabFolder
	 * @param index int
	 */
	private RulePropertyManager buildPropertyTab(TabFolder parent, int index, String title) {

	    TabItem tab = new TabItem(parent, 0, index);
	    tab.setText(title);

		PerRulePropertyPanelManager manager = new PerRulePropertyPanelManager(this);
		tab.setControl(
		    manager.setupOn(parent, this)
		    );
		manager.tab(tab);
		return manager;
	}

	/**
	 * @param parent TabFolder
	 * @param index int
	 */
	private RulePropertyManager buildDescriptionTab(TabFolder parent, int index, String title) {

		TabItem tab = new TabItem(parent, 0, index);
		tab.setText(title);

        DescriptionPanelManager manager = new DescriptionPanelManager(this);
        tab.setControl(
            manager.setupOn(parent)
            );
        manager.tab(tab);
        return manager;
	}

    /**
     * @param parent TabFolder
     * @param index int
     */
    private RulePropertyManager buildXPathTab(TabFolder parent, int index, String title) {

        TabItem tab = new TabItem(parent, 0, index);
        tab.setText(title);

        XPathPanelManager manager = new XPathPanelManager(this);
        tab.setControl(
            manager.setupOn(parent)
            );
        manager.tab(tab);
        return manager;
    }
	
	/**
     * @param parent TabFolder
     * @param index int
     */
    private RulePropertyManager buildExampleTab(TabFolder parent, int index, String title) {

        TabItem tab = new TabItem(parent, 0, index);
        tab.setText(title);

        ExamplePanelManager manager = new ExamplePanelManager(this);
        tab.setControl(
            manager.setupOn(parent)
            );
        manager.tab(tab);
        return manager;
    }    
    
	/**
	 *
	 * @param parent TabFolder
	 * @param index int
	 * @param title String
	 */
	private RulePropertyManager buildUsageTab(TabFolder parent, int index, String title) {

		TabItem tab = new TabItem(parent, 0, index);
		tab.setText(title);

		ExclusionPanelManager manager = new ExclusionPanelManager(this);
		tab.setControl(
			manager.setupOn(
					parent,
					SWTUtil.stringFor(StringKeys.MSGKEY_LABEL_EXCLUSION_REGEX),
					SWTUtil.stringFor(StringKeys.MSGKEY_LABEL_XPATH_EXCLUSION),
					SWTUtil.stringFor(StringKeys.MSGKEY_LABEL_COLOUR_CODE)
					)
			);
		manager.tab(tab);
		return manager;
	}
	
	/**
	 * Create buttons for rule table management
	 * @param parent Composite
	 * @return Composite
	 */
	private Composite buildRuleTableButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 3;
		composite.setLayout(gridLayout);

		removeRuleButton = buildRemoveRuleButton(composite);
//		editRuleButton = buildEditRuleButton(composite);
		addRuleButton = buildAddRuleButton(composite);
		Button importRuleSetButton = buildImportRuleSetButton(composite);
		Button exportRuleSetButton = buildExportRuleSetButton(composite);
		Button clearAllButton = buildClearAllButton(composite);
		Button ruleDesignerButton = buildRuleDesignerButton(composite);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		removeRuleButton.setLayoutData(data);

//		data = new GridData();
//		data.horizontalAlignment = GridData.FILL;
//		editRuleButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		addRuleButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		importRuleSetButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		exportRuleSetButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		clearAllButton.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.END;
		ruleDesignerButton.setLayoutData(data);

		return composite;
	}

	/**
	 * Create buttons for rule properties table management
	 * @param parent Composite
	 * @return Composite
	 */
	private Composite buildRulePropertiesTableButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.wrap = false;
		rowLayout.pack = false;
		composite.setLayout(rowLayout);

		return composite;
	}

	/**
	 * Build rule table viewer
	 * @param parent Composite
	 * @return Tree
	 */
	private Tree buildRuleTreeViewer(Composite parent) {

		int treeStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK;
		ruleTreeViewer = new CheckboxTreeViewer(parent, treeStyle);

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

	private void updateTooltipFor(TreeItem item, int columnIndex) {
		
		RuleLabelProvider provider = (RuleLabelProvider)ruleTreeViewer.getLabelProvider();		
		String txt = provider.getDetailText(item.getData(), columnIndex);		
		ruleTreeViewer.getTree().setToolTipText(txt);
	}
	
	public static Image imageFor(RulePriority priority) {
	    String codePath = PMDUiConstants.buttonCodePathFor(priority, false);
	    return ResourceManager.imageFor(codePath);
	}
	
	private boolean currentSelectionHasNonDefaultValues() {
		
		 return ruleSelection == null ?
			false : ruleSelection.allSelectedRulesUseDefaultValues();
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
	    
	    MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
	    removeItem.setText("Remove");
	    removeItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                removeSelectedRules();
            }
        });
	    
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
	
	private static String[] columnLabels() {
	    String[] names = new String[availableColumns.length];
	    for (int i=0; i<availableColumns.length; i++) {
	        names[i] = availableColumns[i].label();
	    }
	    return names;
	}
	
	private void toggleColumnVisiblity(String columnName) {
	    
	    if (hiddenColumnNames.contains(columnName)) {
	        hiddenColumnNames.remove(columnName);
	    } else {
	        hiddenColumnNames.add(columnName);
	    }

	    PreferenceUIStore.instance.hiddenColumnNames(hiddenColumnNames);
	    redrawTable();
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

	private void adjustMenuUseDefaultsOption() {
		
	}
	
	// if all the selected rules/ruleGroups reference a common ruleset name
	// then check that item and disable it, do the reverse for all others.
    private void adjustMenuRulesetSettings() {
        
        String rulesetName = ruleSetNameFrom(ruleSelection.commonRuleset());
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

    private void adjustMenuPrioritySettings() {
        
        RulePriority priority = ruleSelection.commonPriority();
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
	
	private boolean hasPriorityGrouping() {
	    return 
	        groupingColumn == TextColumnDescriptor.priorityName || 
	        groupingColumn == TextColumnDescriptor.priority;
	}
	
	private void setPriority(RulePriority priority) {
	    
	    ruleSelection.setPriority(priority);
	    
	    if (hasPriorityGrouping()) {
	        redrawTable();
	    } else {
	        ruleTreeViewer.update(ruleSelection.allRules().toArray(), null);
	    }
	}
	
	private String[] rulesetNames() {
	    
	    Set<String> names = new HashSet<String>();
	    for (Rule rule : ruleSet.getRules()) {
	        names.add(ruleSetNameFrom(rule));  // if we strip out the 'Rules' portions then we don't get matches...need to rename rulesets
	    }
	    return names.toArray(new String[names.size()]);
	}
	
	private void setRuleset(String rulesetName) {
	    // TODO - awaiting support in PMD itself
	}
		
	/**
	 * @param item Object[]
	 */
	private void selectedItems(Object[] items) {

	    ruleSelection = new RuleSelection(items);
	    for (RulePropertyManager manager : rulePropertyManagers) manager.manage(ruleSelection);
        
	    Rule rule = ruleSelection.soleRule();
	    
		removeRuleButton.setEnabled(rule != null);
//		editRuleButton.setEnabled(rule != null && ruleSelection.hasOneRule());
	}
	
	/**
	 * Method groupBy.
	 * @param chosenColumn RuleColumnDescriptor
	 */
	private void groupBy(RuleColumnDescriptor chosenColumn) {
		
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
	}

	/**
	 * @param item TreeItem
	 * @param checked boolean
	 */
	private void check(TreeItem item, boolean checked) {

		item.setChecked(checked);
		if (item.getData() instanceof RuleGroup) return;

		if (checked) {
			checkedRules.add(item.getData());
		} else {
			checkedRules.remove(item.getData());
		}
	}

	private void removeSelectedRules() {
	    
	    int removeCount = ruleSelection.removeAllFrom(ruleSet);
	    if (removeCount == 0) return;
	    	    
        setModified(true);
        
        try {
            refresh();
        } catch (Throwable t) {
            ruleTreeViewer.setSelection(null);
        }
	}
	
	/**
	 * Build the remove rule button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildRemoveRuleButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_REMOVERULE));
		button.setEnabled(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				removeSelectedRules();
			}
		});
		return button;
	}

	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildEditRuleButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_EDITRULE));
		button.setEnabled(false);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection)ruleTreeViewer.getSelection();
				Rule rule = (Rule)selection.getFirstElement();

				RuleDialog dialog = new RuleDialog(getShell(), rule);
				int result = dialog.open();
				if (result == RuleDialog.OK) {
					setModified(true);
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
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildAddRuleButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_ADDRULE));
		button.setEnabled(true);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				RuleDialog dialog = new RuleDialog(getShell());
				int result = dialog.open();
				if (result == RuleDialog.OK) {
					Rule addedRule = dialog.getRule();
					ruleSet.addRule(addedRule);
					setModified(true);
					try {
						refresh();
					} catch (Throwable t) {
						plugin.logError("Exception when refreshing the rule table", t);
					}

					setModified(true);
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
	 * Build the import ruleset button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildImportRuleSetButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(getShell());
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
						setModified(true);
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

	/**
	 * Build the export rule set button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildExportRuleSetButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				String fileName = dialog.open();
				if (fileName != null) {
					try {
						File file = new File(fileName);
						boolean flContinue = true;
						if (file.exists()) {
							flContinue = MessageDialog.openConfirm(getShell(),
									getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
									getMessage(StringKeys.MSGKEY_CONFIRM_RULESET_EXISTS));
						}

						InputDialog input = null;
						if (flContinue) {
							input = new InputDialog(getShell(),
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
							MessageDialog.openInformation(getShell(), getMessage(StringKeys.MSGKEY_INFORMATION_TITLE),
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
	 * Build the clear all button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildClearAllButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_CLEARALL));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				if (MessageDialog.openConfirm(getShell(), getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
						getMessage(StringKeys.MSGKEY_CONFIRM_CLEAR_RULESET))) {
					ruleSet.getRules().clear();
					setModified(true);
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
	 * Build the Rule Designer button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildRuleDesignerButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_RULEDESIGNER));
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
	
	private void populateRuleset() {
	    
	    RuleSet defaultRuleSet = plugin.getPreferencesManager().getRuleSet();
        ruleSet = new RuleSet();
        ruleSet.addRuleSet(defaultRuleSet);
        ruleSet.setName(defaultRuleSet.getName());
        ruleSet.setDescription(Util.asCleanString(defaultRuleSet.getDescription()));
        ruleSet.addExcludePatterns(defaultRuleSet.getExcludePatterns());
        ruleSet.addIncludePatterns(defaultRuleSet.getIncludePatterns());
	}

	/**
	 * Populate the rule table
	 */
	private void populateRuleTable() {		
		ruleTreeViewer.setInput(ruleSet);
		checkSelections();
	}

	private void checkSelections() {
		ruleTreeViewer.setCheckedElements(checkedRules.toArray());
	}

	/**
	 * Helper method to shorten message access
	 * @param key a message key
	 * @return requested message
	 */
	protected String getMessage(String key) {
		return plugin.getStringTable().getString(key);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
    protected IPreferenceStore doGetPreferenceStore() {
		return plugin.getPreferenceStore();
	}

	/**
	 * Returns the activeInstance.
	 * @return PMDPreferencePage
	 */
	public static PMDPreferencePage getActiveInstance() {
		return activeInstance;
	}

	/**
	 * Returns the isModified.
	 * @return boolean
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Sets the isModified.
	 * @param isModified The isModified to set
	 */
	public void setModified(boolean isModified) {
		this.modified = isModified;
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

	/**
	 * Update the configured rule set
	 * Update also all configured projects
	 */
	private void updateRuleSet() {
		try {
			ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
			monitorDialog.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					plugin.getPreferencesManager().setRuleSet(ruleSet);
				}
			});
		} catch (Exception e) {
			plugin.logError("Exception updating all projects after a preference change", e);
		}
	}

	/**
	 * If user wants to, rebuild all projects
	 */
	private void rebuildProjects() {
		if (MessageDialog.openQuestion(getShell(), getMessage(StringKeys.MSGKEY_QUESTION_TITLE),
				getMessage(StringKeys.MSGKEY_QUESTION_RULES_CHANGED))) {
			try {
				ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
				monitorDialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
						} catch (CoreException e) {
							plugin.logError("Exception building all projects after a preference change", e);
						}
					}
				});
			} catch (Exception e) {
				plugin.logError("Exception building all projects after a preference change", e);
			}
		}
	}

	/**
	 * Select and show a particular rule in the table
	 * @param rule Rule
	 */
	protected void selectAndShowRule(Rule rule) {
		Tree tree = ruleTreeViewer.getTree();
		TreeItem[] items = tree.getItems();
		for (TreeItem item : items) {
			Rule itemRule = (Rule)item.getData();
			if (itemRule.equals(rule)) {
	//			tree.setSelection(tree.indexOf(items[i]));
				tree.showSelection();
				break;
			}
		}
	}

   public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) {
        // TODO enhance to recognize default values
        modified = true;                
        ruleTreeViewer.update(rule, null);
    }
	
	public void changed(RuleSelection selection, PropertyDescriptor<?> desc, Object newValue) {
		// TODO enhance to recognize default values
		modified = true;				
		for (Rule rule : selection.allRules()) {
			if (newValue != null) {		// non-reliable update behaviour, alternate trigger option - weird
				ruleTreeViewer.getTree().redraw();
		//		System.out.println("doing redraw");
			} else {
				ruleTreeViewer.update(rule, null);
		//		System.out.println("viewer update");
			}
		}
		for (RulePropertyManager manager : rulePropertyManagers) {
		    manager.validate();
		}
	}

	public void sortBy(RuleFieldAccessor accessor) {

		if (columnSorter == accessor) {
			sortDescending = !sortDescending;
		} else {
			columnSorter = accessor;
		}
		redrawTable();
	}

	
}