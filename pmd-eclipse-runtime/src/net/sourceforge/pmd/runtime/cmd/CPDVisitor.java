package net.sourceforge.pmd.runtime.cmd;

import java.io.IOException;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.ResourceWorkingSetFilter;

/**
 * A visitor to process IFile resource against CPD
 * 
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 * Revision 1.2 2006/05/02 18:34:23 phherlin Make CPD "working set aware" Revision 1.1 2005/05/31 23:04:11
 * phherlin Fix Bug 1190624: refactor CPD integration
 * 
 * Revision 1.4 2003/05/19 22:26:07 phherlin Updating PMD engine to v1.05 Fixing CPD usage to conform to new engine implementation
 * 
 */
public class CPDVisitor implements IResourceVisitor {
    private static Logger log = Logger.getLogger(CPDVisitor.class);
    private CPD cpd;
    private boolean includeDerivedFiles;

    /**
     * Constructor for CPDVisitor.
     */
    public CPDVisitor(CPD cpd) {
        super();
        this.cpd = cpd;
    }

    /**
     * @param includeDerivedFiles The includeDerivedFiles to set.
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        this.includeDerivedFiles = includeDerivedFiles;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource) Add java files into the CPD object
     */
    public boolean visit(IResource resource) throws CoreException {
        log.debug("CPD Visiting " + resource.getName());
        boolean result = true;

        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            try {
                if ((((IFile) resource).getFileExtension() != null)
                        && ((IFile) resource).getFileExtension().equals("java")
                        && (isFileInWorkingSet(file) && (this.includeDerivedFiles || (!this.includeDerivedFiles && !file
                                .isDerived())))) {
                    log.debug("Add file " + resource.getName());
                    cpd.add(((IFile) resource).getLocation().toFile());
                    result = false;
                }
            } catch (IOException e) {
                log.warn("IOException when adding file " + resource.getName() + " to CPD. Continuing.", e);
            } catch (PropertiesException e) {
                log.warn("ModelException when adding file " + resource.getName() + " to CPD. Continuing.", e);
            }
        }

        return result;

    }

    /**
     * Test if a file is in the PMD working set
     * 
     * @param file
     * @return true if the file should be checked
     */
    private boolean isFileInWorkingSet(final IFile file) throws PropertiesException {
        boolean fileInWorkingSet = true;
        final IProjectProperties properties = PMDRuntimePlugin.getDefault().loadProjectProperties(file.getProject());
        final IWorkingSet workingSet = properties.getProjectWorkingSet();
        if (workingSet != null) {
            final ResourceWorkingSetFilter filter = new ResourceWorkingSetFilter();
            filter.setWorkingSet(workingSet);
            fileInWorkingSet = filter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

}
