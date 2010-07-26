/*
 * Created on 29 juin 2005
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

package net.sourceforge.pmd.eclipse.ui;

import net.sourceforge.pmd.RulePriority;


/**
 * This interface groups all plugin constants
 * 
 * @author Philippe Herlin
 *
 */
public class PMDUiConstants {
    public static final String PLUGIN_ID = "net.sourceforge.pmd.eclipse.ui";

    public static final String RULESET_PREFERENCE = PLUGIN_ID + ".ruleset";
    public static final String RULESET_DEFAULT = "";

    public static final String LIST_DELIMITER = ";";

    public static final String ICON_ERROR = "icons/error.gif";
    public static final String ICON_WARN = "icons/warn.gif";
    public static final String ICON_INFO = "icons/info.gif";
//    public static final String ICON_PROJECT = "icons/prj.gif";
    public static final String ICON_FILE = "icons/file.gif";
    public static final String ICON_PRIO1 = "icons/prio_1.gif";
    public static final String ICON_PRIO2 = "icons/prio_2.gif";
    public static final String ICON_PRIO3 = "icons/prio_3.gif";
    public static final String ICON_PRIO4 = "icons/prio_4.gif";
    public static final String ICON_PRIO5 = "icons/prio_5.gif";
    public static final String ICON_REMVIO = "icons/remvio.gif";
    public static final String ICON_LABEL_ERROR = "icons/lab_error.gif";
    public static final String ICON_LABEL_WARN = "icons/lab_warn.gif";
    public static final String ICON_LABEL_INFO = "icons/lab_info.gif";
    public static final String ICON_LABEL_ERR1 = "icons/lab_err1.gif";
    public static final String ICON_LABEL_ERR2 = "icons/lab_err2.gif";
    public static final String ICON_LABEL_ERR3 = "icons/lab_err3.gif";
    public static final String ICON_LABEL_ERR4 = "icons/lab_err4.gif";
    public static final String ICON_LABEL_ERR5 = "icons/lab_err5.gif";
    public static final String ICON_LABEL_ERR_DFA = "icons/lab_errdfa.gif";
    
    public static final String ICON_FILTER_R = "icons/filter_r.png";
    public static final String ICON_FILTER_X = "icons/filter_x.png";

    public static final String ICON_PROJECT = "icons/obj_project.gif";
    public static final String ICON_PACKAGE = "icons/obj_package.gif";
    public static final String ICON_JAVACU = "icons/obj_javacu.gif";
    

    public static final String ICON_BUTTON_DIAMOND_WHITE = "icons/btn_diamond_white.png";

    public static final String ICON_BUTTON_PRIO0 = "icons/btn_prio0.png";
    public static final String ICON_BUTTON_PRIO1 = "icons/btn_prio1.gif";
    public static final String ICON_BUTTON_PRIO2 = "icons/btn_prio2.gif";
    public static final String ICON_BUTTON_PRIO3 = "icons/btn_prio3.gif";
    public static final String ICON_BUTTON_PRIO4 = "icons/btn_prio4.gif";
    public static final String ICON_BUTTON_PRIO5 = "icons/btn_prio5.gif";
    
    public static final String ICON_BUTTON_PACKFILES = "icons/btn_packfiles.gif";
    public static final String ICON_BUTTON_FILEMARKERS = "icons/btn_filemarkers.gif";
	public static final String ICON_BUTTON_MARKERFILES = "icons/btn_markerfiles.gif";
	public static final String ICON_BUTTON_FILES = "icons/btn_files.gif";

    public static final String ICON_BUTTON_DISABLE = "icons/btn_disable.gif";
    public static final String ICON_BUTTON_COLLAPSE = "icons/btn_collapse.gif";
    public static final String ICON_BUTTON_REMVIO = "icons/btn_remvio.gif";
	public static final String ICON_BUTTON_QUICKFIX = "icons/btn_quickfix.gif";
	public static final String ICON_BUTTON_REVIEW = "icons/btn_review.gif";
    public static final String ICON_BUTTON_REFRESH = "icons/btn_refresh.gif";
    public static final String ICON_BUTTON_CALCULATE = "icons/btn_calculate.gif";
    

