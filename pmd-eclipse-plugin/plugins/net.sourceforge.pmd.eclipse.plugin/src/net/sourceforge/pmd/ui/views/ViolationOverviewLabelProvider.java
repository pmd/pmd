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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.ui.model.MarkerRecord;
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
 * Revision 1.5  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.4  2006/10/10 21:43:20  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class ViolationOverviewLabelProvider extends LabelProvider implements ITableLabelProvider {
    private static final String KEY_IMAGE_PACKAGE = "package";
    private static final String KEY_IMAGE_JAVAFILE = "javafile";
    private static final String KEY_IMAGE_ERR1 = "error1";
    private static final String KEY_IMAGE_ERR2 = "error2";
    private static final String KEY_IMAGE_ERR3 = "error3";
    private static final String KEY_IMAGE_ERR4 = "error4";
    private static final String KEY_IMAGE_ERR5 = "error5";
    
    private final ViolationOverview violationView;

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

        // the first column
        if (columnIndex == 0) {
            if (element instanceof PackageRecord) {
                image = getImage(KEY_IMAGE_PACKAGE, PMDUiConstants.ICON_PACKAGE);
            } else if ((element instanceof FileRecord) || (element instanceof FileToMarkerRecord)) {
                image = getImage(KEY_IMAGE_JAVAFILE, PMDUiConstants.ICON_JAVACU);
            } else if (element instanceof MarkerRecord) {
                final MarkerRecord markerRecord = (MarkerRecord)element;
                final int priority = markerRecord.getPriority();
                image = getPriorityImage(priority);
            }
        }

        return image;
    }

    /**
     * Gets the image to a priority.
     * @param priority priority
     * @return Image
     */
    private Image getPriorityImage(int priority) {
        Image image = null;

        switch (priority) {
        case 1:
            image = getImage(KEY_IMAGE_ERR1, PMDUiConstants.ICON_LABEL_ERR1);
            break;                
        case 2:
            image = getImage(KEY_IMAGE_ERR2, PMDUiConstants.ICON_LABEL_ERR2);
            break;                
        case 3:
            image = getImage(KEY_IMAGE_ERR3, PMDUiConstants.ICON_LABEL_ERR3);
            break;                    
        case 4:
            image = getImage(KEY_IMAGE_ERR4, PMDUiConstants.ICON_LABEL_ERR4);
            break;
        case 5:
            image = getImage(KEY_IMAGE_ERR5, PMDUiConstants.ICON_LABEL_ERR5);
            break;
        default:
            // do nothing
        }
        
        return image;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
     *      int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";

        if (element instanceof AbstractPMDRecord) {
            final AbstractPMDRecord record = (AbstractPMDRecord)element;
            switch (columnIndex) {
    
            // show the Element's Name
            case 0:
                result = getElementName(element);
                break;
    
            // show the Number of Violations
            case 1:
                result = getNumberOfViolations(record);
                break;
    
            // show the Number of Violations per Line of Code
            case 2:
                result = getViolationsPerLOC(record);
                break;
    
            // show the Number of Violations per Number of Methods
            case 3:
                result = getViolationsPerMethod(record);
                break;
    
            // show the Project's Name
            case 4:
                result = getProjectName(element);
                break;
    
            default:
                // let the result be an empty string
            }
        }
        return result;
    }

    /**
     * Gets the number of violation to an element. 
     * @param element the record
     * @return number as string
     */
    private String getNumberOfViolations(AbstractPMDRecord element) {
        final int violations = this.violationView.getNumberOfFilteredViolations(element);
        String result = String.valueOf(violations);

        if (element instanceof MarkerRecord 
                && violationView.getShowType() != ViolationOverview.SHOW_MARKERS_FILES) {
            final String ruleName = ((MarkerRecord)element).getName();
            final int maxViolations = getMaxViolations(ruleName);

            if (violations == maxViolations) {
                result = "(max) " + result;                    
            }   
        } else if (element instanceof FileToMarkerRecord) {
            final String ruleName = ((FileToMarkerRecord)element).getParent().getName();
            final int maxViolations = getMaxViolations(ruleName);

            if (violations == maxViolations) {
                result = "(max) " + result;                    
            }   
        }

        return result;
    }

    /**
     * Gets the maximum number of violations to a rule.
     * @param ruleName name of the rule
     * @return maximum number
     */
    private int getMaxViolations(String ruleName) {
        int maxViolations = PMDRuntimePlugin.getDefault().loadPreferences().getMaxViolationsPerFilePerRule(); 
        final Rule rule = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet().getRuleByName(ruleName);
        if (rule != null && rule.hasProperty(PMDRuntimeConstants.RULE_PROPERTY_MAXVIOLATIONS)) {
             maxViolations = rule.getIntProperty(PMDRuntimeConstants.RULE_PROPERTY_MAXVIOLATIONS);
        }
        return maxViolations;
    }

    /**
     * Return the name for the element column.
     * 
     * @param element
     * @return
     */
    private String getElementName(Object element) {
        String name = "";
        if (element instanceof PackageRecord) {
            name = ((PackageRecord) element).getName();
        } else if (element instanceof FileRecord) {
            name = ((FileRecord) element).getName();
        } else if (element instanceof MarkerRecord) {
            name = ((MarkerRecord) element).getName();
        } else if (element instanceof FileToMarkerRecord) {
            name = ((FileToMarkerRecord) element).getParent().getParent().getName();
        }

        return name;
    }

    /**
     * Return the label for the Violations per LOC column.
     * 
     * @param element
     * @return
     */
    private String getViolationsPerLOC(AbstractPMDRecord element) {
        String result = "";
        final int vioCount = this.violationView.getNumberOfFilteredViolations(element);
        final int loc = this.violationView.getLOC(element);

        if (loc == 0) {
            result = "N/A";
        } else {
            final double vioPerLoc = (double)(vioCount * 1000) / loc;
            if (vioPerLoc < 0.1) {
                result = "< 0.1 / 1000";
            } else {
                final DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
                format.applyPattern("##0.0");
                result = format.format(vioPerLoc) + " / 1000";
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
    private String getViolationsPerMethod(AbstractPMDRecord element) {
        String result = "";
        final int vioCount2 = violationView.getNumberOfFilteredViolations(element);
        final int numMethods = violationView.getNumberOfMethods(element);
        
        if (numMethods == 0) {
            result = "N/A";
        } else {
            final double vioPerMethod = (double)vioCount2 / numMethods;  
            
            if ((vioPerMethod < 0.01) || (numMethods == 0)) {
                result = "< 0.01";
            } else {
                final DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
                format.applyPattern("##0.00");
                result = format.format(vioPerMethod);
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
        String projectName = "";
        AbstractPMDRecord projectRec = null;
        if (element instanceof PackageRecord) {
            projectRec = ((PackageRecord) element).getParent();
        } else if (element instanceof FileRecord) {
            projectRec = ((FileRecord) element).getParent().getParent();
        } else if (element instanceof MarkerRecord) {
            projectRec = ((MarkerRecord) element).getParent().getParent().getParent();
        } else if (element instanceof FileToMarkerRecord) {
            projectRec = ((FileToMarkerRecord) element).getParent().getParent().getParent().getParent();
        }
        if (projectRec != null) {
            projectName = projectRec.getName();
        }
        return projectName;
    }

    /**
     * Helper method to get an image.
     * 
     * @param key
     * @param iconPath
     * @return
     */
    private Image getImage(String key, String iconPath) {
        return PMDUiPlugin.getDefault().getImage(key, iconPath);
    }
}
