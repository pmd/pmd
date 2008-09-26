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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
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
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.12  2007/02/28 20:56:04  hooperbloob
 * Optimization: new AbstractPMDRecord[0] -> AbstractPMDRecord.EMPTY_RECORDS (static final constant)
 *
 * Revision 1.11  2007/01/18 21:01:20  phherlin
 * Fix several cast and NPE exceptions
 * Revision 1.10 2006/12/22 14:15:09 holobender fixed bug
 * 
 * Revision 1.9 2006/11/20 14:53:27 holobender Fix: When Packages are in a src
 * folder and not in the root of the project, they are not shown in the
 * violation overview.
 * 
 * Revision 1.8 2006/11/16 17:11:08 holobender Some major changes: - new CPD
 * View - changed and refactored ViolationOverview - some minor changes to
 * dataflowview to work with PMD
 * 
 * Revision 1.7 2006/10/09 13:32:47 phherlin Fix mistake in CVS tags (double $$)
 * 
 * Revision 1.6 2006/10/08 23:11:06 phherlin Review Sebastian code... and fix
 * most PMD warnings
 * 
 * Revision 1.5 2006/10/08 22:19:34 phherlin Fix last Java warnings
 * 
 * Revision 1.4 2006/10/07 16:01:21 phherlin Integrate Sven updates
 * 
 * 
 */
public class ProjectRecord extends AbstractPMDRecord {
    final private IProject project;
    final private RootRecord parent;
    private AbstractPMDRecord[] children;

