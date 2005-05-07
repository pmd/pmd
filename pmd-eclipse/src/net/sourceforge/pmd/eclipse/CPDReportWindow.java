package net.sourceforge.pmd.eclipse;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A window to show the CPD report
 * 
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2005/05/07 13:32:06  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.4  2003/03/18 23:28:36  phherlin
 * *** keyword substitution change ***
 *
 */
public class CPDReportWindow extends ApplicationWindow {
	private Label label;
	private Composite entryTable;
	private Text text;
	/**
	 * Constructor for CPDReportWindow.
	 * @param parentShell
	 */
	public CPDReportWindow(Shell parentShell) {
		super(parentShell);
		init();
	}
	
	private void init() {
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		entryTable = new Composite(newShell, SWT.NULL);
		entryTable.setLayout(new GridLayout());
		
		//create text area label
		label = new Label(entryTable, SWT.NULL);
		label.setText("Duplicate Code View");
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);

		//create text area		
		text = new Text(entryTable, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
	
		text.setEditable(false);
		data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
		text.setLayoutData(data);

	}
	
	public void addEntry(String item) {
		text.append(item);
	}
}
