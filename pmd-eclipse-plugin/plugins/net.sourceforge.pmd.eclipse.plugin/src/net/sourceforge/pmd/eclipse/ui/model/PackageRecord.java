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

package net.sourceforge.pmd.eclipse.ui.model;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

/**
 * AbstractPMDRecord for a Package creates Files when instantiated
 * 
 * @author SebastianRaffel ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.6  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.5  2006/10/09 13:32:47  phherlin
 * Fix mistake in CVS tags (double $$)
 *
 * Revision 1.4  2006/10/08 23:11:06  phherlin
 * Review Sebastian code... and fix most PMD warnings
 * 
 * Revision 1.3 2006/10/07 16:01:21 phherlin
 * Integrate Sven updates
 * 
 */
public class PackageRecord extends AbstractPMDRecord {
    final private IPackageFragment packageFragment;
    final private ProjectRecord parent;
    private AbstractPMDRecord[] children;

    /**
     * Constructor
     * 
     * @param fragment, the PackageFragment
     * @param record, the Project
     */
    public PackageRecord(IPackageFragment fragment, ProjectRecord record) {
        super();

        if (fragment == null) {
            throw new IllegalArgumentException("fragment cannot be null");
        }

        if (record == null) {
            throw new IllegalArgumentException("record cannot be null");
        }

        this.packageFragment = fragment;
        this.parent = record;
        this.children = createChildren();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getParent()
     */
    public AbstractPMDRecord getParent() {
        return this.parent;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getChildren()
     */
    public AbstractPMDRecord[] getChildren() {
        return this.children; // NOPMD by Herlin on 09/10/06 00:22
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResource()
     */
    public IResource getResource() {
        IResource resource = null;
        try {
            resource = this.packageFragment.getCorrespondingResource();
        } catch (JavaModelException jme) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
        }
        return resource;
    }

    /**
     * Gets the Package's Fragment
     * 
     * @return the Fragment
     */
    public IPackageFragment getFragment() {
        return this.packageFragment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#createChildren()
     */
    protected final AbstractPMDRecord[] createChildren() {
        final List fileList = new ArrayList();
        try {
            final ICompilationUnit[] javaUnits = this.packageFragment.getCompilationUnits();
            for (int k = 0; k < javaUnits.length; k++) {
                final IResource javaResource = javaUnits[k].getCorrespondingResource();
                if (javaResource != null) {
                    fileList.add(new FileRecord(javaResource, this)); // NOPMD
                    // by
                    // Herlin
                    // on
                    // 09/10/06
                    // 00:25
                }
            }
        } catch (CoreException ce) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
        }

        return (AbstractPMDRecord[]) fileList.toArray(new AbstractPMDRecord[fileList.size()]);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord addResource(IResource resource) {
        final ICompilationUnit unit = this.packageFragment.getCompilationUnit(resource.getName());
        FileRecord file = null;

        // we want the File to be a java-File
        if (unit != null) {
            // we create a new FileRecord and add it to the List
            file = new FileRecord(resource, this);
            final List files = getChildrenAsList();
            files.add(file);

            this.children = new AbstractPMDRecord[files.size()];
            files.toArray(this.children);
        }

        return file;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource)
     */
    public AbstractPMDRecord removeResource(IResource resource) {
        final List files = getChildrenAsList();
        AbstractPMDRecord removedFile = null;
        boolean removed = false;

        for (int i = 0; (i < files.size()) && !removed; i++) {
            final AbstractPMDRecord file = (AbstractPMDRecord) files.get(i);

            // if the file is in here, remove it
            if (file.getResource().equals(resource)) {
                files.remove(i);

                this.children = new AbstractPMDRecord[files.size()]; // NOPMD
                // by
                // Herlin
                // on
                // 09/10/06
                // 00:31
                files.toArray(this.children);
                removed = true;
                removedFile = file;
            }
        }

        return removedFile;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getName()
     */
    public String getName() {
        String name = this.packageFragment.getElementName();

        // for the default Package we return a String saying "default Package"
        if (this.packageFragment.isDefaultPackage()) {
            name = PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_DEFAULT_PACKAGE);
        }

        return name;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getResourceType()
     */
    public int getResourceType() {
        return AbstractPMDRecord.TYPE_PACKAGE;
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        return obj instanceof PackageRecord ? this.packageFragment.equals(((PackageRecord) obj).packageFragment) : false;
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return this.packageFragment.hashCode();
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfViolationsToPriority(int)
     */
    public int getNumberOfViolationsToPriority(int prio, boolean invertMarkerAndFileRecords) {
        int number = 0;
        for (int i=0; i<children.length; i++) {
            number += children[i].getNumberOfViolationsToPriority(prio, false);
        }
        
        return number;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getLOC()
     */
    public int getLOC() {
        int number = 0;
        for (int i=0; i<children.length; i++) {
            number += children[i].getLOC();
        }
        
        return number;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord#getNumberOfMethods()
     */
    public int getNumberOfMethods() {
        int number = 0;
        for (int i=0; i<children.length; i++) {
            number += children[i].getNumberOfMethods();
        }
        
        return number;
    }

}
