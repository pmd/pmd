package net.sourceforge.pmd.swingui;

import java.awt.Color;
import java.awt.Font;
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
                 "pmdEditingPanelBackground", String.valueOf(Color.lightGray.getRGB()),
                    "disabledTextBackground", "#F5F5F5",
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
            "document",         LookAndFeel.makeIcon(plafClass, "icons/document.gif"),
            "save",             LookAndFeel.makeIcon(plafClass, "icons/save.gif"),
            "saveAs",           LookAndFeel.makeIcon(plafClass, "icons/saveAs.gif"),
            "print",            LookAndFeel.makeIcon(plafClass, "icons/print.gif"),
            "copy",             LookAndFeel.makeIcon(plafClass, "icons/copy.gif"),
            "edit",             LookAndFeel.makeIcon(plafClass, "icons/edit.gif"),
            "view",             LookAndFeel.makeIcon(plafClass, "icons/view.gif"),
            "help",             LookAndFeel.makeIcon(plafClass, "icons/help.gif"),
            "labelFont",        new Font("Dialog", Font.BOLD, 12),
            "dataFont",         new Font("Dialog", Font.PLAIN, 12),
            "codeFont",         new Font("Monospaced", Font.PLAIN, 12),
            "tabFont",          new Font("SansSerif", Font.BOLD, 12),
            "titleFont",        new Font("SansSerif", Font.BOLD, 14),
            "buttonFont",       new Font("SansSerif", Font.BOLD, 12),
            "messageFont",      new Font("Dialog", Font.PLAIN, 12),
        };

        table.putDefaults(defaults);
    }
}