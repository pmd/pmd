/*
 * Created on 8 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides the Violation Overview with Texts and Images
 * 
 * @author SebastianRaffel ( 09.05.2005 ), Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2006/10/10 21:43:20  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class ViolationOverviewLabelProvider extends LabelProvider implements ITableLabelProvider {
    private static final String KEY_IMAGE_PACKAGE = "package";
    private static final String KEY_IMAGE_JAVAFILE = "javafile";
    final private ViolationOverview violationView;

    /**
     * Constructor
     * 
     * @param overview
     */
    public ViolationOverviewLabelProvider(ViolationOverview overview) {
        super();
        this.violationView = overview;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
     *      int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;

        // the second Column gets an Image depending on,
        // if the Element is a PackageRecord or FileRecord
        switch (columnIndex) {
        case 1:
            if (element instanceof PackageRecord) {
                image = PMDUiPlugin.getDefault().getImage(KEY_IMAGE_PACKAGE, PMDUiConstants.ICON_PACKAGE);
            } else if (element instanceof FileRecord) {
                image = PMDUiPlugin.getDefault().getImage(KEY_IMAGE_JAVAFILE, PMDUiConstants.ICON_JAVACU);
            }
            break;

        default:
            // let the image null.

        }

        return image;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
     *      int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";

        switch (columnIndex) {

        // show the Element's Name
        case 2:
            result = getPackageOrFileName(element);
            break;

        // show the Number of Violations
        case 3:
            result = String.valueOf(this.violationView.getFilteredViolations(element));
            break;

        // show the Number of Violations per Line of Code
        case 4:
            result = getViolationsPerLOC(element);
            break;

        // show the Number of Violations per Number of Methods
        case 5:
            result = getViolationsPerMethod(element);
            break;

        // show the Project's Name
        case 6:
            result = getProjectName(element);
            break;

        default:
            // let the result be an empty string
        }

        return result;
    }

    /**
     * Return the name for the element column.
     * 
     * @param element
     * @return
     */
    private String getPackageOrFileName(Object element) {
        String name = "";
        if (element instanceof PackageRecord) {
            name = ((PackageRecord) element).getName();
        } else if (element instanceof FileRecord) {
            name = ((FileRecord) element).getName();
        }

        return name;
    }

    /**
     * Return the label for the Violations per LOC column.
     * 
     * @param element
     * @return
     */
    private String getViolationsPerLOC(Object element) {
        String result = "";
        final int vioCount = this.violationView.getFilteredViolations(element);
        int loc = 0;

        if (element instanceof PackageRecord) {
            final PackageRecord packRec = ((PackageRecord) element);
            final Object[] files = packRec.getChildren();
            for (int i = 0; i < files.length; i++) {
                loc += ((FileRecord) files[i]).getLinesOfCode();
            }
        } else if (element instanceof FileRecord) {
            loc = ((FileRecord) element).getLinesOfCode();
        }

        if (loc == 0) {
            result = "N/A";
        } else {
            final float vioPerLoc = (float) Math.round((float) vioCount / loc * 100) / 100;
            if (vioPerLoc < 0.01) {
                result = "< 0.01";
            } else {
                result = String.valueOf(vioPerLoc);
            }
        }

        return result;

    }

    /**
     * Return the label for the Vioaltions per Method column.
     * 
     * @param element
     * @return
     */
    private String getViolationsPerMethod(Object element) {
        String result = "";
        final int vioCount2 = violationView.getFilteredViolations(element);
        int numMethods = 0;
        if (element instanceof PackageRecord) {
            final PackageRecord packRec = ((PackageRecord) element);
            final Object[] files = packRec.getChildren();
            for (int i = 0; i < files.length; i++) {
                numMethods += ((FileRecord) files[i]).getNumberOfMethods();
            }
        } else if (element instanceof FileRecord) {
            numMethods = ((FileRecord) element).getNumberOfMethods();
        }

        if (numMethods == 0) {
            result = "N/A";
        } else {
            final float vioPerMethod = (float) Math.round((float) vioCount2 / numMethods * 100) / 100;
            if ((vioPerMethod < 0.01) || (numMethods == 0)) {
                result = "< 0.01";
            } else {
                result = String.valueOf(vioPerMethod);
            }
        }

        return result;
    }

    /**
     * Return the project name.
     * 
     * @param element
     * @return
     */
    private String getProjectName(Object element) {
        AbstractPMDRecord projectRec = null;
        if (element instanceof PackageRecord) {
            projectRec = ((PackageRecord) element).getParent();
        } else if (element instanceof FileRecord) {
            projectRec = ((FileRecord) element).getParent().getParent();
        }

        return projectRec.getName();
    }
}
