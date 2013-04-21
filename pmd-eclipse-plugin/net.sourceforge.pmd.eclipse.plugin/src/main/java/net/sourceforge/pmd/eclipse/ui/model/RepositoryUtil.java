package net.sourceforge.pmd.eclipse.ui.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistoryProvider;
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
			Class.forName("org.eclipse.team.core.RepositoryProvider");
			hasRepositoryAccess = Boolean.TRUE;
		} catch (ClassNotFoundException e) {
			hasRepositoryAccess = Boolean.FALSE;
			}
		
		return hasRepositoryAccess;
	}
	
	/**
	 * Returns the name of the resource author if the resource was parked in
	 * a repository or null if it wasn't.
	 * 
	 * @param resource
	 * @return String
	 */
	public static String authorNameFor(IResource resource) {
		
    	IProject project = resource.getProject();
    	String authorName = null;
    	try {
	    	RepositoryProvider provider = RepositoryProvider.getProvider(project);
	    	if (provider == null) return null;
	    	
	    	IFileHistoryProvider fhProvider = provider.getFileHistoryProvider();
	    	if (fhProvider == null) return null;
	    	
	    	IFileRevision revision = fhProvider.getWorkspaceFileRevision(resource);
	    	authorName = revision.getAuthor();
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	return authorName;
	}
}
