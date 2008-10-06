package net.sourceforge.pmd.eclipse.ui.preferences;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Base class for IStructuredContentProviders.
 * 
 * @see IStructuredContentProvider
 *
 */
public abstract class AbstractStructuredContentProvider implements IStructuredContentProvider {

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
