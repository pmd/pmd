package net.sourceforge.pmd.ui.model;

import java.util.ArrayList;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

/**
 * PMDRecord for a Package creates Files when instantiated
 * 
 * @author SebastianRaffel ( 16.05.2005 )
 */
public class PackageRecord extends PMDRecord {

    private IPackageFragment packageFragment;
    private ProjectRecord parent;
    private PMDRecord[] children;

    /**
     * Constructor
     * 
     * @param fragment, the PackageFragment
     * @param record, the Project
     */
    public PackageRecord(IPackageFragment fragment, ProjectRecord record) {
        packageFragment = fragment;
        parent = record;
        children = createChildren();
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getParent() */
    public PMDRecord getParent() {
        return parent;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getChildren() */
    public PMDRecord[] getChildren() {
        return children;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getResource() */
    public IResource getResource() {
        try {
            return packageFragment.getCorrespondingResource();
        } catch (JavaModelException jme) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
        }
        return null;
    }

    /**
     * Gets the Package's Fragment
     * 
     * @return the Fragment
     */
    public IPackageFragment getFragment() {
        return packageFragment;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#createChildren() */
    protected PMDRecord[] createChildren() {
        ArrayList fileList = new ArrayList();
        try {
            ICompilationUnit[] javaUnits = packageFragment.getCompilationUnits();
            for (int k = 0; k < javaUnits.length; k++) {
                IResource javaResource = javaUnits[k].getCorrespondingResource();
                if (javaResource != null) {
                    FileRecord record = new FileRecord(javaResource, this);
                    fileList.add(record);
                }
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        PMDRecord[] fileRecords = new PMDRecord[fileList.size()];
        fileList.toArray(fileRecords);
        return fileRecords;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public PMDRecord addResource(IResource resource) {
        ICompilationUnit unit = packageFragment.getCompilationUnit(resource.getName());

        // we want the File to be a java-File
        if (unit != null) {
            // we create a new FileRecord and add it to the List
            FileRecord file = new FileRecord(resource, this);
            ArrayList files = getChildrenAsList();
            files.add(file);

            children = new PMDRecord[files.size()];
            files.toArray(children);
            return file;
        }
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public PMDRecord removeResource(IResource resource) {
        ArrayList files = getChildrenAsList();

        for (int i = 0; i < files.size(); i++) {
            PMDRecord file = (PMDRecord) files.get(i);

            // if the file is in here, remove it
            if (file.getResource().equals(resource)) {
                files.remove(i);

                children = new PMDRecord[files.size()];
                files.toArray(children);
                return file;
            }
        }

        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getName() */
    public String getName() {
        // for the default Package we return a String saying "default Package"
        if (packageFragment.isDefaultPackage())
            return PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_DEFAULT_PACKAGE);
        return packageFragment.getElementName();
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getResourceType() */
    public int getResourceType() {
        return PMDRecord.TYPE_PACKAGE;
    }
}
