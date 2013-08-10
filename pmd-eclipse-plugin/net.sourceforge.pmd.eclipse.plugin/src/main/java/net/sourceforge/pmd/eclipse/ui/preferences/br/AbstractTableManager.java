package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferenceUIStore;
import net.sourceforge.pmd.eclipse.ui.ColumnDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
/**
 * Core table behaviour including menus and support for hiding/showing columns
 * 
 * @author Brian Remedios
 */
public abstract class AbstractTableManager<T extends Object> implements SortListener {

	private final String	widgetId;		// for saving preference values
	protected boolean 		sortDescending;
	protected Object		columnSorter;	// cast to concrete type in subclass
	protected IPreferences	preferences;
	protected Menu 			headerMenu;	
	protected Menu 			tableMenu;
	
	protected final ColumnDescriptor[] 	availableColumns;	// columns shown in the rule treetable in the desired order
	
	private final Set<ColumnDescriptor>	hiddenColumns = new HashSet<ColumnDescriptor>();

	protected static PMDPlugin plugin = PMDPlugin.getDefault();
	
	   protected static class WidthChangeThread extends Thread {
	        private final int startWidth;
	        private final int endWidth;
	        private final ColumnWidthAdapter column;
	        
	        protected WidthChangeThread(int start, int end, ColumnWidthAdapter theColumn) {
	            super();
	            startWidth = start;
	            endWidth = end;
	            column = theColumn;
	        }
	        
	        private void delay(int mSec) {
	        	try {
	        		Thread.sleep(mSec);
	        	} catch (Exception ex) {
	        		
	        	}
	        }
	        
	        protected void setWidth(final int width) {
	            column.display().syncExec(new Runnable() {
	                public void run() {
	                //	delay(10);
	                	column.width(width);   
	                	}                
	            });
	        }
	        
	        public void run() {
	            if (endWidth > startWidth) {
	                for (int i = startWidth; i <= endWidth; i++ ) {
	                    setWidth(i);
	                }
	            } else {
	                for (int i = startWidth; i >= endWidth; i-- ) {
	                    setWidth(i);
	                }
	            }
	        }
	    }
	   
	protected interface ColumnWidthAdapter {	// unifies table and tree column behaviour as one type		
		int width();
		void width(int newWidth);
		Display display();
		void setData(String key, Object value);
		Object getData(String key);
	}
	
	protected static ColumnWidthAdapter adapterFor(final TableColumn column) {
		return new ColumnWidthAdapter() {
			public int width() { return column.getWidth();	}
			public void width(int newWidth) { column.setWidth(newWidth); }
			public Display display() { return column.getDisplay();	}
			public void setData(String key, Object value) { column.setData(key, value); }
 			public Object getData(String key) { return column.getData(key); }
		};
	}
	
	public AbstractTableManager(String theWidgetId, IPreferences thePreferences, ColumnDescriptor[] theColumns) {
		super();

		widgetId = theWidgetId;
		preferences = thePreferences;
		availableColumns = theColumns;
		
		loadHiddenColumns();
	}
	
	protected abstract ColumnWidthAdapter columnAdapterFor(ColumnDescriptor desc);
	
