package net.sourceforge.pmd.ui.model;

import java.util.ArrayList;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * PMDRecord for Projects creates Packages when instantiated
 * 
 * @author SebastianRaffel ( 16.05.2005 )
 */
public class ProjectRecord extends PMDRecord {

    private IProject project;
    private RootRecord parent;
    private PMDRecord[] children;

    /**
     * Constructor
     * 
     * @param proj, the Project
     * @param record, the RootRecord
     */
    public ProjectRecord(IProject proj, RootRecord record) {
        project = proj;
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
        return (IResource) project;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#createChildren() */
    protected final PMDRecord[] createChildren() {
        ArrayList packageList = new ArrayList();
        try {
            // search for Project members
            IResource[] members = project.members();
            for (int i = 0; i < members.length; i++) {

                // create JavaElements for each member
                IJavaElement javaMember = JavaCore.create(members[i]);
                if (javaMember == null)
                    continue;
                else if (javaMember instanceof IPackageFragmentRoot) {
                    // if the Element is the Root of all Packages
                    // get all packages from it and add them to the list
                    // (e.g. for "org.eclipse.core.resources" and
                    // "org.eclipse.core" the root is "org.eclipse.core")
                    packageList.addAll(createPackagesFromFragmentRoot((IPackageFragmentRoot) javaMember));
                } else if (javaMember instanceof IPackageFragment) {
                    // if the Element is a Package
                    IPackageFragment fragment = (IPackageFragment) javaMember;
                    // ... get its Root and do the same as above
                    if (fragment.getParent() instanceof IPackageFragmentRoot)
                        packageList.addAll(createPackagesFromFragmentRoot((IPackageFragmentRoot) fragment.getParent()));
                }
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        // return the List as an Array of Packages
        PMDRecord[] packageRecords = new PMDRecord[packageList.size()];
        packageList.toArray(packageRecords);
        return packageRecords;
    }

    /**
     * Search for the Packages to a given FragmentRoot (Package-Root) and create PackageRecords for them
     * 
     * @param root
     * @return
     */
    protected final ArrayList createPackagesFromFragmentRoot(IPackageFragmentRoot root) {
        ArrayList packages = new ArrayList();
        IJavaElement[] fragments = null;
        try {
            // search for all children
            fragments = root.getChildren();
            for (int k = 0; k < fragments.length; k++) {
                if (fragments[k] instanceof IPackageFragment) {
                    // create a PackageRecord for the Fragment
                    // and add it to the list
                    packages.add(new PackageRecord((IPackageFragment) fragments[k], this));
                }
            }
        } catch (JavaModelException jme) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
        }

        return packages;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getName() */
    public String getName() {
        return project.getName();
    }

    /**
     * Checks, if the underlying Project is open
     * 
     * @return true, if the Project is open, false otherwise
     */
    public boolean isProjectOpen() {
        return project.isOpen();
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getResourceType() */
    public int getResourceType() {
        return PMDRecord.TYPE_PROJECT;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public PMDRecord addResource(IResource resource) {
        // we only care about Files
        if (resource instanceof IFile) {
            IPackageFragment fragment = (IPackageFragment) JavaCore.create(resource.getParent());
            PackageRecord packageRec;

            // we search int the children Packages for the File's Package
            // by comparing their Fragments
            for (int k = 0; k < children.length; k++) {
                packageRec = (PackageRecord) children[k];
                if (packageRec.getFragment().equals(fragment))
                    // if the Package exists
                    // we delegate to its addResource-function
                    return packageRec.addResource(resource);
            }

            // ... else we create a new Record for the new Package
            packageRec = new PackageRecord(fragment, this);
            ArrayList packages = getChildrenAsList();
            packages.add(packageRec);

            // ... and we add a new FileRecord to it
            children = new PMDRecord[packages.size()];
            packages.toArray(children);
            return packageRec.addResource(resource);
        }

        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public PMDRecord removeResource(IResource resource) {
        // we only care about Files
        if (resource instanceof IFile) {
            IPackageFragment fragment = (IPackageFragment) JavaCore.create(resource.getParent());
            PackageRecord packageRec;

            // like above we compare Fragments to find the right Package
            for (int k = 0; k < children.length; k++) {
                packageRec = (PackageRecord) children[k];
                if (packageRec.getFragment().equals(fragment)) {

                    // if we found it, we remove the File
                    PMDRecord fileRec = packageRec.removeResource(resource);
                    if (packageRec.getChildren().length == 0) {
                        // ... and if the Package is empty too
                        // we also remove it
                        ArrayList packages = getChildrenAsList();
                        packages.remove(packageRec);

                        children = new PMDRecord[packages.size()];
                        packages.toArray(children);
                    }
                    return fileRec;
                }
            }
        }
        return null;
    }
}