    public static final String ICON_BUTTON_UPARROW = "icons/upArrow.png";
    public static final String ICON_BUTTON_DOWNARROW = "icons/downArrow.png";
    public static final String ICON_BUTTON_ADD = "icons/add.gif";
    public static final String ICON_BUTTON_DELETE = "icons/delete.gif";
    public static final String ICON_BUTTON_CHECK_ALL = "icons/checkAll.png";
    public static final String ICON_BUTTON_UNCHECK_ALL = "icons/uncheckAll.png";
    public static final String ICON_BUTTON_IMPORT = "icons/import.gif";
    public static final String ICON_BUTTON_EXPORT = "icons/export.gif";
    public static final String ICON_BUTTON_EDITOR = "icons/editTool.png";
    public static final String ICON_BUTTON_SORT_CHECKED = "icons/sortChecked.png";
    public static final String ICON_BUTTON_RELOAD = "icons/icon_spinningArrows.png";
    
    public static final String ICON_GREEN_CHECK = "icons/icon_greenCheck.png";
    public static final String ICON_EMPTY = "icons/icon_empty.png";
    
    public static final String ID_PERSPECTIVE = PLUGIN_ID + ".views.pmdPerspective";
    public static final String ID_OUTLINE = PLUGIN_ID + ".views.violationOutline";
    public static final String ID_OVERVIEW = PLUGIN_ID + ".views.violationOverview";
    public static final String ID_DATAFLOWVIEW = PLUGIN_ID + ".views.dataflowView";
    public static final String ID_CPDVIEW = PLUGIN_ID + ".views.CPDView";
    
    public static final String MEMENTO_OUTLINE_FILE = "/violationOutline_memento.xml";
    public static final String MEMENTO_OVERVIEW_FILE = "/violationOverview_memento.xml";
    public static final String MEMENTO_DATAFLOW_FILE = "/dataflowView_memento.xml";
    
    public static final String KEY_MARKERATT_RULENAME = "rulename";
    public static final String KEY_MARKERATT_PRIORITY = "pmd_priority";
    public static final String KEY_MARKERATT_LINE2 = "line2";
    public static final String KEY_MARKERATT_VARIABLE = "variable";
    public static final String KEY_MARKERATT_METHODNAME = "method";
    
    public static final String SETTINGS_VIEW_FILE_SELECTION = "view.file_selection";
    public static final String SETTINGS_VIEW_PROJECT_SELECTION = "view.project_selection";
    public static final String SETTINGS_VIEW_ERRORHIGH_FILTER = "view.errorhigh_filter";
    public static final String SETTINGS_VIEW_ERROR_FILTER = "view.high_filter";
    public static final String SETTINGS_VIEW_WARNINGHIGH_FILTER = "view.warninghigh_filter";
    public static final String SETTINGS_VIEW_WARNING_FILTER = "view.warning_filter";
    public static final String SETTINGS_VIEW_INFORMATION_FILTER = "view.information_filter";
    
    public static final String REPORT_FOLDER = "reports";
    public static final String HTML_REPORT_NAME = "pmd-report.html";
    public static final String VBHTML_REPORT_NAME = "pmd-report.vb.html";
    public static final String CSV_REPORT_NAME = "pmd-report.csv";
    public static final String XML_REPORT_NAME = "pmd-report.xml";
    public static final String TXT_REPORT_NAME = "pmd-report.txt";
    public static final String SIMPLE_CPDREPORT_NAME = "cpd-report.txt";
    
    /**
     * This class is not meant to be instantiated
     *
     */
    private PMDUiConstants() {
        super();
    }    
    
    public static String buttonCodePathFor(RulePriority priority, boolean asButton) {
        
        switch (priority.getPriority()) {
            case 1: return asButton ? ICON_BUTTON_PRIO1 : ICON_PRIO1;
            case 2: return asButton ? ICON_BUTTON_PRIO2 : ICON_PRIO2;
            case 3: return asButton ? ICON_BUTTON_PRIO3 : ICON_PRIO3;
            case 4: return asButton ? ICON_BUTTON_PRIO4 : ICON_PRIO4;
            case 5: return asButton ? ICON_BUTTON_PRIO5 : ICON_PRIO5;
            default : throw new RuntimeException("unknown priority level");
        }
    }
}