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
 * @author David Craine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
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
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		entryTable.setLayout(layout);
		
		//create text area label
		label = new Label(entryTable, SWT.NULL);
		label.setText("Duplicate Code View");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		//create text area		
		text = new Text(entryTable, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
	
		text.setEditable(false);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		data.verticalSpan = 10;
		text.setLayoutData(data);

	}
	
	public void addEntry(String item) {
		text.append(item);
	}
}
