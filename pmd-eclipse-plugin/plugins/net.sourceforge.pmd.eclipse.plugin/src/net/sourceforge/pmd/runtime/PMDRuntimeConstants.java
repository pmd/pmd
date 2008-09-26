package net.sourceforge.pmd.runtime;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;


/**
 * This interface groups all plugin constants
 *
 * @author Philippe Herlin
 * @version $Revision$
 *
 * $Log$
 * Revision 1.4  2006/11/16 16:54:41  holobender
 * - changed command for the new cpd view
 * - possibility to set the number of maxviolations per file over the rule-properties
 *
 * Revision 1.3  2006/10/07 16:01:51  phherlin
 * Integrate Sven updates
 *
 * Revision 1.2  2006/06/05 21:28:16  phherlin
 * Fix the switch perspective NPE
 *
 * Revision 1.1  2006/05/22 21:37:36  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.12  2006/05/07 12:01:50  phherlin
 * Add the possibility to use the PMD violation review style
 *
 * Revision 1.11  2006/05/02 20:10:49  phherlin
 * Limit the number of reported violations per file and per rule
 *
 * Revision 1.10  2006/05/02 18:49:51  phherlin
 * Remove dead code
 *
 * Revision 1.9  2006/04/24 21:18:11  phherlin
 * Rulesets list are now managed in the core plugin
 *
 * Revision 1.8  2006/04/11 21:00:58  phherlin
 * Add new VBHTML report
 *
 * Revision 1.7  2005/10/24 22:36:42  phherlin
 * Integrating Sebastian Raffel's work
 *
 * Revision 1.6  2005/05/31 23:04:10  phherlin
 * Fix Bug 1190624: refactor CPD integration
 *
 * Revision 1.5  2005/05/31 20:33:02  phherlin
 * Continuing refactoring
 *
 * Revision 1.4  2005/04/20 23:15:32  phherlin
 * Implement reports generation RFE#1177802
 *
 * Revision 1.3  2005/01/31 23:39:37  phherlin
 * Upgrading to PMD 2.2
 *
 * Revision 1.2  2005/01/16 22:52:17  phherlin
 * Upgrade to PMD 2.1: add new packaged rulesets
 *
 * Revision 1.1  2004/06/29 22:00:30  phherlin
 * Adapting the plugin to the new OSGi standards
 *
 */
public class PMDRuntimeConstants {

    public static final String PMD_MARKER = PMDPlugin.PLUGIN_ID + ".pmdMarker";
    public static final String PMD_DFA_MARKER = PMDPlugin.PLUGIN_ID + ".pmdDFAMarker";
    public static final String PMD_TASKMARKER = PMDPlugin.PLUGIN_ID + ".pmdTaskMarker";

    public static final String RULE_PROPERTY_MAXVIOLATIONS = "maxviolations";

    public static final String ID_PERSPECTIVE = "net.sourceforge.pmd.ui.views.pmdPerspective";

    public static final String KEY_MARKERATT_RULENAME = "rulename";
    public static final String KEY_MARKERATT_PRIORITY = "pmd_priority";
    public static final String KEY_MARKERATT_LINE2 = "line2";
    public static final String KEY_MARKERATT_VARIABLE = "variable";
    public static final String KEY_MARKERATT_METHODNAME = "method";

    public static final String PLUGIN_STYLE_REVIEW_COMMENT = "// @PMD:REVIEWED:";
    public static final String PMD_STYLE_REVIEW_COMMENT = "// NOPMD";

    public static final String REPORT_FOLDER = "reports";
    public static final String HTML_REPORT_NAME = "pmd-report.html";
    public static final String VBHTML_REPORT_NAME = "pmd-report.vb.html";
    public static final String CSV_REPORT_NAME = "pmd-report.csv";
    public static final String XML_REPORT_NAME = "pmd-report.xml";
    public static final String TXT_REPORT_NAME = "pmd-report.txt";
    public static final String SIMPLE_CPDREPORT_NAME = "cpd-report.txt";
    public static final String XML_CPDREPORT_NAME = "cpd-report.xml";
    public static final String CSV_CPDREPORT_NAME = "cpd-report.csv";

    public static final int PROPERTY_CPD = 1111;
    public static final int PROPERTY_REVIEW = 1112;

    /**
     * This class is not meant to be instantiated
     *
     */
    private PMDRuntimeConstants() {
        super();
    }

}