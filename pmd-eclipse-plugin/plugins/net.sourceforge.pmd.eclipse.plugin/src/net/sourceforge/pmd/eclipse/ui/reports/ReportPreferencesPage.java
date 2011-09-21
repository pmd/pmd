package net.sourceforge.pmd.eclipse.ui.reports;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.BasicTableLabelProvider;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.AbstractPMDPreferencePage;
import net.sourceforge.pmd.eclipse.ui.preferences.br.BasicTableManager;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.FormArranger;
import net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers.PerRulePropertyPanelManager;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.StringUtil;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The available report formats and their properties.
 *
 * @author Brian Remedios
 */
public class ReportPreferencesPage extends AbstractPMDPreferencePage implements ValueChangeListener, SizeChangeListener {
	    
    private Text		additionalCommentText;
    private Label		sampleLabel;
    private Button		showPerspectiveBox;
    private Button		useProjectBuildPath;
    private Button		checkCodeOnSave;   
    private Spinner		maxViolationsPerFilePerRule;
    private Button		reviewPmdStyleBox;
    private TableViewer tableViewer;
    private FormArranger formArranger;
    
    private BasicTableManager reportTableMgr;
     
    /**
     * Create and initialize the controls of the page
     *
     * @see PreferencePage#createContents
     */
    protected Control createContents(Composite parent) {

        // Create parent composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        composite.setLayout(layout);

        // Create groups
        Group reportGroup = buildReportGroup(composite);
        Group propertyGroup = buildPropertyGroup(composite);
        
        // Layout children
        propertyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        reportGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return composite;
    }

