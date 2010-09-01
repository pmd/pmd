package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * Tree table support, everything non-Rule related.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractTreeTableManager {


	protected ContainerCheckedTreeViewer  treeViewer;
	
	protected boolean			sortDescending;	
	private final Set<String>	hiddenColumnNames;
	private Button				selectAllButton;
	private Button				unSelectAllButton;
	private IPreferences		preferences;	
	private ModifyListener		modifyListener;
	private Label				activeCountLabel;

	private Menu headerMenu;
	private Menu tableMenu;
	
	private Map<Integer, List<Listener>> paintListeners = new HashMap<Integer, List<Listener>>();
	
	protected static PMDPlugin		plugin = PMDPlugin.getDefault();
	
	public AbstractTreeTableManager(IPreferences thePreferences) {
		super();

		preferences = thePreferences;
		hiddenColumnNames = PreferenceUIStore.instance.hiddenColumnNames();
	}
	
	protected Map<Integer, List<Listener>> paintListeners() {
		return paintListeners;
	}
	
	protected void createCheckBoxColumn(Tree tree) {
		
		TreeColumn tc = new TreeColumn(tree, 0);
		tc.setWidth(10);
		tc.setResizable(false);
		tc.pack();
		
        tc.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
               sortByCheckedItems();
            }
          });
	}
	/**
	 * Remove all rows, columns, and column painters in preparation
	 * for new columns.
	 *
	 * @return Tree
	 */
	protected Tree cleanupRuleTree() {

	    Tree tree = treeViewer.getTree();

        tree.clearAll(true);
        for(;tree.getColumns().length>0;) { // TODO also dispose any heading icons?
            tree.getColumns()[0].dispose();
        }

        // ensure we don't have any previous per-column painters left over
        for (Map.Entry<Integer, List<Listener>> entry : paintListeners.entrySet()) {
            int eventCode = entry.getKey().intValue();
            List<Listener> listeners = entry.getValue();
            for (Listener listener : listeners) {
                tree.removeListener(eventCode, listener);
            }
            listeners.clear();
        }

        return tree;
	}
	
	protected abstract boolean isQualifiedItem(Object item);
	
	public void saveUIState() {
		saveItemSelections();
	}
	
	protected abstract void saveItemSelections();
	
	public int activeItemCount() {

		Object[] checkedItems = treeViewer.getCheckedElements();
		int count = 0;

		for (Object item : checkedItems) {
			if (isQualifiedItem(item)) count++;
		}

		return count;
	}
	
	protected CheckboxTreeViewer treeViewer() { return treeViewer; }
	
	protected Button newImageButton(Composite parent, String imageId, String toolTipId) {
		
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(imageId));
		button.setToolTipText(getMessage(toolTipId));
		button.setEnabled(true);
		return button;
	}
	
	/**
	 *
	 * @param parent Composite
	 * @return Button
	 */
	protected Button buildSelectAllButton(Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_CHECK_ALL, StringKeys.PREF_RULESET_BUTTON_CHECK_ALL);
		button.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
				setAllItemsActive();
			}
		});

		return button;
	}
	
	/**
	 *
	 * @param parent Composite
	 * @return Button
	 */
	protected Button buildUnselectAllButton(Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_UNCHECK_ALL, StringKeys.PREF_RULESET_BUTTON_UNCHECK_ALL);		
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
	
	protected boolean isHidden(String columnName) {
		return hiddenColumnNames.contains(columnName);
	}
	
	protected abstract String nameFor(Object treeItemData);
	
	/**
	 * @param item TreeItem
	 * @param checked boolean
	 */
	private void check(TreeItem item, boolean checked) {

		item.setChecked(checked);
		Object itemData = item.getData();
		if (itemData == null || itemData instanceof RuleGroup) return;

		String name = nameFor(itemData);

		isActive(name, checked);

		updateCheckControls();
		setModified();
	}
	
	protected abstract void selectedItems(Object[] items); 
	
	protected abstract void removeSelectedItems();
	
	protected abstract void updateTooltipFor(TreeItem item, int columnIndex);
	
	// TODO move to util
	public static int columnIndexAt(TreeItem item, int xPosition) {

		TreeColumn[] cols = item.getParent().getColumns();
		Rectangle bounds = null;

		for(int i = 0; i < cols.length; i++){
			bounds = item.getBounds(i);
			if (bounds.x < xPosition &&  xPosition < (bounds.x + bounds.width)) {
				return i;
			}
		}
		return -1;
	}
	
	protected void buildTreeViewer(Composite parent) {
		
		int treeStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK;
		treeViewer = new ContainerCheckedTreeViewer(parent, treeStyle);

		final Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedItems(selection.toArray());
			}
		});
		
		tree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ev) {
				if (ev.character == SWT.DEL) {
					removeSelectedItems();
				}
			}
		});
		
		tree.addListener(SWT.Selection, new Listener() {
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
		
		tree.addListener(SWT.MouseMove, new Listener() {
	        public void handleEvent(Event event) {
	        	Point point = new Point(event.x, event.y);
	            TreeItem item = tree.getItem(point);
	            if (item != null) {
	            	int columnIndex = columnIndexAt(item, event.x);
	            	updateTooltipFor(item, columnIndex);
	            }
	        }
	    });
		
		setupMenusFor(tree);
	}
	
	private void setupMenusFor(final Tree tree) {
		
		final Display display = tree.getDisplay();
		Shell shell = tree.getShell();
		
		headerMenu = new Menu(shell, SWT.POP_UP);
		addColumnSelectionOptions(headerMenu);
		
		tableMenu = new Menu(shell, SWT.POP_UP);
		addTableSelectionOptions(tableMenu);
		
		tree.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point pt = display.map(null, tree, new Point(event.x, event.y));
				Rectangle clientArea = tree.getClientArea();
				boolean isHeader = clientArea.y <= pt.y && pt.y < (clientArea.y + tree.getHeaderHeight());
				if (!isHeader) adjustTableMenuOptions();
				tree.setMenu(isHeader ? headerMenu : tableMenu);
			}
		});
		
		tree.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				headerMenu.dispose();
				tableMenu.dispose();
			}
		});
	}
	
	protected void addTableSelectionOptions(Menu menu) {
		// subclasses to provide this
	}
	
	protected void adjustTableMenuOptions() {
		// subclasses to provide this
	}
	
	public void updated(Object item) {
		treeViewer.update(item, null);
	}
	
	/**
	 * Method checkPath.
	 * @param item TreeItem
	 * @param checked boolean
	 * @param grayed boolean
	 */
	protected void checkPath(TreeItem item, boolean checked, boolean grayed) {
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
	protected void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    check(item, checked);
	    TreeItem[] items = item.getItems();
	    for (TreeItem item2 : items) {
	        checkItems(item2, checked);
	    }
	    updateCheckControls();
	}
	
	protected void buildActiveCountLabel(Composite parent) {
        activeCountLabel = new Label(parent, 0);
        activeCountLabel.setText("---");
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER, GridData.CENTER, true, false, 1, 1);
        activeCountLabel.setAlignment(SWT.RIGHT);
	    activeCountLabel.setLayoutData(data);
	}
	
	protected void activeCountText(String msg) {
		activeCountLabel.setText(msg);
		activeCountLabel.getParent().pack();	// handle changing string length
	}
	
	protected abstract String[] columnLabels();
	
	protected boolean isActive(String item) {
		return preferences.isActive(item);
	}
	
	protected void isActive(String item, boolean flag) {
		preferences.isActive(item, flag);
	}
	
	protected void buildCheckButtons(Composite parent) {

	     selectAllButton = buildSelectAllButton(parent);
	     unSelectAllButton = buildUnselectAllButton(parent);
	}
	
	protected void addColumnSelectionOptions(Menu menu) {

        for (String columnLabel : columnLabels()) {
            MenuItem columnItem = new MenuItem(menu, SWT.CHECK);
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
	
	protected void toggleColumnVisiblity(String columnName) {

	    if (hiddenColumnNames.contains(columnName)) {
	        hiddenColumnNames.remove(columnName);
	    } else {
	        hiddenColumnNames.add(columnName);
	    }

	    PreferenceUIStore.instance.hiddenColumnNames(hiddenColumnNames);
	    redrawTable();
	}

    protected void redrawTable() {
    	redrawTable("-", -1);
    }
    
	protected void redrawTable(String sortColumnLabel, int sortDir) {
		
		TreeColumn sortColumn = columnFor(sortColumnLabel);
		treeViewer().getTree().setSortColumn(sortColumn);
		treeViewer().getTree().setSortDirection(sortDir);
	}
    
    private TreeColumn columnFor(String tooltipText) {
    	for (TreeColumn column : treeViewer().getTree().getColumns()) {
    		if (String.valueOf(column.getToolTipText()).equals(tooltipText)) return column;
    	}
    	return null;
    }
    
	protected void formatValueOn(StringBuilder target, Object value, Class<?> datatype) {

	    ValueFormatter formatter = FormatManager.formatterFor(datatype);
	    if (formatter != null) {
	        formatter.format(value, target);
	        return;
	    }

		target.append(value);     // should not get here..breakpoint here
	}
	
//	protected Button buildSortByCheckedItemsButton(Composite parent) {
//		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
//		button.setToolTipText("Sort by checked items");
//		button.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_SORT_CHECKED));
//
//		button.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//				sortByCheckedItems();
//			}
//		});
//
//		return button;
//	}	

	protected abstract void setAllItemsActive();
	
	protected abstract void sortByCheckedItems();
	

		
	public void modifyListener(ModifyListener theListener) {
		modifyListener = theListener;
	}

	/**
	 * Helper method to shorten message access
	 * @param key a message key
	 * @return requested message
	 */
	protected String getMessage(String key) {
		return PMDPlugin.getDefault().getStringTable().getString(key);
	}

	protected void setModified() {
		if (modifyListener != null) modifyListener.setModified();
	}
	
	protected void updateButtonsFor(int[] selectionRatio) {
		
		selectAllButton.setEnabled( selectionRatio[0] < selectionRatio[1]);
		unSelectAllButton.setEnabled( selectionRatio[0] > 0);
	}
		
	protected abstract void updateCheckControls();
	
	/**
	 * Refresh the list
	 */
	protected void refresh() {
		try {
			treeViewer().getControl().setRedraw(false);
			treeViewer().refresh();
		} catch (ClassCastException e) {
			plugin.logError("Ignoring exception while refreshing table", e);
		} finally {
			treeViewer().getControl().setRedraw(true);
		}
	}
	
}