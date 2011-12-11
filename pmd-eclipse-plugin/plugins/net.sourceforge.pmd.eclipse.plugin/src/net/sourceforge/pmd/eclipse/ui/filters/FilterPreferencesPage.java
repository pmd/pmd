package net.sourceforge.pmd.eclipse.ui.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.BasicTableLabelProvider;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.AbstractPMDPreferencePage;
import net.sourceforge.pmd.eclipse.ui.preferences.br.BasicTableManager;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The available report formats and their properties.
 *
 * @author Brian Remedios
 */
public class FilterPreferencesPage extends AbstractPMDPreferencePage implements ValueChangeListener, SizeChangeListener {

    private TableViewer tableViewer;
    private Button 		addButton;
    private Button		removeButton;
    
    private Button		excludeButt;
    private Button		includeButt;
    private Button		cpdButt;
    private Button		pmdButt;
    private Text		patternField;
    private BasicTableManager reportTableMgr;
    
    
    private static Image IncludeIcon;
    private static Image ExcludeIcon;

	private static final RGB ProtoTransparentColour = new RGB(1,1,1);	// almost full black, unlikely to be used
	
	public static Image typeIconFor(FilterHolder holder) {
		return holder.isInclude ? includeIcon() : excludeIcon();
	}
	
	private static Image includeIcon() {
		
		if (IncludeIcon != null) return IncludeIcon;
		
		IncludeIcon = ShapePainter.newDrawnImage(
					Display.getCurrent(), 
					16, 
					16, 
					Shape.plus,
					ProtoTransparentColour, 
					new RGB(0,255,0)
					);
		
		return IncludeIcon;
	}
	
	private static Image excludeIcon() {
		
		if (ExcludeIcon != null) return ExcludeIcon;
		
		ExcludeIcon = ShapePainter.newDrawnImage(
					Display.getCurrent(), 
					16, 
					16, 
					Shape.minus,
					ProtoTransparentColour, 
					new RGB(255,0,0)
					);
		
		return ExcludeIcon;
	}
    
    /**
     * Create and initialize the controls of the page
     *
    
     * @param parent Composite
     * @return Control
     * @see PreferencePage#createContents */
    @Override
	protected Control createContents(Composite parent) {

        // Create parent composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        composite.setLayout(layout);

        // Create panels
        Composite filterGroup = buildFilterGroup(composite);
        Composite buttonPanel = buildTableButtons(composite);
        
        // Layout children
        filterGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        buttonPanel.setLayoutData(new GridData());
        buildFilterEditor(parent);
        
        return composite;
    }

    private FilterHolder[] currentFilters() {
    	
    	List<FilterHolder> holders = new ArrayList<FilterHolder>();
    	
    	 RuleSet ruleSet = plugin.getPreferencesManager().getRuleSet();

         for (String pattern : ruleSet.getExcludePatterns() ) {
        	 holders.add( new FilterHolder(pattern, true, false, false) );
         }
         for (String pattern : ruleSet.getIncludePatterns() ) {
        	 holders.add( new FilterHolder(pattern, true, false, true) );
         }
    	return holders.toArray(new FilterHolder[holders.size()]);
    }
	/**
     * Build the group of priority preferences
     * @param parent the parent composite
    
     * @return the group widget */
    private Composite buildFilterGroup(Composite parent) {

        IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			public void dispose() {	}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
			public Object[] getElements(Object inputElement) { return (FilterHolder[])inputElement;	}
        };
        BasicTableLabelProvider labelProvider = new BasicTableLabelProvider(FilterColumnUI.VisibleColumns);

        reportTableMgr = new BasicTableManager("renderers", null, FilterColumnUI.VisibleColumns);
        tableViewer = reportTableMgr.buildTableViewer(
        		parent,
        		SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK
        		);
        reportTableMgr.setupColumns(FilterColumnUI.VisibleColumns);
        
