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
import java.util.List;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

/**
 * AbstractPMDRecord for a Package creates Files when instantiated
 * 
 * @author SebastianRaffel ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $$Revision$$
 * 
 * $$Log$
 * $Revision 1.3  2006/10/07 16:01:21  phherlin
 * $Integrate Sven updates
 * $$
 * 
 */
public class PackageRecord extends AbstractPMDRecord {

    private IPackageFragment packageFragment;
    private ProjectRecord parent;
    private AbstractPMDRecord[] children;

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

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#createChildren() */
    protected final AbstractPMDRecord[] createChildren() {
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

        AbstractPMDRecord[] fileRecords = new AbstractPMDRecord[fileList.size()];
        fileList.toArray(fileRecords);
        return fileRecords;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord addResource(IResource resource) {
        ICompilationUnit unit = packageFragment.getCompilationUnit(resource.getName());

        // we want the File to be a java-File
        if (unit != null) {
            // we create a new FileRecord and add it to the List
            FileRecord file = new FileRecord(resource, this);
            List files = getChildrenAsList();
            files.add(file);

            children = new AbstractPMDRecord[files.size()];
            files.toArray(children);
            return file;
        }
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord removeResource(IResource resource) {
        List files = getChildrenAsList();

        for (int i = 0; i < files.size(); i++) {
            AbstractPMDRecord file = (AbstractPMDRecord) files.get(i);

            // if the file is in here, remove it
            if (file.getResource().equals(resource)) {
                files.remove(i);

                children = new AbstractPMDRecord[files.size()];
                files.toArray(children);
                return file;
            }
        }

        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getName() */
    public String getName() {
        // for the default Package we return a String saying "default Package"
        if (packageFragment.isDefaultPackage())
            return PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_DEFAULT_PACKAGE);
        return packageFragment.getElementName();
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResourceType() */
    public int getResourceType() {
        return AbstractPMDRecord.TYPE_PACKAGE;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PackageRecord other = (PackageRecord) obj;
        if (packageFragment == null) {
            if (other.packageFragment != null)
                return false;
        } else if (!packageFragment.equals(other.packageFragment))
            return false;
        return true;
    }
}
