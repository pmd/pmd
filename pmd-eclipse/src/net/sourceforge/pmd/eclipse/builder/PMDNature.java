package net.sourceforge.pmd.eclipse.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * A project nature for PMD. Add a PMDBuilder to a project
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/03/17 23:35:59  phherlin
 * first version
 * adding nature and incremental builder
 *
 */
public class PMDNature implements IProjectNature {
    public static final String PMD_NATURE = "net.sourceforge.pmd.eclipse.pmdNature";
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
     * Check if PMD builder is allready in command list
     * @param commands a command list
     */
    private boolean pmdBuilderFound(ICommand[] commands) {
        boolean flFound = false;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(PMDBuilder.PMD_BUILDER)) {
                flFound = true;
                break;
            }
        }

        return flFound;
    }

}
