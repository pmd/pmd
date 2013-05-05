package net.sourceforge.pmd.eclipse.runtime.cmd;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractProjectCommand extends AbstractDefaultCommand {

    private IProject project;

    private static final long serialVersionUID = 1L;
    
	protected AbstractProjectCommand(String theName, String theDescription) {
		super(theName, theDescription);
	}

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        setProject(null);
        setTerminated(false);
    }
    
    /**
     * @param project The project to set.
     */
    public void setProject(final IProject theProject) {
        project = theProject;
        setReadyToExecute(project != null);
    }
    
    protected IProject project() {
    	return project;
    }
    
    protected void visitProjectResourcesWith(IResourceVisitor visitor) throws CoreException {
    	project.accept(visitor);
    }
    
    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return project != null;
    }
    
    protected IFolder getProjectFolder(String folderId) {
    	return project.getFolder(folderId);
    }
    
    protected IProjectProperties projectProperties() throws PropertiesException {
    	return PMDPlugin.getDefault().loadProjectProperties(project);
    }
}
