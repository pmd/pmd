package net.sourceforge.pmd.swingui;
//J_
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
public class PMDLookAndFeel extends WindowsLookAndFeel
{

    /**
     ****************************************************************************
     *
     * @return
     */
    public String getDescription() {
        return "Source Forge PMD look and feel";
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public String getID() {
        return "SourceForgePMD";
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public String getName() {
        return "SourceForgePMD";
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public boolean isNativeLookAndFeel() {
        return false;
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    /**
     ****************************************************************************
     *
     * @param table
     */
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);

        //String pkgName = "net.sourceforge.pmd.swingui";
    }

    /**
     ****************************************************************************
     *
     * @param table
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
        super.initSystemColorDefaults(table);

        Color darkBlue = Color.blue.darker();

        String[] defaultSystemColors = {
                                        "pmdBlue", String.valueOf(darkBlue.getRGB()),
                                        "pmdRed", String.valueOf(Color.red.getRGB()),
                                        "pmdGreen", "#336666",
                                        "pmdGray", "#F0F0F0",
                                        "pmdTreeBackground", "#F0F0F0",
                                        "pmdTableBackground", "#F0F0F0",
                                        "pmdMessageAreaBackground", "#F0F0F0",
                                        "pmdStatusAreaBackground", "#F0F0F0",
                                        "mediumGray", "#686868",
                                        "mediumDarkGray", "#434343",
                                        "paleGray", "#AAAAAA",
                                        "standardButtonBackground", "#686868",
                                        "standardButtonForeground", "#FFFFFF",
                                        "pmdTableHeaderBackground", "#686868",
                                        "pmdTableHeaderForeground", "#FFFFFF",
                                        "pmdEditingPanelBackground", String.valueOf(Color.lightGray.getRGB()),
                                        "disabledTextBackground", "#AAAAAA",
                                        };

        loadSystemColors(table, defaultSystemColors, isNativeLookAndFeel());
    }

    /**
     ****************************************************************************
     *
     * @param table
     */
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        Class lafClass = WindowsLookAndFeel.class;
        Class plafClass = PMDLookAndFeel.class;
        Object[] defaults = {"document", LookAndFeel.makeIcon(plafClass, "icons/document.gif"),
                             "cancel", LookAndFeel.makeIcon(plafClass, "icons/cancel.gif"),
                             "save", LookAndFeel.makeIcon(plafClass, "icons/save.gif"),
                             "saveAs", LookAndFeel.makeIcon(plafClass, "icons/saveas.gif"),
                             "print", LookAndFeel.makeIcon(plafClass, "icons/print.gif"),
                             "copy", LookAndFeel.makeIcon(plafClass, "icons/copy.gif"),
                             "edit", LookAndFeel.makeIcon(plafClass, "icons/edit.gif"),
                             "view", LookAndFeel.makeIcon(plafClass, "icons/view.gif"),
                             "help", LookAndFeel.makeIcon(plafClass, "icons/help.gif"),
                             "pmdLogo", LookAndFeel.makeIcon(plafClass, "icons/pmdlogo.gif"),
                             "pmdLogoImage", getImageIcon("icons/pmdlogo.jpg"),
                             "labelFont", new Font("Dialog", Font.BOLD, 12),
                             "label14Font", new Font("Dialog", Font.BOLD, 14),
                             "label16Font", new Font("Dialog", Font.BOLD, 16),
                             "dataFont", new Font("Dialog", Font.PLAIN, 12),
                             "codeFont", new Font("Monospaced", Font.PLAIN, 12),
                             "tabFont", new Font("SansSerif", Font.BOLD, 12),
                             "titleFont", new Font("SansSerif", Font.BOLD, 14),
                             "buttonFont", new Font("SansSerif", Font.BOLD, 12),
                             "messageFont", new Font("Dialog", Font.PLAIN, 12),
                             "serif12Font", new Font("Serif", Font.PLAIN, 12),
                             "serif14Font", new Font("Serif", Font.PLAIN, 14),
                             "viewerProperties", loadViewerProperties(),

                             // These are all the icons defined in the WindowsLookAndFeel.  We redefine them
                             // here because of the way they are defined in that class: in terms of the return
                             // value of getClass().  I.e., getClass() just returns the handle to the invoking
                             // class, which now is PMDLookAndFeel.  That means that the icons are searched
                             // for in the PMD look and feel package, which is not where they really are.
                             // Since we've just called the superclass method, the icons have been installed
                             // incorrectly in the table.  Reinstall them using the correct class.

                             "Tree.openIcon", LookAndFeel.makeIcon(lafClass, "icons/TreeOpen.gif"), "Tree.closedIcon", LookAndFeel.makeIcon(lafClass, "icons/TreeClosed.gif"), "Tree.leafIcon", LookAndFeel.makeIcon(lafClass, "icons/TreeLeaf.gif"),

                             "FileChooser.newFolderIcon", LookAndFeel.makeIcon(lafClass, "icons/NewFolder.gif"), "FileChooser.upFolderIcon", LookAndFeel.makeIcon(lafClass, "icons/UpFolder.gif"), "FileChooser.homeFolderIcon", LookAndFeel.makeIcon(lafClass, "icons/HomeFolder.gif"), "FileChooser.detailsViewIcon", LookAndFeel.makeIcon(lafClass, "icons/DetailsView.gif"), "FileChooser.listViewIcon", LookAndFeel.makeIcon(lafClass, "icons/ListView.gif"),

                             "FileView.directoryIcon", LookAndFeel.makeIcon(lafClass, "icons/Directory.gif"), "FileView.fileIcon", LookAndFeel.makeIcon(lafClass, "icons/File.gif"), "FileView.computerIcon", LookAndFeel.makeIcon(lafClass, "icons/Computer.gif"), "FileView.hardDriveIcon", LookAndFeel.makeIcon(lafClass, "icons/HardDrive.gif"), "FileView.floppyDriveIcon", LookAndFeel.makeIcon(lafClass, "icons/FloppyDrive.gif"), };

        table.putDefaults(defaults);
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    private Properties loadViewerProperties() {
        Properties properties = new Properties();

        try {
            InputStream inputStream = getClass().getResourceAsStream("pmdViewer.properties");

            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return properties;
    }

    /**
     *******************************************************************************
     *
     * @param fileName
     *
     * @return
     */
    protected static final ImageIcon getImageIcon(String fileName) {
        final byte[][] buffer = new byte[1][];

        try {
            InputStream resource = PMDLookAndFeel.class.getResourceAsStream(fileName);

            if (resource == null) {
                return null;
            }

            BufferedInputStream in;
            ByteArrayOutputStream out;
            int n;

            in = new BufferedInputStream(resource);
            out = new ByteArrayOutputStream(1024);
            buffer[0] = new byte[1024];

            while ((n = in.read(buffer[0])) > 0) {
                out.write(buffer[0], 0, n);
            }

            in.close();
            out.flush();
            buffer[0] = out.toByteArray();
        } catch (IOException ioe) {
            return null;
        }

        if (buffer[0] == null) {
            return null;
        }

        if (buffer[0].length == 0) {
            return null;
        }

        return new ImageIcon(buffer[0]);
    }
}