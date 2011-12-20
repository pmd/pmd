package net.sourceforge.pmd.eclipse.plugin;

import java.util.HashSet;
import java.util.Set;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewCodeCmd;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Monitors for changes in the workspace and initiates the ReviewCodeCmd
 * when suitable file changes in some meaningful way.
 * 
 * @author Brian Remedios
 */
public class FileChangeReviewer implements IResourceChangeListener {

	public FileChangeReviewer() {
	}

	private enum ChangeType {
		ADDED,
		REMOVED,
		CHANGED
	}
	
	private class ResourceChange {
		public final ChangeType resourceDeltaType;
		public final int flags;
		public final IFile file;
		
		private ResourceChange(ChangeType type, IFile theFile, int theFlags) {
			resourceDeltaType = type;
			file = theFile;
			flags = theFlags;
		}
		
		public int hashCode() { return resourceDeltaType.hashCode() + 13 + file.hashCode() + flags; }
		
		public boolean equals(Object other) {
			if (other == this) return true;
			if (other.getClass() == getClass()) {
				ResourceChange chg = (ResourceChange)other;
				return chg.file.equals(file) && 
					resourceDeltaType == chg.resourceDeltaType &&
					flags == chg.flags;
			}
			return false;
		}
	}
	
	/**
	 * @param event
	 */
	public void resourceChanged(IResourceChangeEvent event) {

		Set<ResourceChange> itemsChanged = new HashSet<ResourceChange>();
		
		switch (event.getType()) {
//		case IResourceChangeEvent.PRE_DELETE: 
//		case IResourceChangeEvent.PRE_CLOSE:
//		case IResourceChangeEvent.PRE_BUILD: 
//		case IResourceChangeEvent.POST_BUILD:
//		case IResourceChangeEvent.PRE_REFRESH: 
			
		case IResourceChangeEvent.POST_CHANGE:			
			changed(itemsChanged, event.getDelta(), EclipseUtil.DUMMY_MONITOR);
		}
		
		if (itemsChanged.isEmpty()) return;
		
		ReviewCodeCmd cmd = new ReviewCodeCmd();	// separate one for each thread
		cmd.reset();
		
		for (ResourceChange chg : itemsChanged) cmd.addResource(chg.file);
		
		try {
			cmd.performExecute();
		} catch (CommandException e) {
			 PMDPlugin.getDefault().log(IStatus.ERROR, "Error processing code review upon file changes", e);
		}
	}

	private void changed(Set<ResourceChange> itemsChanged, IResourceDelta delta, IProgressMonitor monitor) {

		IResource rsc = delta.getResource();
		int flags = delta.getFlags();

		switch (delta.getKind()) {
			case IResourceDelta.NO_CHANGE :	return;
			case IResourceDelta.REMOVED :
//				if (rsc instanceof IProject) {
//					removed(itemsChanged, (IProject)rsc, delta.getFlags());
//				}
//				if (rsc instanceof IFile) {
//					removed(itemsChanged, (IFile)rsc, flags, true);
//				}
				for (IResourceDelta grandkidDelta : delta.getAffectedChildren()) {
					if (monitor.isCanceled()) return;
					changed(itemsChanged, grandkidDelta, monitor);
				}
			case IResourceDelta.ADDED :
//				if (rsc instanceof IProject) {
//					removed(itemsChanged, (IProject)rsc, delta.getFlags());
//				}
				if (rsc instanceof IFile) {
					added(itemsChanged, (IFile)rsc, flags, true);
				}
				for (IResourceDelta grandkidDelta : delta.getAffectedChildren()) {
					if (monitor.isCanceled()) return;
					changed(itemsChanged, grandkidDelta, monitor);
				}		
			case IResourceDelta.CHANGED :
//				if (rsc instanceof IProject) {
//					changed(itemsChanged, (IProject)rsc, delta.getFlags());
//				}
				if (rsc instanceof IFile) {
					changed(itemsChanged, (IFile)rsc, flags, true);
				}
				for (IResourceDelta grandkidDelta : delta.getAffectedChildren()) {
					if (monitor.isCanceled()) return;
					changed(itemsChanged, grandkidDelta, monitor);
				}			
			default :
				for (IResourceDelta grandkidDelta : delta.getAffectedChildren()) {
					if (monitor.isCanceled()) return;
					changed(itemsChanged, grandkidDelta, monitor);
				}
			}
	}

	private void changed(Set<ResourceChange> itemsChanged, IFile rsc, int flags, boolean b) {
		
		if ((flags & IResourceDelta.CONTENT) > 0) {
			itemsChanged.add( new ResourceChange(ChangeType.CHANGED, rsc, flags));
			}
	}
	
	private void added(Set<ResourceChange> itemsChanged, IFile rsc, int flags,	boolean b) {
		itemsChanged.add( new ResourceChange(ChangeType.ADDED, rsc, flags));
//		System.out.println("added: " + rsc);
	}
//	private void changed(Set<ResourceChange> itemsChanged, IProject rsc, int flags) {
//		System.out.println("changed: " + rsc);
//	}
//
//
//	private void removed(Set<ResourceChange> itemsChanged, IFile rsc, int flags, boolean b) {
//		itemsChanged.add( new ResourceChange(ChangeType.REMOVED, rsc, flags));
//		System.out.println("removed: " + rsc);
//	}
//
//	private void removed(Set<ResourceChange> itemsChanged, IProject rsc, int flags) {
//		System.out.println("removed: " + rsc);
//	}
	

}
