package net.sourceforge.pmd.eclipse.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import net.sourceforge.pmd.cpd.*;
import net.sourceforge.pmd.eclipse.CPDReportWindow;
import net.sourceforge.pmd.eclipse.CPDVisitor;
import net.sourceforge.pmd.eclipse.PMDPlugin;

/**
 * @author David Craine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CPDCheckProjectAction
	implements IObjectActionDelegate, IRunnableWithProgress {
	IWorkbenchPart targetPart;

	/**
	 * Constructor for CPDCheckProjectAction.
	 */
	public CPDCheckProjectAction() {
		super();
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
		try {
			CPD cpd = new CPD();
			//cpd.setListener(new CPDActivityListener(monitor));  //for some reason CPD dies when using this listener
			PMDPlugin.getDefault().getPreferenceStore().setDefault(
				PMDPlugin.MIN_TILE_SIZE_PREFERENCE,
				25);
			cpd.setMinimumTileSize(
				PMDPlugin.getDefault().getPreferenceStore().getInt(
					PMDPlugin.MIN_TILE_SIZE_PREFERENCE));
			CPDVisitor visitor = new CPDVisitor(cpd);
			Object sel = targetPart.getSite().getSelectionProvider();
			CPDReportWindow crw =
				new CPDReportWindow(targetPart.getSite().getShell());
			crw.create();

			if (sel instanceof TreeViewer) {
				TreeViewer tv = (TreeViewer) sel;
				ISelection sel2 = tv.getSelection();
				if (sel2 instanceof StructuredSelection) {
					monitor.beginTask("Searhing for files...", IProgressMonitor.UNKNOWN);
					StructuredSelection ss = (StructuredSelection) sel2;
					for (Iterator iter = ss.iterator(); iter.hasNext();) {
						Object obj = iter.next();
						if (obj instanceof IProject) {
							((IProject) obj).accept(visitor);
						}
					}
					monitor.beginTask("Running Cut & Paste Detector...", IProgressMonitor.UNKNOWN);
					cpd.go();
					monitor.beginTask("Building result set...", IProgressMonitor.UNKNOWN);
					Results results = cpd.getResults();
					for (Iterator iter = results.getTiles(); iter.hasNext();) {
						Tile tile = (Tile) iter.next();
						int dups = results.getOccurrenceCountFor(tile);
						crw.addEntry(String.valueOf(dups) + " duplicates found.\n");
						for (Iterator iter2 = results.getOccurrences(tile);
							iter2.hasNext();
							) {
							TokenEntry te = (TokenEntry) iter2.next();
							crw.addEntry("\t"+te.getTokenSrcID()+": "+te.getBeginLine()+"\n");
						}
						crw.addEntry("\n");
					}
					monitor.done();
					crw.open();
					crw.getShell().setSize(500, 500);
				}
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Error running CPD", e.toString());
			monitor.done();
		}

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 * Find all the selected projects and add their respsective java files into the 	 
	 * CPD utility via the CPDVisitor.  Then run the CPD utility and display
	 * the results.
	 */
	public void run(IAction action) {
		try {
			new ProgressMonitorDialog(targetPart.getSite().getShell()).run(
				false,
				false,
				this);
		} catch (Exception e) {
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	class CPDActivityListener implements CPDListener {
		IProgressMonitor monitor;
		private boolean firstFile = true;
		private boolean firstToken = true;
		private boolean firstTile = true;
		private int tokenTracker = 0;
		private static final int TOKEN_LIMIT = 100;

		public CPDActivityListener(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		/**
		 * @see net.sourceforge.pmd.cpd.CPDListener#addedFile(int, File)
		 */
		public void addedFile(int fileCount, File file) {
			if (firstFile) {
				monitor.beginTask("Adding Files", fileCount);
				firstFile = false;
			}
			if (file != null)
				monitor.subTask(file.getName());
			monitor.worked(1); 
		}

		/**
		 * @see net.sourceforge.pmd.cpd.CPDListener#addingTokens(int, int, String)
		 */
		public void addingTokens(int tokenSetCount, int doneSoFar,String tokenSrcId) {
			if (firstToken) {
				monitor.beginTask("Adding Tokens", tokenSetCount);
				firstToken = false;
			}
			if (tokenSrcId != null && tokenTracker++ == TOKEN_LIMIT) {
				monitor.subTask(tokenSrcId);
				tokenTracker = 0;
				monitor.worked(TOKEN_LIMIT);
			}

		}

		/**
		 * @see net.sourceforge.pmd.cpd.CPDListener#update(String)
		 */
		public void update(String arg0) {

		}

		/**
		 * @see net.sourceforge.pmd.cpd.CPDListener#addedNewTile(Tile, int, int)
		 */
		public void addedNewTile(Tile tile, int tilesSoFar, int totalTiles) {
			if (firstTile) {
				monitor.beginTask("Adding Tiles", totalTiles);
				firstTile = false;
			}
			if (tile != null)
				monitor.subTask(tile.getImage());
			monitor.worked(1);
		}

	}
}