        Table table = tableViewer.getTable();
        table.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1) );

        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(contentProvider);
        table.setHeaderVisible(true);
   //     labelProvider.addColumnsTo(table);

        tableViewer.setInput( currentFilters() );

        selectCheckedFilters();

        TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) column.pack();

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedPatterns(filtersIn(selection.toList()));
				}
			});

        tableViewer.getTable().addListener(SWT.Selection, new Listener () {
    		public void handleEvent(Event event) {
    			if (event.detail == SWT.CHECK) {
    				checked(event.item);
    			}
    		}
    	});
        
        return parent;
    }

	private void selectedPatterns(Collection<FilterHolder> holders) {
		
		setState(holders, includeButt,  FilterHolder.IncludeAccessor);
		setState(holders, pmdButt,		FilterHolder.PMDAccessor);
		setState(holders, cpdButt, 		FilterHolder.CPDAccessor);		
		setValue(holders, patternField, FilterHolder.PatternAccessor);
	}
    
    private static void setState(Collection<FilterHolder> holders, Button button, FilterHolder.Accessor accessor) {
    	
    	Boolean state = FilterHolder.boolValueOf(holders, accessor);
    	if (state == null) {
    		button.setGrayed(true);
    		return;
    	}

   		button.setSelection(state);
    }
    
    private static void setValue(Collection<FilterHolder> holders, Text field, FilterHolder.Accessor accessor) {
    	
    	String text = FilterHolder.textValueOf(holders, accessor);
    	field.setText(text);
    }
    
    private void setAllPMD(boolean state) {
    	for (FilterHolder fh : selectedFilters()) {
    		fh.forPMD = state;
    	}
    }
    
    private void setAllCPD(boolean state) {
    	for (FilterHolder fh : selectedFilters()) {
    		fh.forCPD = state;
    	}
    }
    
    private void setAllInclude(boolean state) {
    	for (FilterHolder fh : selectedFilters()) {
    		fh.isInclude = state;
    	}
    }
    
    private void setAllPatterns(String pattern) {
    	for (FilterHolder fh : selectedFilters()) {
    		fh.pattern = pattern;
    	}
    }
    
    private void buildFilterEditor(Composite parent) {
    
    	  Composite editorPanel = new Composite(parent, SWT.None);
          editorPanel.setLayoutData( new GridData(GridData.FILL, GridData.FILL, false, true) );
          editorPanel.setLayout(new GridLayout(3, false));

          Label typeLabel = new Label(editorPanel, SWT.None);
          typeLabel.setLayoutData( new GridData());
          typeLabel.setText("Type:");
          
          excludeButt = createButton(editorPanel, SWT.RADIO, excludeIcon(), "Exclude");
          excludeButt.addSelectionListener( new SelectionAdapter() {
        	  public void widgetSelected(SelectionEvent se) {
        		  setAllInclude(includeButt.getSelection());
        		  tableViewer.refresh();
        	  }
          });
          
          includeButt = createButton(editorPanel, SWT.RADIO, includeIcon(), "Include");
          includeButt.addSelectionListener( new SelectionAdapter() {
        	  public void widgetSelected(SelectionEvent se) {
        		  setAllInclude(includeButt.getSelection());
        		  tableViewer.refresh();
        	  }
          });
          
          Label contextLabel = new Label(editorPanel, SWT.None);
          contextLabel.setLayoutData( new GridData());
          contextLabel.setText("Applies to:");
          
          pmdButt = createButton(editorPanel, SWT.CHECK, "PMD");
          pmdButt.addSelectionListener( new SelectionAdapter() {
        	  public void widgetSelected(SelectionEvent se) {
        		  setAllPMD(pmdButt.getSelection());
        		  tableViewer.refresh();
        	  }
          });
          
          cpdButt = createButton(editorPanel, SWT.CHECK, "CPD");
          cpdButt.addSelectionListener( new SelectionAdapter() {
        	  public void widgetSelected(SelectionEvent se) {
        		  setAllCPD(cpdButt.getSelection());
        		  tableViewer.refresh();
        	  }
          });
          
          Label patternLabel = new Label(editorPanel, SWT.None);
          patternLabel.setLayoutData( new GridData());
          patternLabel.setText("Pattern:");

          patternField = new Text(editorPanel, SWT.BORDER);
          patternField.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1) );
          patternField.addFocusListener(new FocusAdapter() {
        	  public void focusLost(FocusEvent fe) {
        		  setAllPatterns(patternField.getText());
        		  tableViewer.refresh();
        	  }
          });
    }

	private static Button createButton(Composite panel, int type, String label) {
		Button butt = new Button(panel, type);
		butt.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		butt.setText(label);
		return butt;
	}
	
	private static Button createButton(Composite panel, int type, Image image, String tooltip) {
		Button butt = new Button(panel, type);
		butt.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		butt.setImage(image);
		butt.setToolTipText(tooltip);
		return butt;
	}
	
	/**
	 * Create buttons for rule table management
	 * @param parent Composite
	 * @return Composite
	 */
	public Composite buildTableButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 3;
		composite.setLayout(gridLayout);

		addButton = buildAddButton(composite);
addButton.setEnabled(false);
		removeButton = buildRemoveButton(composite);
