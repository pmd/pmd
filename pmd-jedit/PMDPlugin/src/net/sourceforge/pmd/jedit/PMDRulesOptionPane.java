/*
* User: tom
* Date: Jul 8, 2002
* Time: 4:29:19 PM
*/
package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.jedit.checkboxtree.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.StringList;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.awt.Dimension;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleSetWriter;
import java.io.*;

import common.gui.pathbuilder.PathBuilder;
import common.gui.pathbuilder.PathBuilderDialog;
import ise.java.awt.KappaLayout;

/*
    TODO: put strings in properties file
    QUESTION: would it be possible to run the examples through the Beauty plugin
    and the Code2Html plugin to format and colorize the examples per the users preferences?
    DONE: make custom rules a separate panel. -- added custom rules dialog.
    DONE: add ability to export current ruleset.
*/
public class PMDRulesOptionPane extends AbstractOptionPane implements OptionPane {

    SelectedRules rules;

    JTextArea exampleTextArea = new JTextArea( 12, 60 );
    JTextField txtCustomRules;
    CheckboxTree tree;
    JCheckBox useDefaultRules;
    JLabel exampleLabel;
    JButton exportButton;
    JButton customRulesButton;
    
    
    static final String USE_DEFAULT_RULES_KEY = "pmd.use-default-rules";


    public PMDRulesOptionPane() {
        super( PMDJEditPlugin.NAME );
        rules = new SelectedRules();
    }

