/*
 * Created on 7 mai 2005
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

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
 * AbstractPMDRecord for Projects creates Packages when instantiated
 * 
 * @author SebastianRaffel ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $$Revision$$
 * 
 * $$Log$
 * $Revision 1.4  2006/10/07 16:01:21  phherlin
 * $Integrate Sven updates
 * $$
 * 
 */
public class ProjectRecord extends AbstractPMDRecord {

    private IProject project;
    private RootRecord parent;
    private AbstractPMDRecord[] children;

    /**
     * Constructor
     * 
     * @param proj, the Project
     * @param record, the RootRecord
     */
    public ProjectRecord(IProject project, RootRecord record) {
        super();
        this.project = project;
        this.parent = record;
        
        if (project.isAccessible()) {
            this.children = createChildren();
        }
        
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getParent() */
    public AbstractPMDRecord getParent() {
        return parent;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getChildren() */
    public AbstractPMDRecord[] getChildren() {
        return children;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResource() */
    public IResource getResource() {
        return (IResource) project;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#createChildren() */
    protected final AbstractPMDRecord[] createChildren() {
        List packageList = new ArrayList();
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
                    List packages = createPackagesFromFragmentRoot((IPackageFragmentRoot) javaMember);
                    for (int j=0; j < packages.size(); j++) {
                        if (!packageList.contains(packages.get(j))) {
                            packageList.add(packages.get(j));
                        }
                    }
                } else if (javaMember instanceof IPackageFragment) {
                    // if the Element is a Package
                    IPackageFragment fragment = (IPackageFragment) javaMember;
                    // ... get its Root and do the same as above
                    if (fragment.getParent() instanceof IPackageFragmentRoot) {
                        List packages = createPackagesFromFragmentRoot((IPackageFragmentRoot) fragment.getParent());
                        for (int j=0; j < packages.size(); j++) {
                            if (!packageList.contains(packages.get(j))) {
                                packageList.add(packages.get(j));
                            }
                        }
                    }
                }
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        // return the List as an Array of Packages
        AbstractPMDRecord[] packageRecords = new AbstractPMDRecord[packageList.size()];
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

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getName() */
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

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResourceType() */
    public int getResourceType() {
        return AbstractPMDRecord.TYPE_PROJECT;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord addResource(IResource resource) {
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
            List packages = getChildrenAsList();
            packages.add(packageRec);

            // ... and we add a new FileRecord to it
            children = new AbstractPMDRecord[packages.size()];
            packages.toArray(children);
            return packageRec.addResource(resource);
        }

        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord removeResource(IResource resource) {
        // we only care about Files
        if (resource instanceof IFile) {
            IPackageFragment fragment = (IPackageFragment) JavaCore.create(resource.getParent());
            PackageRecord packageRec;

            // like above we compare Fragments to find the right Package
            for (int k = 0; k < children.length; k++) {
                packageRec = (PackageRecord) children[k];
                if (packageRec.getFragment().equals(fragment)) {

                    // if we found it, we remove the File
                    AbstractPMDRecord fileRec = packageRec.removeResource(resource);
                    if (packageRec.getChildren().length == 0) {
                        // ... and if the Package is empty too
                        // we also remove it
                        List packages = getChildrenAsList();
                        packages.remove(packageRec);

                        children = new AbstractPMDRecord[packages.size()];
                        packages.toArray(children);
                    }
                    return fileRec;
                }
            }
        }
        return null;
    }
}
