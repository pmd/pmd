package net.sourceforge.pmd.eclipse.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
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
public class ProgressDialog extends ApplicationWindow {
	private ProgressIndicator progressIndicator;
	private Label label;
	private Composite entryTable;
	public static final int UNKNOWN = -1;
	private int max=UNKNOWN;
	private String title;
	
	
	/**
	 * Constructor for ProgressDialog.
	 * @param parentShell
	 */
	public ProgressDialog(Shell parentShell, String title, int max) {
		super(parentShell);
		this.title = title;
		
	}
		/**
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(title);
		entryTable = new Composite(newShell, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		entryTable.setLayout(layout);

		//create the label
		label = new Label(entryTable, SWT.NULL);
		label.setText("");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		label.setLayoutData(data);
		
		progressIndicator = new ProgressIndicator(entryTable);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 10;
		progressIndicator.setLayoutData(data);	
		if (max==UNKNOWN)
			progressIndicator.beginAnimatedTask();
		else
			progressIndicator.beginTask(max);
		

	}
	
	public void show() {
		this.create();
		this.getShell().setSize(300,200);
		this.open();
	}
	

	public void worked(double work) {
		progressIndicator.worked(work);
	}

	public void setMessage(String msg) {
		label.setText(msg);
	}
}
