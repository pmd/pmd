package net.sourceforge.pmd.eclipse.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements the resource focus change. 1st track the part that is activated
 * then track the selection changes in that part
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class PartListener implements IPartListener, ISelectionChangedListener {
    private ViolationView violationView;
    private IWorkbenchPart focusPart;

    /**
     * Constructor
     */
    public PartListener(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.ui.IPartListener#partActivated(IWorkbenchPart)
     */
    public void partActivated(IWorkbenchPart part) {
        if (part != focusPart) {
            ISelectionProvider selectionProvider = null;
            if (focusPart != null) {
                selectionProvider = focusPart.getSite().getSelectionProvider();
                if (selectionProvider != null) {
                    selectionProvider.removeSelectionChangedListener(this);
                }
            }

            focusPart = part;

            selectionProvider = part.getSite().getSelectionProvider();
            if (selectionProvider != null) {
                selectionProvider.addSelectionChangedListener(this);
                violationView.setFocusResource(getFocusResource(selectionProvider.getSelection()));
            } else {
                violationView.setFocusResource(null);
            }
        }

    }

    /**
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partClosed(IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        if (focusPart == part) {
            ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();
            if (selectionProvider != null) {
                selectionProvider.removeSelectionChangedListener(this);
            }
            focusPart = null;
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partDeactivated(IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part) {
        if (focusPart == part) {
            ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();
            if (selectionProvider != null) {
                selectionProvider.removeSelectionChangedListener(this);
            }
            focusPart = null;
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partOpened(IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part) {
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        violationView.setFocusResource(getFocusResource(event.getSelection()));
    }

    /**
     * Get the resource affected by a part focus
     */
    private IResource getFocusResource(ISelection selection) {
        IResource resource = null;

        if ((selection != null) && (selection instanceof IStructuredSelection)) {
            resource = getResourceFromSelection((IStructuredSelection) selection);
            if (resource == null) {
                resource = getEditedResource();
            }
        }

        return resource;
    }

    /**
     * Return the corresponding resource from a part selection
     */
    private IResource getResourceFromSelection(IStructuredSelection structuredSelection) {
        IResource resource = null;
        Object selectedObject = structuredSelection.getFirstElement();
        if (selectedObject != null) {
            if (selectedObject instanceof IResource) {
                resource = (IResource) selectedObject;
            } else if (selectedObject instanceof IAdaptable) {
                resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
            }
        }

        return resource;
    }

    /**
     * Return the resource currently edited
     */
    private IResource getEditedResource() {
        IResource resource = null;

        if (focusPart instanceof IEditorPart) {
            IEditorInput input = ((IEditorPart) focusPart).getEditorInput();
            if (input != null) {
                if (input instanceof IFileEditorInput) {
                    resource = ((IFileEditorInput) input).getFile();
                } else {
                    resource = (IResource) input.getAdapter(IResource.class);
                    if (resource == null) {
                        resource = (IFile) input.getAdapter(IFile.class);
                    }
                }
            }
        }

        return resource;
    }

}
