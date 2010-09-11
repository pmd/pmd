package net.sourceforge.pmd.eclipse.ui.preferences;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.RuleLabelDecorator;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapePicker;
import net.sourceforge.pmd.eclipse.ui.model.RootRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptor;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptorCache;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityTableLabelProvider;
import net.sourceforge.pmd.util.StringUtil;

import org.apache.log4j.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The top-level page for PMD preferences
 *
 * @see CPDPreferencePage
 * @see PMDPreferencePage
 *
 * @author Philippe Herlin
 * @author Brian Remedios
 */
public class GeneralPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
	
    private static final String[] LOG_LEVELS = { "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "ALL" };
    private static final RGB SHAPE_COLOR = new RGB(255,255,255);
    
    private Text		additionalCommentText;
    private Label		sampleLabel;
    private Button		showPerspectiveBox;
    private Button		useProjectBuildPath;
    private Button		checkCodeOnSave;
    private Spinner		maxViolationsPerFilePerRule;
    private Button		reviewPmdStyleBox;
    private Text		logFileNameText;
    private Scale		logLevelScale;
    private Label		logLevelValueLabel;
    private Button		browseButton;
    private TableViewer tableViewer;
    private IPreferences preferences;
    
    /**
     * Initialize the page
     *
     * @see PreferencePage#init
     */
    public void init(IWorkbench arg0) {
   //     setDescription(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TITLE));
        preferences = PMDPlugin.getDefault().loadPreferences();
    }

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
        Group generalGroup = buildGeneralGroup(composite);
        Group priorityGroup = buildPriorityGroup(composite);
        Group reviewGroup = buildReviewGroup(composite);
        Group logGroup = buildLoggingGroup(composite);

        // Layout children
        generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        priorityGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        logGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        reviewGroup.setLayoutData(data);

        return composite;
    }

    /**
     * Build the group of general preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildGeneralGroup(final Composite parent) {

        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_GENERAL));
        group.setLayout(new GridLayout(1, false));

        // build the children
        showPerspectiveBox = buildShowPerspectiveBoxButton(group);
        useProjectBuildPath = buildUseProjectBuildPathButton(group);
        checkCodeOnSave = buildCheckCodeOnSaveButton(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        maxViolationsPerFilePerRule = buildMaxViolationsPerFilePerRuleText(group);

        // layout children
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        showPerspectiveBox.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        useProjectBuildPath.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        separator.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        maxViolationsPerFilePerRule.setLayoutData(data);

        return group;
    }
  
    private Link createPreferenceLink(Composite parent, String label, final String prefPageId) {
    	
    	 Link link = new Link(parent, SWT.None);
         link.setText(label);
         link.addSelectionListener (new SelectionAdapter () {
 			public void widgetSelected(SelectionEvent se) {
 				PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
 						getShell(), prefPageId,
 						new String[] {}, null
 						);
 				if (pref != null) {
 					pref.open();
 				}
 			}
 		});
         
         return link;
    }
    
	/**
     * Build the group of priority preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildPriorityGroup(final Composite parent) {

        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_PRIORITIES));
        group.setLayout(new GridLayout(1, false));

        createPreferenceLink(group, 
        		"PMD folder annotations can be enabled on the <A>label decorations</A> page", 
        		"org.eclipse.ui.preferencePages.Decorators"
        		);
        
        IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			public void dispose() {	}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
			public Object[] getElements(Object inputElement) { return (RulePriority[])inputElement;	}        	
        };
        PriorityTableLabelProvider labelProvider = new PriorityTableLabelProvider(PriorityColumnDescriptor.VisibleColumns);
        
        tableViewer = new TableViewer(group, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData( new GridData(GridData.BEGINNING, GridData.CENTER, true, true) );
        
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(contentProvider);
        table.setHeaderVisible(true);
        labelProvider.addColumnsTo(table);
        tableViewer.setInput( UISettings.currentPriorities(true) );
        
        TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) column.pack();
		
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        table.setLayoutData(data);
        
        Composite editorPanel = new Composite(group, SWT.None);
        editorPanel.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true) );
        editorPanel.setLayout(new GridLayout(6, false));
        
        Label shapeLabel = new Label(editorPanel, SWT.None);
        shapeLabel.setLayoutData( new GridData());
        shapeLabel.setText("Shape:");
        
        final ShapePicker<Shape> ssc = new ShapePicker<Shape>(editorPanel, SWT.None, 14);
        ssc.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        ssc.setSize(280, 30);
		ssc.setShapeMap(UISettings.shapeSet(SHAPE_COLOR, 10));
		ssc.setItems( UISettings.allShapes() );
		
        Label colourLabel = new Label(editorPanel, SWT.None);
        colourLabel.setLayoutData( new GridData());
        colourLabel.setText("Color:");
        
        final ColorSelector colorPicker = new ColorSelector(editorPanel);
        
        Label nameLabel = new Label(editorPanel, SWT.None);
        nameLabel.setLayoutData( new GridData());
        nameLabel.setText("Name:");
        
        final Text priorityName = new Text(editorPanel, SWT.BORDER);
        priorityName.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true) );

//        final Label descLabel = new Label(editorPanel, SWT.None);
//        descLabel.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, false, true, 1, 1));
//        descLabel.setText("Description:");
        
//        final Text priorityDesc = new Text(editorPanel, SWT.BORDER);
//        priorityDesc.setLayoutData( new GridData(GridData.FILL, GridData.CENTER, true, true, 5, 1) );
        
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedPriorities(selection.toList(), ssc, colorPicker, priorityName);
			}} );
        
		ssc.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				setShape((Shape)selection.getFirstElement());
			}} );
		
		colorPicker.addListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				setColor((RGB)event.getNewValue());
			}} );
		
		priorityName.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				setName( priorityName.getText() );	
			}} );
		
        return group;
    }
    
    private void setShape(Shape shape) {
    	
    	if (shape == null) return;	// renderers can't handle this
    	
    	for (PriorityDescriptor pd : selectedDescriptors()) {
    		pd.shape.shape = shape;
    	}
    	tableViewer.refresh();
    }
    
    private void setColor(RGB clr) {
    	for (PriorityDescriptor pd : selectedDescriptors()) {
    		pd.shape.rgbColor = clr;
    	}
    	tableViewer.refresh();
    }
    
    private void setName(String newName) {
    	
    	if (StringUtil.isEmpty(newName)) return;
    	
    	for (PriorityDescriptor pd : selectedDescriptors()) {
    		pd.label = newName;
    	}
    	tableViewer.refresh();
    }
    
    private PriorityDescriptor[] selectedDescriptors() {
    	
    	Object[] items = ((IStructuredSelection)tableViewer.getSelection()).toArray();
    	PriorityDescriptor[] descs = new PriorityDescriptor[items.length];
    	for (int i=0; i<descs.length; i++) descs[i] = PriorityDescriptorCache.instance.descriptorFor((RulePriority)items[i]);
    	return descs;
    }
    
    private static void selectedPriorities(List<RulePriority> items, ShapePicker<Shape> ssc, ColorSelector colorPicker, Text nameField) {

    	if (items.size() != 1 ) {
    		ssc.setSelection((Shape)null);
    		nameField.setText("");
    		return;
    	}
    	
    	RulePriority priority = items.get(0);
    	PriorityDescriptor desc = PriorityDescriptorCache.instance.descriptorFor(priority);
    	
    	ssc.setSelection( desc.shape.shape );
    	nameField.setText( desc.label);
    	colorPicker.setColorValue( desc.shape.rgbColor );
    }
    
    /**
     * Build the group of review preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildReviewGroup(final Composite parent) {

        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_REVIEW));
        group.setLayout(new GridLayout(1, false));

        // build children
        this.reviewPmdStyleBox = buildReviewPmdStyleBoxButton(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        buildLabel(group, StringKeys.MSGKEY_PREF_GENERAL_LABEL_ADDCOMMENT);
        additionalCommentText = buildAdditionalCommentText(group);
        buildLabel(group, StringKeys.MSGKEY_PREF_GENERAL_LABEL_SAMPLE);
        sampleLabel = buildSampleLabel(group);
        updateSampleLabel();

        // layout children
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        reviewPmdStyleBox.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        separator.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        additionalCommentText.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        sampleLabel.setLayoutData(data);

        return group;
    }

    /**
     * Build the log group.
     * Note that code is a cut & paste from the Eclipse Visual Editor
     *
     */
    private Group buildLoggingGroup(Composite parent) {
        GridData gridData2 = new GridData();
        gridData2.horizontalSpan = 2;
        gridData2.horizontalAlignment = SWT.FILL;
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData11.horizontalSpan = 3;
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.horizontalSpan = 3;
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        Group loggingGroup = new Group(parent, SWT.NONE);
        loggingGroup.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_LOGGING));
        loggingGroup.setLayout(gridLayout);

        Label logFileNameLabel = new Label(loggingGroup, SWT.NONE);
        logFileNameLabel.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_LOG_FILE_NAME));
        logFileNameLabel.setLayoutData(gridData);

        logFileNameText = new Text(loggingGroup, SWT.BORDER);
        logFileNameText.setText(this.preferences.getLogFileName());
        logFileNameText.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_LOG_FILE_NAME));
        logFileNameText.setLayoutData(gridData1);

        browseButton = new Button(loggingGroup, SWT.NONE);
        browseButton.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_BUTTON_BROWSE));
        browseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                browseLogFile();
            }
            public void widgetDefaultSelected(SelectionEvent event) {
                // do nothing
            }
        });

        Label separator = new Label(loggingGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(gridData11);

        Label logLevelLabel = new Label(loggingGroup, SWT.NONE);
        logLevelLabel.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_LOG_LEVEL));

        logLevelValueLabel = new Label(loggingGroup, SWT.NONE);
        logLevelValueLabel.setText("");
        logLevelValueLabel.setLayoutData(gridData2);

        logLevelScale = new Scale(loggingGroup, SWT.NONE);
        logLevelScale.setMaximum(6);
        logLevelScale.setPageIncrement(1);
        logLevelScale.setLayoutData(gridData3);
        logLevelScale.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                updateLogLevelValueLabel();
            }
            public void widgetDefaultSelected(SelectionEvent event) {
                updateLogLevelValueLabel();
            }
        });

        logLevelScale.setSelection(intLogLevel(this.preferences.getLogLevel()));
        updateLogLevelValueLabel();

        return loggingGroup;
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
     * Build the sample
     */
    private Label buildSampleLabel(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        return label;
    }

    /**
     * Build the text for additional comment input
     *
     * @param parent
     * @return
     */
    private Text buildAdditionalCommentText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText(this.preferences.getReviewAdditionalComment());
        text.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_ADDCOMMENT));

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateSampleLabel();
            }
        });

        return text;
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
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_SHOW_PERSPECTIVE));
        button.setSelection(preferences.isPmdPerspectiveEnabled());

        return button;
    }

    /**
     * Build the check box for enabling using Project Build Path
     * @param viewGroup the parent composite
     */
    private Button buildUseProjectBuildPathButton(Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_USE_PROJECT_BUILD_PATH));
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
    	
        Label label = buildLabel(comp, StringKeys.MSGKEY_PREF_GENERAL_LABEL_MAX_VIOLATIONS_PFPR);
        label.setLayoutData( new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));
        
        final Spinner spinner = new Spinner(comp, SWT.SINGLE | SWT.BORDER);
        spinner.setLayoutData( new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_CENTER, true, false, 1, 1));
        spinner.setMinimum(preferences.getMaxViolationsPerFilePerRule());
        spinner.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_MAX_VIOLATIONS_PFPR));
        return spinner;
    }

    /**
     * Build the check box for enabling PMD review style
     * @param viewGroup the parent composite
     *
     */
    private Button buildReviewPmdStyleBoxButton(final Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_REVIEW_PMD_STYLE));
        button.setSelection(this.preferences.isReviewPmdStyleEnabled());

        return button;
    }


    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
    	
        if (additionalCommentText != null) {
            additionalCommentText.setText(IPreferences.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        }

        if (showPerspectiveBox != null) {
            showPerspectiveBox.setSelection(IPreferences.PMD_PERSPECTIVE_ENABLED_DEFAULT);
        }

        if (checkCodeOnSave != null) {
        	checkCodeOnSave.setSelection(IPreferences.PMD_CHECK_AFTER_SAVE_DEFAULT);
        }
        
        if (useProjectBuildPath != null) {
            useProjectBuildPath.setSelection(IPreferences.PROJECT_BUILD_PATH_ENABLED_DEFAULT);
        }

        if (maxViolationsPerFilePerRule != null) {
            maxViolationsPerFilePerRule.setMinimum(IPreferences.MAX_VIOLATIONS_PFPR_DEFAULT);
        }

        if (reviewPmdStyleBox !=null) {
            reviewPmdStyleBox.setSelection(IPreferences.REVIEW_PMD_STYLE_ENABLED_DEFAULT);
        }

        if (logFileNameText != null) {
            logFileNameText.setText(IPreferences.LOG_FILENAME_DEFAULT);
        }

        if (logLevelScale != null) {
            logLevelScale.setSelection(intLogLevel(IPreferences.LOG_LEVEL));
            updateLogLevelValueLabel();
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
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_HEADER), NONE);
            setValid(true);

        } catch (IllegalArgumentException e) {
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_MESSAGE_INCORRECT_FORMAT), ERROR);
            setValid(false);
        }
    }

    /**
     * Update the label of the log level to reflect the log level selected
     *
     */
    protected void updateLogLevelValueLabel() {
        this.logLevelValueLabel.setText(LOG_LEVELS[this.logLevelScale.getSelection()]);
    }

    /**
     * Display a file selection dialog in order to let the user select a log file
     *
     */
    protected void browseLogFile() {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_DIALOG_BROWSE));
        String fileName = dialog.open();
        if (fileName != null) {
            logFileNameText.setText(fileName);
        }
    }

    private void updateMarkerIcons() {
    	
    	if (!PriorityDescriptorCache.instance.hasChanges()) {
    		return;
    	}
    	
    	// TODO show in UI...could take a while to update
    	
    	PriorityDescriptorCache.instance.storeInPreferences();
        UISettings.createRuleMarkerIcons(getShell().getDisplay());
    	UISettings.reloadPriorities();
    	
    	// ensure that the decorator gets these new images...
    	RuleLabelDecorator decorator = PMDPlugin.getDefault().ruleLabelDecorator();
    	if (decorator != null) decorator.reloadDecorators();
    	
    	RootRecord root = new RootRecord(ResourcesPlugin.getWorkspace().getRoot());
    	Set<IFile> files = MarkerUtil.allMarkedFiles(root);
    	PMDPlugin.getDefault().changedFiles(files);
    }
    
    public boolean performCancel() {
    	// clear out any changes for next possible usage
    	PriorityDescriptorCache.instance.loadFromPreferences();
        return true;
    }
    
    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
    	
    	updateMarkerIcons();
    	
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

        if (logFileNameText != null) {
            preferences.setLogFileName(logFileNameText.getText());
        }

        if (logLevelScale != null) {
            preferences.setLogLevel(Level.toLevel(LOG_LEVELS[logLevelScale.getSelection()]));
        }

        preferences.sync();
        PMDPlugin.getDefault().applyLogPreferences(preferences);

        return true;
    }

    /**
     * Return the selection index corresponding to the log level
     */
    private int intLogLevel(Level level) {
        int result = 0;

        if (level.equals(Level.OFF)) {
            result = 0;
        } else if (level.equals(Level.FATAL)) {
            result = 1;
        } else if (level.equals(Level.ERROR)) {
            result = 2;
        } else if (level.equals(Level.WARN)) {
            result = 3;
        } else if (level.equals(Level.INFO)) {
            result = 4;
        } else if (level.equals(Level.DEBUG)) {
            result = 5;
        } else if (level.equals(Level.ALL)) {
            result = 6;
        }

        return result;

    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

}
