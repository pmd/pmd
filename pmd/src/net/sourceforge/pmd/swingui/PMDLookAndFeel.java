package net.sourceforge.pmd.swingui;

import java.awt.Color;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
public class PMDLookAndFeel extends WindowsLookAndFeel
{

    /**************************************************************
     *
     * @return
     */
    public String getDescription()
    {
        return "Source Forge PMD look and feel";
    }

    /**************************************************************
     *
     * @return
     */
    public String getID()
    {
        return "SourceForgePMD";
    }

    /**************************************************************
     *
     * @return
     */
    public String getName()
    {
        return "SourceForgePMD";
    }

    /**************************************************************
     *
     * @return
     */
    public boolean isNativeLookAndFeel()
    {
        return false;
    }

    /**************************************************************
     *
     * @return
     */
    public boolean isSupportedLookAndFeel()
    {
        return true;
    }

    /**************************************************************
     *
     * @param table
     */
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

        String pkgName = "net.sourceforge.pmd.swingui";
    }



    /**************************************************************
     *
     * @param table
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
          super.initSystemColorDefaults(table);

          String[] defaultSystemColors =
          {
                                   "pmdBlue", "#5A74AF",
                                   "pmdGray", "#C5C5C5",
                                    "pmdRed", "#CC4662",
                                  "pmdGreen", "#336666",
                                  "pmdCream", "#FFFCED",
                         "pmdTreeBackground", "#F5F5F5",
                        "pmdTableBackground", "#F5F5F5",
                                "mediumGray", "#686868",
                            "mediumDarkGray", "#434343",
                                  "paleGray", "#AAAAAA",
                  "pmdTableHeaderBackground", "#686868",
                  "pmdTableHeaderForeground", "#FFFFFF",
          };

          loadSystemColors(table, defaultSystemColors, isNativeLookAndFeel());
    }



    /**************************************************************
     *
     * @param table
     */
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);

        Class wlafClass = WindowsLookAndFeel.class;
        Class plafClass = PMDLookAndFeel.class;
        Object[] defaults =
        {
            "Tree.openIcon",    LookAndFeel.makeIcon(plafClass, "icons/TreeOpen.gif"),
            "Tree.closedIcon",  LookAndFeel.makeIcon(plafClass, "icons/TreeClosed.gif"),
            "Tree.leafIcon",    LookAndFeel.makeIcon(plafClass, "icons/TreeLeaf.gif"),
            "Document",         LookAndFeel.makeIcon(plafClass, "icons/Document.gif"),
            "Save",             LookAndFeel.makeIcon(plafClass, "icons/Save.gif"),
            "SaveAs",           LookAndFeel.makeIcon(plafClass, "icons/SaveAs.gif"),
            "Print",            LookAndFeel.makeIcon(plafClass, "icons/Print.gif"),
            "Copy",             LookAndFeel.makeIcon(plafClass, "icons/Copy.gif"),
            "Edit",             LookAndFeel.makeIcon(plafClass, "icons/Edit.gif"),
            "View",             LookAndFeel.makeIcon(plafClass, "icons/View.gif"),
            "QuestionMark",     LookAndFeel.makeIcon(plafClass, "icons/QuestionMark.gif"),
        };

        table.putDefaults(defaults);
    }
}