removeButton.setEnabled(false);

		GridData data = new GridData();
		addButton.setLayoutData(data);

		return composite;
	}
    
    /**
     * Check the filters as noted from the preferences.
     */
    private void selectCheckedFilters() {

    	Set<String> activeOnes = preferences.activeExclusionPatterns();
    	activeOnes.addAll(preferences.activeInclusionPatterns());
    	
    	for (TableItem item : tableViewer.getTable().getItems()) {
    		FilterHolder holder = (FilterHolder)item.getData();
    		item.setChecked(
    				activeOnes.contains(holder.pattern)
   				);
    	}
    }

    /**
     *
     * @return Set<String>
     */
    private Set<FilterHolder> currentCheckedFilters() {

    	Set<FilterHolder> holders = new HashSet<FilterHolder>();
    	for (Object holder : checkedItems(tableViewer.getTable())) {
    		holders.add((FilterHolder) holder);
    	}
    	return holders;
    }

    /**
    *
    * @return Set<String>
    */
   private Set<FilterHolder> selectedFilters() {

   	Set<FilterHolder> holders = new HashSet<FilterHolder>();
   	for (Object tItem : tableViewer.getTable().getSelection()) {
   		holders.add((FilterHolder) (((TableItem)tItem).getData()));
   	}
   	return holders;
   }
    
    /**
    *
    * @return Set<String>
    */
   private static Collection<FilterHolder> filtersIn(List<?> tableItems) {

	   	Set<FilterHolder> holders = new HashSet<FilterHolder>();
	   	for (Object tItem : tableItems) {
	   		holders.add((FilterHolder) tItem);
	   	}
	   	return holders;
   }
    
    /**
     * Method checkedItems.
     * @param table Table
     * @return Set<Object>
     */
    private static Set<Object> checkedItems(Table table) {

    	Set<Object> checkedItems = new HashSet<Object>();

    	for (TableItem ti : table.getItems()) {
    		if (ti.getChecked()) checkedItems.add( ti.getData() );
    	}
    	return checkedItems;
    }

    /**
     *
     * @param item Object
     */
    private void checked(Object item) {

    	//FIXME
    	boolean matches = currentCheckedFilters().equals(preferences.activeExclusionPatterns());

    	setModified(!matches);
    }

    /**
    
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults() */
    @Override
	protected void performDefaults() {

    }

    /**
     *
     * @return boolean
     * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
     */
    @Override
	public boolean performCancel() {
    	// clear out any changes for next possible usage
    	selectCheckedFilters();
        return true;
    }

    private static Set<String> patternsIn(Collection<FilterHolder> holders, boolean getInclusions) {
    	
    	if (holders.isEmpty()) return Collections.emptySet();
    	
    	Set<String> patterns = new HashSet<String>();
    	for (FilterHolder holder : holders) {
    		if (holder.isInclude == getInclusions) {
    			patterns.add(holder.pattern);
    		}
    	}
    	return patterns;
    }
	
	protected Button newImageButton(Composite parent, String imageId, String toolTipId) {
		
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setImage(ResourceManager.imageFor(imageId));
		button.setToolTipText(getMessage(toolTipId));
		button.setEnabled(true);
		return button;
	}
	
	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildAddButton(final Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_ADD, StringKeys.PREF_RULESET_BUTTON_ADDRULE);
		
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	addNewFilter();
				}
			});

		return button;
	}
	
	private void addNewFilter() {
		FilterHolder holder = new FilterHolder("<finish this>", true, false, false);
		
		// TODO finish this
	}
	
	/**
	 * Build the edit rule button
	 * @param parent Composite
	 * @return Button
	 */
	public Button buildRemoveButton(final Composite parent) {
		
		Button button = newImageButton(parent, PMDUiConstants.ICON_BUTTON_DELETE, StringKeys.PREF_RULESET_BUTTON_REMOVERULE);
		
		button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	deleteSelected();
				}
			});

		return button;
	}
	
	private void deleteSelected() {
		IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
		if (sel.isEmpty()) return;
		
		// TODO finish this
	}
	
    /**
     * @return boolean
     * @see org.eclipse.jface.preference.IPreferencePage#performOk() */
    @Override
	public boolean performOk() {

    	Set<FilterHolder> filters = currentCheckedFilters();
        preferences.activeExclusionPatterns( patternsIn(filters, false) );
        preferences.activeInclusionPatterns( patternsIn(filters, true) );

        preferences.sync();
        PMDPlugin.getDefault().applyLogPreferences(preferences);

        return true;
    }

	/**
	 * Method descriptionId.
	 * @return String
	 */
	@Override
	protected String descriptionId() {
		return "???";	// TODO
	}

	public void changed(PropertySource source, PropertyDescriptor<?> desc,	Object newValue) {
		// TODO enable/disable save/cancel buttons
	}

	// ignore these

	public void addedRows(int newRowCount) { }
	public void changed(RuleSelection rule, PropertyDescriptor<?> desc,	Object newValue) { }

}