    /**
     * Constructor
     * 
     * @param proj, the Project
     * @param record, the RootRecord
     */
    public ProjectRecord(IProject project, RootRecord record) {
        super();

        if (project == null) {
            throw new IllegalArgumentException("project cannot be null");
        }

        if (record == null) {
            throw new IllegalArgumentException("record cannot be null");

        }

        this.project = project;
        this.parent = record;

        if (project.isAccessible()) {
            this.children = createChildren();
        } else {
            this.children = AbstractPMDRecord.EMPTY_RECORDS;
        }

    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getParent()
     */
    public AbstractPMDRecord getParent() {
        return this.parent;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getChildren()
     */
    public AbstractPMDRecord[] getChildren() {
        return this.children; // NOPMD by Herlin on 09/10/06 00:43
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResource()
     */
    public IResource getResource() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#createChildren()
     */
    protected final AbstractPMDRecord[] createChildren() {
        final Set packages = new HashSet();
        try {
            // search for Packages
            this.project.accept(new IResourceVisitor() {

                public boolean visit(IResource resource) throws CoreException {
                    boolean visitChildren = false;
                    switch (resource.getType()) {
                    case IResource.FOLDER:
                        final IJavaElement javaMember = JavaCore.create(resource);

                        if (javaMember == null) {
                            visitChildren = true;
                        } else {
                            if (javaMember instanceof IPackageFragmentRoot) {
                                // if the Element is the Root of all Packages
                                // get all packages from it and add them to the
                                // list
                                // (e.g. for "org.eclipse.core.resources" and
                                // "org.eclipse.core" the root is
                                // "org.eclipse.core")
                                packages.addAll(createPackagesFromFragmentRoot((IPackageFragmentRoot) javaMember));
                            } else if ((javaMember instanceof IPackageFragment)
                                    && (javaMember.getParent() instanceof IPackageFragmentRoot)) {
                                // if the Element is a Package get its Root and
                                // do the same as above
                                final IPackageFragment fragment = (IPackageFragment) javaMember;
                                packages.addAll(createPackagesFromFragmentRoot((IPackageFragmentRoot) fragment.getParent()));
                            }
                            visitChildren = false;
                        }
                        break;
                    case IResource.PROJECT:
                        visitChildren = true;
                        break;
                    default:
                        visitChildren = false;
                    }
                    return visitChildren;
                }
            });
        } catch (CoreException ce) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        // return the List as an Array of Packages
        return (AbstractPMDRecord[]) packages.toArray(new AbstractPMDRecord[packages.size()]);
    }

    /**
     * Search for the Packages to a given FragmentRoot (Package-Root) and create
     * PackageRecords for them
     * 
     * @param root
     * @return
     */
    protected final Set createPackagesFromFragmentRoot(IPackageFragmentRoot root) {
        final Set packages = new HashSet();
        IJavaElement[] fragments = null;
        try {
            // search for all children
            fragments = root.getChildren();
            for (int k = 0; k < fragments.length; k++) {
                if (fragments[k] instanceof IPackageFragment) {
                    // create a PackageRecord for the Fragment
                    // and add it to the list
                    packages.add(new PackageRecord((IPackageFragment) fragments[k], this)); // NOPMD
                                                                                            // by
                                                                                            // Herlin
                                                                                            // on
                                                                                            // 09/10/06
                                                                                            // 00:47
                }
            }
        } catch (JavaModelException jme) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
        }

        return packages;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getName()
     */
    public String getName() {
        return this.project.getName();
    }

    /**
     * Checks, if the underlying Project is open
     * 
     * @return true, if the Project is open, false otherwise
     */
    public boolean isProjectOpen() {
        return this.project.isOpen();
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResourceType()
     */
    public int getResourceType() {
        return AbstractPMDRecord.TYPE_PROJECT;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord addResource(IResource resource) {
        AbstractPMDRecord addedResource = null;

        // we only care about Files
        if (resource instanceof IFile) {
            IJavaElement javaMember = JavaCore.create(resource.getParent());
            if (javaMember instanceof IPackageFragmentRoot) {
                javaMember = ((IPackageFragmentRoot) javaMember).getPackageFragment("");
            }

            final IPackageFragment fragment = (IPackageFragment) javaMember;

            // we search int the children Packages for the File's Package
            // by comparing their Fragments
            for (int k = 0; (k < this.children.length) && (addedResource == null); k++) {
                final PackageRecord packageRec = (PackageRecord) children[k];
                if (packageRec.getFragment().equals(fragment)) {
                    // if the Package exists
                    // we delegate to its addResource-function
                    addedResource = packageRec.addResource(resource);
                }
            }

            // ... else we create a new Record for the new Package
            if (addedResource == null) {
                final PackageRecord packageRec = new PackageRecord(fragment, this);
                final List packages = getChildrenAsList();
                packages.add(packageRec);

                // ... and we add a new FileRecord to it
                this.children = new AbstractPMDRecord[packages.size()];
                packages.toArray(children);
                addedResource = packageRec.addResource(resource);
            }
        }

        return addedResource;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord removeResource(IResource resource) {
        AbstractPMDRecord removedResource = null;

        // we only care about Files
        if (resource instanceof IFile) {
            IPackageFragment fragment;
            final IJavaElement element = JavaCore.create(resource.getParent());
            if (element instanceof IPackageFragment) {
                fragment = (IPackageFragment) element;
            } else {
                fragment = ((IPackageFragmentRoot) element).getPackageFragment("");
            }

            PackageRecord packageRec;

            // like above we compare Fragments to find the right Package
            for (int k = 0; (k < this.children.length) && (removedResource == null); k++) {
                packageRec = (PackageRecord) this.children[k];
                if (packageRec.getFragment().equals(fragment)) {

                    // if we found it, we remove the File
                    final AbstractPMDRecord fileRec = packageRec.removeResource(resource);
                    if (packageRec.getChildren().length == 0) {
                        // ... and if the Package is empty too
                        // we also remove it
                        final List packages = getChildrenAsList();
                        packages.remove(packageRec);

                        this.children = new AbstractPMDRecord[packages.size()]; // NOPMD
                                                                                // by
                                                                                // Herlin
                                                                                // on
                                                                                // 09/10/06
                                                                                // 00:54
                        packages.toArray(this.children);
                    }

                    removedResource = fileRec;
                }
            }
        }

        return removedResource;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getNumberOfViolationsToPriority(int)
     */
    public int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords) {
        int number = 0;
        for (int i = 0; i < children.length; i++) {
            number += children[i].getNumberOfViolationsToPriority(prio, invertMarkerAndFileRecords);
        }

        return number;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getLOC()
     */
    public int getLOC() {
        int number = 0;
        for (int i = 0; i < children.length; i++) {
            number += children[i].getLOC();
        }

        return number;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getNumberOfMethods()
     */
    public int getNumberOfMethods() {
        int number = 0;
        for (int i = 0; i < children.length; i++) {
            number += children[i].getNumberOfMethods();
        }

        return number;
    }

}
