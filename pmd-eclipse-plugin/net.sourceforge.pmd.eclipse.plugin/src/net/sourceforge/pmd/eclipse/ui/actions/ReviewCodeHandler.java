package net.sourceforge.pmd.eclipse.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewCodeCmd;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * 
 * @author Brian Remedios
 */
public class ReviewCodeHandler extends AbstractHandler {

	private IWorkbenchWindow fWindow;
	private IResource[] fResources;
	private IPath fLocation;
		
	public ReviewCodeHandler() {		
	}

	protected final ISelection getSelection() {
		IWorkbenchWindow window= getWorkbenchWindow();
		return window != null ?
			window.getSelectionService().getSelection() :
			null;
	}
	
	protected final IWorkbenchWindow getWorkbenchWindow() {
		if (fWindow == null) {
			fWindow= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			}
		return fWindow;
	}
	

//	/**
//	 * Collects the files out of the given resources.
//	 *
//	 * @param resources the resources from which to get the files
//	 * @return an array of files
//	 */
//	protected static IFile[] collectFiles(IResource[] resources) {
//		
//		Set<IFile> files= new HashSet<IFile>();
//		for (int i= 0; i < resources.length; i++) {
//			IResource resource= resources[i];
//			if ((IResource.FILE & resource.getType()) > 0)
//				files.add((IFile)resource);
//		}
//		return (IFile[]) files.toArray(new IFile[files.size()]);
//	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		computeSelectedResources();
		
		try {
			if (fResources != null && fResources.length > 0) {
								
				ReviewCodeCmd cmd = new ReviewCodeCmd();	// separate one for each thread
				cmd.reset();
				
				for (IResource rsc : fResources) cmd.addResource(rsc);
				
				try {
					cmd.performExecute();
				} catch (CommandException e) {
					 PMDPlugin.getDefault().log(IStatus.ERROR, "Error processing user-initiated code review", e);
				}
			}			
			return null;		// Standard return value. DO NOT CHANGE.

		} finally {
			fResources = null;
			fLocation = null;
		}
	}
	
	/**
	 * Computes the selected resources.
	 */
	protected final void computeSelectedResources() {

		if (fResources != null || fLocation != null)
			return;

		ISelection selection= getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection= (IStructuredSelection) selection;
			List resources = new ArrayList(structuredSelection.size());

			Iterator<?> e= structuredSelection.iterator();
			while (e.hasNext()) {
				Object element= e.next();
				if (element instanceof IResource)
					resources.add(element);
				else if (element instanceof IAdaptable) {
					IAdaptable adaptable= (IAdaptable) element;
					Object adapter= adaptable.getAdapter(IResource.class);
					if (adapter instanceof IResource)
						resources.add(adapter);
				}
			}

			if (!resources.isEmpty())
				fResources= (IResource[]) resources.toArray(new IResource[resources.size()]);

		} else if (selection instanceof ITextSelection) {
			IWorkbenchWindow window= getWorkbenchWindow();
			if (window != null) {
				IWorkbenchPart workbenchPart= window.getPartService().getActivePart();
				if (workbenchPart instanceof IEditorPart) {
					IEditorPart editorPart= (IEditorPart) workbenchPart;
					IEditorInput input= editorPart.getEditorInput();
					Object adapter= input.getAdapter(IResource.class);
					if (adapter instanceof IResource)
						fResources= new IResource[] { (IResource) adapter };
					else {
						adapter= input.getAdapter(ILocationProvider.class);
						if (adapter instanceof ILocationProvider) {
							ILocationProvider provider= (ILocationProvider) adapter;
							fLocation= provider.getPath(input);
						}
					}
				}
			}
		}
	}

}
