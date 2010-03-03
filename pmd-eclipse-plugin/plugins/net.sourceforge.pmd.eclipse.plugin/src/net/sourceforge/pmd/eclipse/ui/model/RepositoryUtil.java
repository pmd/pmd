package net.sourceforge.pmd.eclipse.ui.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileRevision;

/**
 * 
 * @author Brian Remedios
 */
public class RepositoryUtil {

	private static Boolean hasRepositoryAccess;
	
	private RepositoryUtil() {	}

	public static boolean hasRepositoryAccess() {
		
		if (hasRepositoryAccess != null) return hasRepositoryAccess;

		try {
			Object cls = Class.forName("org.eclipse.team.core.RepositoryProvider");
			hasRepositoryAccess = Boolean.TRUE;
		} catch (ClassNotFoundException e) {
			hasRepositoryAccess = Boolean.FALSE;
			}
		
		return hasRepositoryAccess;
	}
	
	public static String authorNameFor(IResource resource) {
		
    	IProject project = resource.getProject();
    	String authorName = null;
    	try {
	    	RepositoryProvider provider = RepositoryProvider.getProvider(project);
	    	IFileRevision revision = provider.getFileHistoryProvider().getWorkspaceFileRevision(resource);
	    	authorName = revision.getAuthor();
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	return authorName;
	}
}
