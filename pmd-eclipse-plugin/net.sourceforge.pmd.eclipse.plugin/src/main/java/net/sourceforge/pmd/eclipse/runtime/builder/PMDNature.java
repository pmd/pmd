package net.sourceforge.pmd.eclipse.runtime.builder;


import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A project nature for PMD. Add a PMDBuilder to a project
 *
 * @author Philippe Herlin
 *
 */
public class PMDNature implements IProjectNature {
	
    public static final String PMD_NATURE = "net.sourceforge.pmd.eclipse.plugin.pmdNature";
    private IProject project;
    
    /**
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        IProjectDescription description = project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        if (!pmdBuilderFound(commands)) {
            ICommand pmdBuilderCommand = description.newCommand();
            pmdBuilderCommand.setBuilderName(PMDBuilder.PMD_BUILDER);
            ICommand[] newCommands = new ICommand[commands.length + 1];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[commands.length] = pmdBuilderCommand;
            description.setBuildSpec(newCommands);
            project.setDescription(description, null);
        }
    }

    /**
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        IProjectDescription description = project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        if (pmdBuilderFound(commands)) {
            ICommand[] newCommands = new ICommand[commands.length - 1];
            for (int i = 0, j = 0; i < commands.length; i++) {
                if (!commands[i].getBuilderName().equals(PMDBuilder.PMD_BUILDER)) {
                    newCommands[j++] = commands[i];
                }
            }
            description.setBuildSpec(newCommands);
            project.setDescription(description, null);
        }
    }

    /**
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
     */
    public void setProject(IProject project) {
        this.project = project;
    }

    /**
     * Add the PMD Nature to a project
     * @param project a project to set the PMD Nature
     * @param monitor a progress monitor
     * @return success true if the nature has been correctly set; false means
     * the project already had PMD nature.
     * @throws CoreException if any error occurs
     */
    public static boolean addPMDNature(final IProject project, final IProgressMonitor monitor) throws CoreException {
        boolean success = false;

        if (!project.hasNature(PMD_NATURE)) {
            final IProjectDescription description = project.getDescription();
            final String[] natureIds = description.getNatureIds();
            String[] newNatureIds = new String[natureIds.length + 1];
            System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
            newNatureIds[natureIds.length] = PMD_NATURE;
            description.setNatureIds(newNatureIds);
            project.setDescription(description, monitor);
            success = true;
        }

        return success;
    }

    /**
     * Remove the PMD Nature from a project
     * @param project a project to remove the PMD Nature
     * @param monitor a progress monitor
     * @return success true if the nature has been removed; false means the
     * project already had not the PMD Nature.
     * @throws CoreException if any error occurs.
     */
    public static boolean removePMDNature(final IProject project, final IProgressMonitor monitor) throws CoreException {
       boolean success = false;

       if (project.hasNature(PMD_NATURE)) {
           final IProjectDescription description = project.getDescription();
           final String[] natureIds = description.getNatureIds();
           String[] newNatureIds = new String[natureIds.length - 1];
           for (int i = 0, j = 0; i < natureIds.length; i++) {
               if (!natureIds[i].equals(PMD_NATURE)) {
                   newNatureIds[j++] = natureIds[i];
               }
           }
           description.setNatureIds(newNatureIds);
           project.setDescription(description, monitor);
           MarkerUtil.deleteAllMarkersIn(project);
       }

       return success;
    }

    /**
     * Check if PMD builder is already in command list
     * @param commands a command list
     */
    private boolean pmdBuilderFound(ICommand[] commands) {
        
        for (ICommand command: commands) {
            if (command.getBuilderName().equals(PMDBuilder.PMD_BUILDER)) {
                return true;
            }
        }

        return false;
    }

}
