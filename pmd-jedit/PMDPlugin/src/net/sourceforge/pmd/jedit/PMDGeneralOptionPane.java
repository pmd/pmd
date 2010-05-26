/*
* User: tom
* Date: Jul 8, 2002
* Time: 4:29:19 PM
*/
package net.sourceforge.pmd.jedit;


import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ise.java.awt.KappaLayout;

public class PMDGeneralOptionPane extends AbstractOptionPane implements OptionPane {


    private JCheckBox chkRunPMDOnSave, chkShowProgressBar, chkPrintRule;

    JComboBox javaVersionBox;

    public PMDGeneralOptionPane() {
        super( PMDJEditPlugin.NAME );
    }

    public void _init() {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JPanel panel = new JPanel(new KappaLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        JLabel title = new JLabel("<html><b>" + jEdit.getProperty("options.pmd.general.label", "PMD Settings"));

        javaVersionBox = new JComboBox( PMDJEditPlugin.sourceTypes );
        int stidx = jEdit.getIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY, 1 );
        javaVersionBox.setSelectedIndex( stidx );

        chkRunPMDOnSave = new JCheckBox( jEdit.getProperty("net.sf.pmd.Run_PMD_on_Save", "Run PMD on Save"), jEdit.getBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE ) );
        chkPrintRule = new JCheckBox( jEdit.getProperty("net.sf.pmd.Print_Rulename_in_ErrorList", "Print Rulename in ErrorList"), jEdit.getBooleanProperty( PMDJEditPlugin.PRINT_RULE ) );
        chkShowProgressBar = new JCheckBox( jEdit.getProperty("net.sf.pmd.Show_PMD_Progress_Bar", "Show PMD Progress Bar"), jEdit.getBooleanProperty( PMDJEditPlugin.SHOW_PROGRESS ) );
        
        panel.add("0, 0, 1, 1, W, w, 3", title);
        panel.add("0, 1, 1, 1, W, w, 3", javaVersionBox);
        panel.add("0, 2, 1, 1, W, w, 3", chkRunPMDOnSave);
        panel.add("0, 3, 1, 1, W, w, 3", chkPrintRule);
        panel.add("0, 4, 1, 1, W, w, 3", chkShowProgressBar);

        add( panel );
    }

    public void _save() {

        jEdit.setIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY,
                javaVersionBox.getSelectedIndex() );

        // If the user has checked "Run PMD on save", set the plugin to load on jEdit
        // start up so that the "run on save" feature works right away.  Unchecking
        // "Run PMD on save" sets the "activate" property back to "defer", which is
        // the default.
        boolean on_save = chkRunPMDOnSave.isSelected();
        jEdit.setBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE, on_save );
        jEdit.setProperty( "plugin.net.sourceforge.pmd.jedit.PMDJEditPlugin.activate", on_save ? "startup" : "defer" );

        jEdit.setBooleanProperty( PMDJEditPlugin.PRINT_RULE, chkPrintRule.isSelected() );
        jEdit.setBooleanProperty( PMDJEditPlugin.SHOW_PROGRESS, chkShowProgressBar.isSelected() );
    }

}