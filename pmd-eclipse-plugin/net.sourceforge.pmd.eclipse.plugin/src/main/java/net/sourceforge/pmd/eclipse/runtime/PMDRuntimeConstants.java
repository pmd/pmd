package net.sourceforge.pmd.eclipse.runtime;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;


/**
 * This interface groups all plugin constants
 *
 * @author Philippe Herlin
 *
 */
public class PMDRuntimeConstants {

    public static final String PMD_MARKER       = PMDPlugin.PLUGIN_ID + ".pmdMarker";	// obsolete
    
    public static final String PMD_MARKER_1       = PMDPlugin.PLUGIN_ID + ".pmdMarker1";
    public static final String PMD_MARKER_2       = PMDPlugin.PLUGIN_ID + ".pmdMarker2";
    public static final String PMD_MARKER_3       = PMDPlugin.PLUGIN_ID + ".pmdMarker3";
    public static final String PMD_MARKER_4       = PMDPlugin.PLUGIN_ID + ".pmdMarker4";
    public static final String PMD_MARKER_5       = PMDPlugin.PLUGIN_ID + ".pmdMarker5";
    
    public static final String PMD_DFA_MARKER   = PMDPlugin.PLUGIN_ID + ".pmdDFAMarker";
    public static final String PMD_TASKMARKER   = PMDPlugin.PLUGIN_ID + ".pmdTaskMarker";
    public static final String[] RULE_MARKER_TYPES = new String[] { PMD_MARKER, PMD_MARKER_1, PMD_MARKER_2, PMD_MARKER_3, PMD_MARKER_4, PMD_MARKER_5 };   
    public static final String[] ALL_MARKER_TYPES = new String[] { PMD_MARKER, PMD_DFA_MARKER, PMD_TASKMARKER, PMD_MARKER_1, PMD_MARKER_2, PMD_MARKER_3, PMD_MARKER_4, PMD_MARKER_5 };
    
    public static final IntegerProperty MAX_VIOLATIONS_DESCRIPTOR = new IntegerProperty("maxviolations", "Max allowable violations", 1, Integer.MAX_VALUE-1, 1000, 0f);
    
    public static final String ID_PERSPECTIVE               = "net.sourceforge.pmd.eclipse.ui.views.pmdPerspective";

    public static final String KEY_MARKERATT_RULENAME       = "rulename";
    public static final String KEY_MARKERATT_PRIORITY       = "pmd_priority";
    public static final String KEY_MARKERATT_LINE2          = "line2";
    public static final String KEY_MARKERATT_VARIABLE       = "variable";
    public static final String KEY_MARKERATT_METHODNAME     = "method";

    public static final String PLUGIN_STYLE_REVIEW_COMMENT  = "// @PMD:REVIEWED:";
    public static final String PMD_STYLE_REVIEW_COMMENT     = "// NOPMD";

    public static final String REPORT_FOLDER                = "reports";
    public static final String HTML_REPORT_NAME             = "pmd-report.html";
    public static final String VBHTML_REPORT_NAME           = "pmd-report.vb.html";
    public static final String CSV_REPORT_NAME              = "pmd-report.csv";
    public static final String XML_REPORT_NAME              = "pmd-report.xml";
    public static final String TXT_REPORT_NAME              = "pmd-report.txt";
    public static final String SIMPLE_CPDREPORT_NAME        = "cpd-report.txt";
    public static final String XML_CPDREPORT_NAME           = "cpd-report.xml";
    public static final String CSV_CPDREPORT_NAME           = "cpd-report.csv";

    public static final int PROPERTY_CPD                    = 1111;
    public static final int PROPERTY_REVIEW                 = 1112;

    /**
     * This class is not meant to be instantiated
     *
     */
    private PMDRuntimeConstants() {
        super();
    }

}