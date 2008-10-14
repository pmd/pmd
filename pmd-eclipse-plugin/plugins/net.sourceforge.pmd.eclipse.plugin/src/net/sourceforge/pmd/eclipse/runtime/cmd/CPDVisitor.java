package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.io.File;
import java.util.List;

import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

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
 *
 */
public class CPDVisitor implements IResourceVisitor {
    private static final Logger log = Logger.getLogger(CPDVisitor.class);
    private boolean includeDerivedFiles;
    private ResourceWorkingSetFilter workingSetFilter;
    private Language language;
    private List<File> files;

    /**
     * @param includeDerivedFiles The includeDerivedFiles to set.
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        this.includeDerivedFiles = includeDerivedFiles;
    }

    /**
     * @param workingSet WorkingSet of the visited project.
     */
    public void setWorkingSet(IWorkingSet workingSet) {
        this.workingSetFilter = new ResourceWorkingSetFilter();
        this.workingSetFilter.setWorkingSet(workingSet);
    }

    /**
     * @param language Only add files with that language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @return the list of files
     */
    public List<File> getFiles() {
        return this.files;
    }

    /**
     * @param files the list of files to set
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource) Add java files into the CPD object
     */
    public boolean visit(IResource resource) throws CoreException {
        log.debug("CPD Visiting " + resource.getName());
        boolean result = true;

        if (resource instanceof IFile) {
            final IFile file = (IFile) resource;
            final File ioFile = ((IFile) resource).getLocation().toFile();
            try {
                if (((IFile) resource).getFileExtension() != null
                        && this.language.getFileFilter().accept(ioFile, file.getName())
                        && isFileInWorkingSet(file)
                                && (this.includeDerivedFiles
                                        || !this.includeDerivedFiles && !file.isDerived())) {
                    log.debug("Add file " + resource.getName());
                    this.files.add(ioFile);
                    result = false;
                }
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

        if (this.workingSetFilter != null) {
            fileInWorkingSet = this.workingSetFilter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

}