    public void _init() {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JPanel panel = new JPanel(new KappaLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        JLabel title = new JLabel("<html><b>" + jEdit.getProperty("options.pmd.rules.label", "PMD Rules"));
        
        panel.add("0, 0, 1, 1, W, w, 3", title);
        panel.add("0, 1, 1, 1, W, w, 3", getRulesPanel());
        
        add(panel);
    }
    
    private JPanel getRulesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout( new KappaLayout() );

        JLabel rules_label = new JLabel( jEdit.getProperty( "net.sf.pmd.Rules", "Rules" ) );
        useDefaultRules = new JCheckBox( jEdit.getProperty( "net.sf.pmd.Select_default_rules", "Select default rules" ) );
        useDefaultRules.setSelected( jEdit.getBooleanProperty( USE_DEFAULT_RULES_KEY, false ) );
        
        // use a checkbox tree for displaying the rules.  This lets the rules be
        // grouped by ruleset, and makes it very easy to select an entire set of
        // rules with a single click. The tree is only 2 levels
        // deep, the first level is the ruleset level, the second level contains the
        // individual rules.  Using the PROPAGATE_PRESERVING_UNCHECK checking mode
        // means the ruleset will be checked if one or more of the rules it contains
        // is checked.
        JScrollPane rules_pane = null;
        if ( rules == null ) {
            JOptionPane.showMessageDialog( null,
                    jEdit.getProperty( "net.sf.pmd.Error_loading_rules._Check_any_custom_rulesets_for_errors.", "Error loading rules. Check any custom rulesets for errors." ),
                    jEdit.getProperty( "net.sf.pmd.Error_Loading_Rules", "Error Loading Rules" ),
                    JOptionPane.ERROR_MESSAGE );
        }
        else {
            if ( jEdit.getBooleanProperty( USE_DEFAULT_RULES_KEY, false ) ) {
                rules.loadGoodRulesTree();
            }
            tree = new CheckboxTree( rules.getRoot() );
            tree.getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
            tree.setCheckingPaths( rules.getCheckingModel().getCheckingPaths() );
            tree.setRootVisible( false );
            tree.addMouseMotionListener( new MyMouseMotionAdapter() );
            rules_pane = new JScrollPane( tree );
            rules_pane.setMaximumSize( new Dimension( 500, 200 ) );
            rules_pane.setPreferredSize( new Dimension( 500, 200 ) );
        }

        useDefaultRules.addActionListener(
            new ActionListener() {
                public void actionPerformed( final ActionEvent ae ) {
                    SwingUtilities.invokeLater( new Runnable() {
                                public void run() {
                                    if ( ( ( JCheckBox ) ae.getSource() ).isSelected() ) {
                                        rules.loadGoodRulesTree();
                                        customRulesButton.setEnabled( false );
                                    }
                                    else {
                                        rules.loadTree();
                                        customRulesButton.setEnabled( true );
                                    }
                                    tree.setModel( rules.getTreeModel() );
                                    tree.getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
                                    tree.setCheckingPaths( rules.getCheckingModel().getCheckingPaths() );
                                    tree.invalidate();
                                    tree.validate();
                                }
                            }
                                              );
                }
            }
        );

        exampleLabel = new JLabel( jEdit.getProperty( "net.sf.pmd.Example", "Example" ) );
        JScrollPane example_pane = new JScrollPane( exampleTextArea );
        example_pane.setMaximumSize( new Dimension( 500, 200 ) );
        example_pane.setPreferredSize( new Dimension( 500, 200 ) );

        exportButton = new JButton( "Export this ruleset" );
        exportButton.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    RuleSets rulesets = rules.getSelectedRules();
                    RuleSet ruleset = new RuleSet();
                    for ( RuleSet rs : rulesets.getAllRuleSets() ) {
                        ruleset.addRuleSet( rs );
                    }

                    // file chooser
                    VFSFileChooserDialog chooser = new VFSFileChooserDialog( GUIUtilities.getParentDialog( PMDRulesOptionPane.this ),
                            jEdit.getActiveView(),
                            System.getProperty( "user.home" ) + "/ruleset.xml",
                            VFSBrowser.SAVE_DIALOG,
                            false,
                            false );

                    chooser.setTitle( "Export Ruleset" );
                    chooser.setVisible( true );

                    if ( chooser.getSelectedFiles() != null ) {
                        try {
                            String outfile = chooser.getSelectedFiles() [ 0 ];
                            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( outfile ) );
                            RuleSetWriter writer = new RuleSetWriter( outputStream );
                            writer.write( ruleset );
                            writer.close();
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( GUIUtilities.getParentDialog( PMDRulesOptionPane.this ), "Error saving ruleset:\n" + e.getMessage(), "Error Saving Ruleset", JOptionPane.ERROR_MESSAGE );
                        }
                    }
                }
            }
        );


        JLabel more_info_label = new JLabel( jEdit.getProperty( "net.sf.pmd.Please_see_http>//pmd.sf.net/_for_more_information", "Please see http://pmd.sf.net/ for more information" ) );

        customRulesButton = new JButton( "Custom Rules" );
        customRulesButton.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    customRulesDialog();
                }
            }
        );

        panel.setBorder( BorderFactory.createEmptyBorder( 12, 11, 11, 12 ) );
        panel.add( "0, 0,  2, 1,  W, w,  3", rules_label );
        panel.add( "0, 1,  2, 1,  W, w,  3", useDefaultRules );
        panel.add( "0, 2,  2, 10, 0, w,  3", rules_pane );
        panel.add( "0, 12, 1, 1,  0, w,  3", exportButton );
        panel.add( "1, 12, 1, 1,  0, w,  3", customRulesButton );
        panel.add( "0, 13, 2, 1,  W, w,  3", exampleLabel );
        panel.add( "0, 14, 2, 6,  0, wh, 3", example_pane );
        panel.add( "0, 20, 2, 1,  W, w,  3", more_info_label );
        return panel;
    }
    
    public void _save() {
        if ( rules != null ) {
            rules.save( tree.getCheckingModel() );
        }

        if ( txtCustomRules != null ) {
            jEdit.setProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, txtCustomRules
                    .getText() );
        }
        jEdit.setBooleanProperty( USE_DEFAULT_RULES_KEY, useDefaultRules.isSelected() );
    }

    private class MyMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
        public void mouseMoved( MouseEvent event ) {
            TreePath path = tree.getPathForLocation( event.getX(), event.getY() );
            if ( path != null ) {
                DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
                if ( node != null ) {
                    Object userObject = node.getUserObject();
                    if ( userObject instanceof RuleNode ) {
                        changeExampleLabel( jEdit.getProperty( "net.sf.pmd.Example", "Example" ) );
                        Rule rule = (( RuleNode ) userObject).getRule();
                        String header = getRuleExampleHeader(rule);
                        List<String> examples = ( ( RuleNode ) userObject ).getRule().getExamples();
                        exampleTextArea.setLineWrap( true );
                        exampleTextArea.setWrapStyleWord( true );
                        exampleTextArea.setText( header + StringList.join( examples, "\n---------\n" ) );
                        exampleTextArea.setCaretPosition( 0 );
                    }
                    else if ( userObject instanceof RuleSetNode ) {
                        changeExampleLabel( jEdit.getProperty( "net.sf.pmd.Description", "Description" ) );
                        String description = ( ( RuleSetNode ) userObject ).getRuleSet().getDescription();
                        description = cleanUpDescription( description );
                        exampleTextArea.setLineWrap( true );
                        exampleTextArea.setWrapStyleWord( true );
                        exampleTextArea.setText( description );
                        exampleTextArea.setCaretPosition( 0 );
                    }
                }
            }
        }
    }

    private String getRuleExampleHeader( Rule rule ) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Name: " ).append(rule.getName());
        sb.append("\nLanguage: ").append(rule.getLanguage().getName());
        sb.append("\nDescription: ").append(cleanUpDescription(rule.getDescription()));
        sb.append("\nError Message: ").append(rule.getMessage());
        sb.append("\nPriority: ").append(rule.getPriority().toString()).append('\n');
        return sb.toString();
    }

    private String cleanUpDescription( String desc ) {
        desc = desc.replaceAll( "\\s+", " " );
        desc = desc.trim();
        return desc;
    }

    private void changeExampleLabel( final String text ) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    exampleLabel.setText( text );
                    exampleLabel.repaint();
                }
            }
        );
    }

    private void customRulesDialog() {
        PathBuilderDialog dialog = new PathBuilderDialog( jEdit.getActiveView(), "Choose Custom Rulesets", "Custom Ruleset Files" );
        PathBuilder pathBuilder = dialog.getPathBuilder();
        pathBuilder.setAddButtonText( "Add Ruleset" );
        pathBuilder.setRemoveButtonText( "Remove Ruleset" );
        pathBuilder.setFileDialogTitle( "Select Ruleset File" );
        pathBuilder.setFileFilter( new RulesetFileFilter() );
        String paths = jEdit.getProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, "" );
        paths = paths.replaceAll( ",", File.pathSeparator );
        pathBuilder.setPath( paths );

        dialog.setVisible( true );

        if ( dialog.getResult() ) {
            paths = pathBuilder.getPath();
            paths = paths.replaceAll( File.pathSeparator, "," );
            jEdit.setProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, paths );

            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        try {
                            rules = new SelectedRules();
                            tree.setModel( rules.getTreeModel() );
                            tree.getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
                            tree.setCheckingPaths( rules.getCheckingModel().getCheckingPaths() );
                            tree.invalidate();
                            tree.validate();
                            tree.repaint();
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }
            );
        }
    }

    public class RulesetFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f != null && ( f.isDirectory() || f.getName().endsWith( ".xml" ) );
        }
        public String getDescription() {
            return "PMD Ruleset Files";
        }
    }
}