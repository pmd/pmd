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

package net.sourceforge.pmd.eclipse.ui.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.MarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides the Violation Overview with Texts and Images
 *
 * @author SebastianRaffel ( 09.05.2005 ), Philippe Herlin
 *
 */
public class ViolationOverviewLabelProvider extends AbstractViolationLabelProvider {
	
    private static final String KEY_IMAGE_PACKAGE = "package";
    private static final String KEY_IMAGE_JAVAFILE = "javafile";

    private final ViolationOverview violationView;

    /**
     * Constructor
     *
     * @param overview
     */
    public ViolationOverviewLabelProvider(ViolationOverview overview) {
        super();
        violationView = overview;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;

        // the first column
        if (columnIndex == 0) {
            if (element instanceof PackageRecord) {
                image = getImage(KEY_IMAGE_PACKAGE, PMDUiConstants.ICON_PACKAGE);
            } else if (element instanceof FileRecord || element instanceof FileToMarkerRecord) {
                image = getImage(KEY_IMAGE_JAVAFILE, PMDUiConstants.ICON_JAVACU);
            } else if (element instanceof MarkerRecord) {
                MarkerRecord markerRecord = (MarkerRecord)element;
                int priority = markerRecord.getPriority();
                image = getPriorityImage(priority);
            }
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

            // show the Number of Violations per 1K lines of code
            case 2:
                result = getViolationsPerKLOC(record);
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
        int maxViolations = PMDPlugin.getDefault().loadPreferences().getMaxViolationsPerFilePerRule();
        final Rule rule = PMDPlugin.getDefault().getPreferencesManager().getRuleSet().getRuleByName(ruleName);
        if (rule != null) {
            if (rule.hasDescriptor(PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR)) {
        	return rule.getProperty(PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR);
            } else {
        	return PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR.defaultValue();
            }
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
    private String getViolationsPerKLOC(AbstractPMDRecord element) {
        String result = "";
        int vioCount = violationView.getNumberOfFilteredViolations(element);
        int loc = violationView.getLOC(element);

        if (loc == 0) {
            result = "N/A";
        } else {
            double vioPerLoc = (double)(vioCount * 1000) / loc;
            if (vioPerLoc < 0.1) {
                result = "< 0.1";
            } else {
                DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
                format.applyPattern("##0.0");
                result = format.format(vioPerLoc);
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

            if (vioPerMethod < 0.01 || numMethods == 0) {
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
        return PMDPlugin.getDefault().getImage(key, iconPath);
    }
}