    /**
     * Build the group of renderer property preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildPropertyGroup(final Composite parent) {

        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText("Properties");
        group.setLayout(new GridLayout(1, false));

        formArranger = new FormArranger(group, PerRulePropertyPanelManager.editorFactoriesByPropertyType, this, this);

        return group;
    }
    
	/**
     * Build the group of priority preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildReportGroup(final Composite parent) {

        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText("Formats");
        group.setLayout(new GridLayout(2, false));
        
        IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			public void dispose() {	}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
			public Object[] getElements(Object inputElement) { return (Renderer[])inputElement;	}        	
        };
        BasicTableLabelProvider labelProvider = new BasicTableLabelProvider(ReportColumnUI.VisibleColumns);
        
        reportTableMgr = new BasicTableManager("renderers", null, ReportColumnUI.VisibleColumns);   
        tableViewer = reportTableMgr.buildTableViewer(
        		group, 
        		SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK
        		);
        reportTableMgr.setupColumns(ReportColumnUI.VisibleColumns);
        
        Table table = tableViewer.getTable();
        table.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1) );
        
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(contentProvider);
        table.setHeaderVisible(true);
   //     labelProvider.addColumnsTo(table);
        tableViewer.setInput( ReportManager.instance.allRenderers() );
        
        selectCheckedRenderers();
        
        TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) column.pack();
        
        Composite editorPanel = new Composite(group, SWT.None);
        editorPanel.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true) );
        editorPanel.setLayout(new GridLayout(4, false));

        Label nameLabel = new Label(editorPanel, SWT.None);
        nameLabel.setLayoutData( new GridData());
        nameLabel.setText("Name:");
        
        final Label rendererName = new Label(editorPanel, SWT.BORDER);
        rendererName.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true) );
        
        final Button suppressed = new Button(editorPanel, SWT.CHECK);
        suppressed.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        suppressed.setText("Show suppressed violations");
        
        Label descLabel = new Label(editorPanel, SWT.None);
        descLabel.setLayoutData( new GridData());
        descLabel.setText("Description:");
        
        final Label descValue = new Label(editorPanel, SWT.BORDER);
        descValue.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true, 3, 1) );
     
        
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				List items = selection.toList();
				selectedRenderers(items, rendererName, descValue, suppressed);
				if (items.size() == 1) {
					formArranger.arrangeFor((Renderer)items.get(0));
					} else {
						formArranger.clearChildren();
					}
			}} );
        
        tableViewer.getTable().addListener(SWT.Selection, new Listener () {
    		public void handleEvent(Event event) {
    			if (event.detail == SWT.CHECK) {
    				checked(event.item);
    			}
    		}
    	});
        
        suppressed.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
		

			}} );
		
		rendererName.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				setName( rendererName.getText() );	
			}} );
				
        return group;
    }

    private void selectCheckedRenderers() {
    	
    	Set<String> activeNames = preferences.activeReportRenderers();
    	
    	for (TableItem item : tableViewer.getTable().getItems()) {
    		Renderer ren = (Renderer)item.getData();
    		item.setChecked(
    				activeNames.contains( ren.getName()) 
    				);
    	}
    }
    
    private Set<String> currentCheckedRenderers() {
    	
    	Set<String> names = new HashSet<String>();
    	for (Object renderer : checkedItems(tableViewer.getTable())) {
    		names.add(((Renderer)renderer).getName());
    	}
    	return names;
    }
    
    private static Set<Object> checkedItems(Table table) {
    	
    	Set<Object> checkedItems = new HashSet<Object>();
    	
    	for (TableItem ti : table.getItems()) {
    		if (ti.getChecked()) checkedItems.add( ti.getData() );
    	}
    	return checkedItems;
    }
    
    private void checked(Object item) {
    	
    	boolean matches = currentCheckedRenderers().equals(preferences.activeReportRenderers());
    	
    	setModified(!matches); 
    }
    
    private void setName(String newName) {
    	
    	if (StringUtil.isEmpty(newName)) return;
    	
    	for (Renderer ren : selectedRenderers()) {
    		//ren.label = newName;
    	}
    	tableViewer.refresh();
    }
    
    private Renderer[] selectedRenderers() {
    	
    	Object[] items = ((IStructuredSelection)tableViewer.getSelection()).toArray();
    	Renderer[] renderers = new Renderer[items.length];
    	for (int i=0; i<renderers.length; i++) renderers[i] = (Renderer)items[i];
    	return renderers;
    }
    
    private static void selectedRenderers(List<Renderer> items, Label nameField, Label descField, Button suppressed) {

    	if (items.size() != 1 ) {
    		nameField.setText("");
    		return;
    	}
    	
    	Renderer renderer = items.get(0);
    	
    	nameField.setText( renderer.getName());
    	descField.setText( renderer.getDescription());
    	suppressed.setSelection(renderer.isShowSuppressedViolations());
     }
 
    /**
     * Build a label
     */
    private Label buildLabel(Composite parent, String msgKey) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(msgKey == null ? "" : getMessage(msgKey));
        return label;
    }
    
    /**
     * Build the check box for showing the PMD perspective
     * @param viewGroup the parent composite
     *
     */
    private Button buildCheckCodeOnSaveButton(Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText("Check code after saving");
        button.setSelection(preferences.isCheckAfterSaveEnabled());
        button.setEnabled(false);	// FIXME - make it real
        return button;
    }
    
    /**
     * Build the check box for showing the PMD perspective
     * @param viewGroup the parent composite
     *
     */
    private Button buildShowPerspectiveBoxButton(Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(getMessage(StringKeys.PREF_GENERAL_LABEL_SHOW_PERSPECTIVE));
        button.setSelection(preferences.isPmdPerspectiveEnabled());
        return button;
    }

    /**
     * Build the check box for enabling using Project Build Path
     * @param viewGroup the parent composite
     */
    private Button buildUseProjectBuildPathButton(Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(getMessage(StringKeys.PREF_GENERAL_LABEL_USE_PROJECT_BUILD_PATH));
        button.setSelection(preferences.isProjectBuildPathEnabled());
        return button;
    }

    /**
     * Build the text for maximum violations per file per rule
     *
     * @param parent
     * @return
     */
    private Spinner buildMaxViolationsPerFilePerRuleText(Composite parent) {
    	
    	Composite comp = new Composite(parent, 0);
    	comp.setLayout(new GridLayout(2, false));
    	
        Label label = buildLabel(comp, StringKeys.PREF_GENERAL_LABEL_MAX_VIOLATIONS_PFPR);
        label.setLayoutData( new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));
        
        final Spinner spinner = new Spinner(comp, SWT.BORDER);
        spinner.setLayoutData( new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_CENTER, true, false, 1, 1));
        spinner.setMinimum(preferences.getMaxViolationsPerFilePerRule());
        spinner.setToolTipText(getMessage(StringKeys.PREF_GENERAL_TOOLTIP_MAX_VIOLATIONS_PFPR));
        return spinner;
    }

    public static void setSelection(Button button, boolean flag) {
    	if (button == null || button.isDisposed()) return;
    	button.setSelection(flag);
    }
    
    public static void setText(Text field, String txt) {
    	if (field == null || field.isDisposed()) return;
    	field.setText(txt);
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
    	
        setText(additionalCommentText, IPreferences.REVIEW_ADDITIONAL_COMMENT_DEFAULT);

        setSelection(showPerspectiveBox, 	IPreferences.PMD_PERSPECTIVE_ENABLED_DEFAULT);
        setSelection(checkCodeOnSave , 		IPreferences.PMD_CHECK_AFTER_SAVE_DEFAULT);      
        setSelection(useProjectBuildPath, 	IPreferences.PROJECT_BUILD_PATH_ENABLED_DEFAULT);
        setSelection(reviewPmdStyleBox, 	IPreferences.REVIEW_PMD_STYLE_ENABLED_DEFAULT);

        if (maxViolationsPerFilePerRule != null) {
            maxViolationsPerFilePerRule.setMinimum(IPreferences.MAX_VIOLATIONS_PFPR_DEFAULT);
        }

    }

    /**
     * Update the sample label when the additional comment text is modified
     */
    protected void updateSampleLabel() {
        String pattern = additionalCommentText.getText();
        try {
            String commentText = MessageFormat.format(pattern, new Object[] { System.getProperty("user.name", ""), new Date() });

            sampleLabel.setText(commentText);
            setMessage(getMessage(StringKeys.PREF_GENERAL_HEADER), NONE);
            setValid(true);

        } catch (IllegalArgumentException e) {
            setMessage(getMessage(StringKeys.PREF_GENERAL_MESSAGE_INCORRECT_FORMAT), ERROR);
            setValid(false);
        }
    }
    
    public boolean performCancel() {
    	// clear out any changes for next possible usage
    	selectCheckedRenderers();
        return true;
    }
    
    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
    	    	
        if (additionalCommentText != null) {
            preferences.setReviewAdditionalComment(additionalCommentText.getText());
        }

        if (showPerspectiveBox != null) {
            preferences.setPmdPerspectiveEnabled(showPerspectiveBox.getSelection());
        }

        if (checkCodeOnSave != null) {
            preferences.isCheckAfterSaveEnabled(checkCodeOnSave.getSelection());
        }
        
        if (useProjectBuildPath != null) {
            preferences.setProjectBuildPathEnabled(useProjectBuildPath.getSelection());
        }

        if (maxViolationsPerFilePerRule != null) {
            preferences.setMaxViolationsPerFilePerRule(Integer.valueOf(maxViolationsPerFilePerRule.getText()).intValue());
        }

        if (reviewPmdStyleBox != null) {
            preferences.setReviewPmdStyleEnabled(reviewPmdStyleBox.getSelection());
        }

        preferences.activeReportRenderers( currentCheckedRenderers() );
        
        preferences.sync();
        PMDPlugin.getDefault().applyLogPreferences(preferences);

        return true;
    }

	@Override
	protected String descriptionId() {
		return "???";	// TODO
	}

	public void addedRows(int newRowCount) {
		// TODO Auto-generated method stub
		
	}

	public void changed(RuleSelection rule, PropertyDescriptor<?> desc,
			Object newValue) {
		// TODO Auto-generated method stub
		
	}

	public void changed(PropertySource source, PropertyDescriptor<?> desc,
			Object newValue) {
		// TODO Auto-generated method stub
		
	}

}
