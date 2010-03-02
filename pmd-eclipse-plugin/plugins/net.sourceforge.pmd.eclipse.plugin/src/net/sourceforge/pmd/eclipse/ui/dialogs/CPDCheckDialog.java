/*
 * Created on 16.10.2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.dialogs;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 *
 * @author Sven, Brian Remedios
 */

public class CPDCheckDialog extends Dialog {
	
    private final String[] languages;
    private final String[] formats;
    private int selectedFormat;
    private String selectedLanguage;
    private boolean createReport;

    private Group reportGroup = null;
    private Button createReportCheckbox = null;
    private Combo languageCombo = null;
    private Spinner minTileSizeSpinner = null;
    private Combo formatCombo = null;
    private int tileSize = defaultMinTileSize();
    
    public CPDCheckDialog(Shell parentShell, String[] languages, String[] formats) {
        super(parentShell);
        this.languages = languages;
        this.formats = formats;
    }

    public boolean close() {
        this.selectedLanguage = languageCombo.getText();
        return super.close();
    }

    /*
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);
        initialize(container);
        return container;
    }

    /*
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_TITLE));
    }

    /**
     * Gets the selected language.
     * @return language as String
     */
    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    private int defaultMinTileSize() {
    	return PMDPlugin.getDefault().loadPreferences().getMinTileSize();
    }

    /**
     * Gets the selected format
     * @return format as string
     */
    public String getSelectedFormat() {
        return formats[selectedFormat];
    }

    public boolean isCreateReportSelected() {
        return createReport;
    }

    public int getTileSize() {
    	return tileSize;
    }
    
    /**
     * Initializes the container.
     * @param container
     */
    private void initialize(Composite container) {
        final GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.END;
        gridData7.horizontalIndent = 40;
        gridData7.verticalAlignment = GridData.CENTER;
        final GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.END;
        gridData6.verticalAlignment = GridData.CENTER;

        final GridData gridData4 = new GridData();
        gridData4.verticalIndent = 5;
        gridData4.horizontalIndent = 5;
        gridData4.horizontalSpan = 2;

        final GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 2;
        gridLayout1.makeColumnsEqualWidth = false;
        container.setLayout(gridLayout1);

        final Label helpLabel = new Label(container,SWT.NONE);
        helpLabel.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_HELP_LABEL));
        helpLabel.setLayoutData(gridData4);

        final Label languageLabel = new Label(container, SWT.NONE);
        languageLabel.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_LANGUAGE_LABEL));
        languageLabel.setLayoutData(gridData6);

        createLanguageCombo(container);

        final Label minimumTileSizeLabel = new Label(container, SWT.NONE);
        minimumTileSizeLabel.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_MIN_TILESIZE_LABEL));
        minimumTileSizeLabel.setLayoutData(gridData7);
        
        createTileSizeSpinner(container);
       
        createReportGroup(container);
    }
    
    private void createTileSizeSpinner(Composite container) {
    	
        final GridData gridData5 = new GridData();
        gridData5.horizontalAlignment = GridData.FILL;
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.horizontalIndent = 10;
        gridData5.heightHint = -1;
        gridData5.verticalAlignment = GridData.CENTER;
        
        minTileSizeSpinner = new Spinner(container, SWT.BORDER);
        minTileSizeSpinner.setLayoutData(gridData5);
        minTileSizeSpinner.setToolTipText(getString(StringKeys.MSGKEY_DIALOG_TOOLTIP_CPD_MIN_TILESIZE));   
        minTileSizeSpinner.setMinimum(tileSize);
        minTileSizeSpinner.setTextLimit(3);

        minTileSizeSpinner.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                tileSize = Integer.parseInt( minTileSizeSpinner.getText() );
            }
        });
    }
    
    /**
     * This method initializes reportGroup
     * @param container
     *
     */
    private void createReportGroup(Composite container) {
    	
        final GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.BEGINNING;
        gridData7.horizontalIndent = 30;
        gridData7.verticalAlignment = GridData.CENTER;
        final GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.BEGINNING;
        gridData2.horizontalSpan = 2;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.verticalAlignment = GridData.CENTER;
        final GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.FILL;
        gridData11.verticalAlignment = GridData.CENTER;
        gridData11.heightHint = -1;
        gridData11.grabExcessHorizontalSpace = true;
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.verticalIndent = 10;
        gridData.horizontalIndent = 10;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        reportGroup = new Group(container, SWT.NONE);
        reportGroup.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_REPORT));
        reportGroup.setLayoutData(gridData);
        reportGroup.setLayout(gridLayout);
        createReportCheckbox = new Button(reportGroup, SWT.CHECK);
        createReportCheckbox.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_CREATEREPORT));
        createReportCheckbox.setLayoutData(gridData2);
        createReportCheckbox.setSelection(true);
        this.createReport = true;
        createReportCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                formatCombo.setEnabled(createReportCheckbox.getSelection());
                createReport = createReportCheckbox.getSelection();
            }
            public void widgetSelected(SelectionEvent e) {
                formatCombo.setEnabled(createReportCheckbox.getSelection());
                createReport = createReportCheckbox.getSelection();
            }
        });
        final Label formatLabel = new Label(reportGroup, SWT.NONE);
        formatLabel.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_FORMAT_LABEL));
        formatLabel.setLayoutData(gridData7);
        createFormatCombo();
    }

    /**
     * This method initializes languageCombo
     * @param container
     *
     */
    private void createLanguageCombo(Composite container) {
        final GridData gridData4 = new GridData();
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.verticalAlignment = GridData.CENTER;
        gridData4.verticalIndent = 10;
        gridData4.horizontalIndent = 10;
        gridData4.horizontalAlignment = GridData.FILL;
        languageCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        languageCombo.setLayoutData(gridData4);
        languageCombo.setItems(languages);
        languageCombo.select(0);
        languageCombo.setToolTipText(getString(StringKeys.MSGKEY_DIALOG_TOOLTIP_CPD_LANGUAGE));
    }

    /**
     * This method initializes formatCombo
     *
     */
    private void createFormatCombo() {
        final GridData gridData3 = new GridData();
        gridData3.grabExcessHorizontalSpace = false;
        gridData3.widthHint = 150;
        gridData3.horizontalAlignment = GridData.BEGINNING;
        gridData3.horizontalIndent = 10;
        gridData3.verticalAlignment = GridData.CENTER;
        formatCombo = new Combo(reportGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        formatCombo.setLayoutData(gridData3);
        formatCombo.setItems(formats);
        formatCombo.setToolTipText(getString(StringKeys.MSGKEY_DIALOG_TOOLTIP_CPD_FORMAT));
        formatCombo.select(0);
        formatCombo.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                selectedFormat = formatCombo.getSelectionIndex();
            }
            public void widgetSelected(SelectionEvent e) {
                selectedFormat = formatCombo.getSelectionIndex();
            }
        });
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getString(String key) {
        return SWTUtil.stringFor(key);
    }
}