	protected void setupMenusFor(final Control control) {
		
		final Display display = control.getDisplay();
		Shell shell = control.getShell();
		
		setupMenus(shell);
		
		control.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point pt = display.map(null, control, new Point(event.x, event.y));
				Rectangle clientArea = clientAreaFor(control);
				boolean isHeader = clientArea.y <= pt.y && pt.y < (clientArea.y + headerHeightFor(control));
				if (!isHeader) adjustTableMenuOptions();
				setMenu(control, isHeader ? headerMenu : tableMenu);
			}
		});
		
		addDisposeListener(control);
	}
	
	protected abstract Rectangle clientAreaFor(Control tableOrTreeControl);
	
	protected abstract int headerHeightFor(Control tableOrTreeControl);
	
	protected abstract void setMenu(Control contro, Menu menu);
	
	protected abstract void saveItemSelections();
	
	public void saveUIState() {
		saveItemSelections();
	}
	
	protected void setupMenus(Shell shell) {
		
		headerMenu = new Menu(shell, SWT.POP_UP);
		addHeaderSelectionOptions(headerMenu);
		
		tableMenu = new Menu(shell, SWT.POP_UP);
		addTableSelectionOptions(tableMenu);
	}
	
	public void setTableMenu(Menu tableMenu) {
	    this.tableMenu = tableMenu;
	}

	protected void addDeleteListener(Control control) {

		control.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ev) {
				if (ev.character == SWT.DEL) {
					removeSelectedItems();
				}
			}
		});	
	}
	
	protected void addHeaderSelectionOptions(Menu menu) {

        for (ColumnDescriptor desc : availableColumns) {
            MenuItem columnItem = new MenuItem(menu, SWT.CHECK);
            columnItem.setSelection(!isHidden(desc));
            columnItem.setText(desc.label());
            final ColumnDescriptor columnDesc = desc;
            columnItem.addSelectionListener( new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    toggleColumnVisiblity(columnDesc);
                    }
                }
            );
        }
	}
	
	protected abstract void removeSelectedItems();
	
	
	protected void addTableSelectionOptions(Menu menu) {
		// subclasses to provide this
	}
	
	protected void adjustTableMenuOptions() {
		// subclasses to provide this
	}
	
	protected void addDisposeListener(Control control) {
		
		control.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				headerMenu.dispose();
				tableMenu.dispose();
			}
		});
	}
	
	public void visible(ColumnDescriptor column, boolean show) {
		
		if (show) {
			show(column);
		} else {
			hide(column);
		}
	}
	
	protected void show(ColumnDescriptor desc) {
		hiddenColumns.remove(desc);
					
		ColumnWidthAdapter cwa = columnAdapterFor(desc);
			
		Object widthData = cwa.getData("restoredWidth");
		int width = widthData == null ?
				desc.defaultWidth() :
				((Integer)widthData).intValue();
	    WidthChangeThread t = new WidthChangeThread(0, width, cwa);
	    t.start();
	}

	protected void hide(ColumnDescriptor desc) {
		hiddenColumns.add(desc);
		 
		ColumnWidthAdapter cwa = columnAdapterFor(desc);
		
		cwa.setData("restoredWidth", Integer.valueOf( cwa.width() ));
        WidthChangeThread t = new WidthChangeThread(cwa.width(), 0, cwa);
        t.start();
	}
	
	protected boolean isHidden(ColumnDescriptor desc) {
		return hiddenColumns.contains(desc);
	}

	protected boolean isActive(String item) {
		return preferences.isActive(item);
	}
	
	protected void isActive(String item, boolean flag) {
		preferences.isActive(item, flag);
	}
	
	protected void toggleColumnVisiblity(ColumnDescriptor desc) {

	    if (hiddenColumns.contains(desc)) {
	        show(desc);
	    } else {
	        hide(desc);
	    }    
	    
	    storeHiddenColumns();
	  //  redrawTable();
	}
	
	public void sortBy(Object accessor, Object context) {

		if (columnSorter == accessor) {
			sortDescending = !sortDescending;
		} else {
			columnSorter = accessor;
		}

		redrawTable(idFor(context), sortDescending ? SWT.DOWN : SWT.UP);
	}
	
	protected abstract String idFor(Object column);
	
	protected abstract void redrawTable(String columnId, int sortDirection);
	
	
	private void storeHiddenColumns() {
		
		Set<String> columnIds = new HashSet<String>(hiddenColumns.size());
		for (ColumnDescriptor desc : hiddenColumns) {
			columnIds.add(desc.id());
		}
		
		PreferenceUIStore.instance.hiddenColumnIds(columnIds);
	}
	
	private void loadHiddenColumns() {
		
		for (String columnId : PreferenceUIStore.instance.hiddenColumnIds() ) {
			for (ColumnDescriptor desc : availableColumns) {
				if (desc.id().equals(columnId)) {
					hiddenColumns.add(desc);
				}
			}
		}
	}
	
	/**
	 * Helper method to shorten message access
	 * @param key a message key
	 * @return requested message
	 */
	protected String getMessage(String key) {
		return PMDPlugin.getDefault().getStringTable().getString(key);
	}
}