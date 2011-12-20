package net.sourceforge.pmd.eclipse.plugin;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 
 * @author Brian Remedios
 */
public class EclipseUtil {

	public static final IProgressMonitor DUMMY_MONITOR = new IProgressMonitor() {

		public void beginTask(String name, int totalWork) {	}
		public void done() {	}
		public void internalWorked(double work) {	}
		public boolean isCanceled() {	return false;	}
		public void setCanceled(boolean value) {	}
		public void setTaskName(String name) {	}
		public void subTask(String name) {	}
		public void worked(int work) {	}		
	};
}
