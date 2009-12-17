/*
* User: tom
* Date: Jul 8, 2002
* Time: 4:29:19 PM
*/
package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.jedit.checkboxtree.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.StringList;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.List;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleSetWriter;
import java.io.*;

/*
    TODO: make custom rules a separate panel.
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

    static final String USE_DEFAULT_RULES_KEY = "pmd.use-default-rules";


    public PMDRulesOptionPane() {
        super( PMDJEditPlugin.NAME );
        try {
            rules = new SelectedRules();
        }
        catch ( RuleSetNotFoundException rsne ) {
            rsne.printStackTrace();
        }
    }

    public void _init() {
        removeAll();

        setLayout( new KappaLayout() );

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
        }

        useDefaultRules.addActionListener(
            new ActionListener() {
                public void actionPerformed( final ActionEvent ae ) {
                    SwingUtilities.invokeLater( new Runnable() {
                                public void run() {
                                    if ( ( ( JCheckBox ) ae.getSource() ).isSelected() ) {
                                        rules.loadGoodRulesTree();
                                    }
                                    else {
                                        rules.loadTree();
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

        // Custom Rule Panel Definition.
        /// TODO: make custom rules a separate panel
        /*
        JPanel pnlCustomRules = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        pnlCustomRules.add( new JLabel( "Path to custom rules.xml files(separated by comma)" ) );
        pnlCustomRules.add( ( txtCustomRules = new JTextField( jEdit.getProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, "" ), 30 ) ) );

        rulesPanel.add( pnlCustomRules, BorderLayout.SOUTH );
        */

        exampleLabel = new JLabel( jEdit.getProperty( "net.sf.pmd.Example", "Example" ) );
        JScrollPane example_pane = new JScrollPane( exampleTextArea );

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
                            System.getProperty("user.home") + "/ruleset.xml",
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
                        catch(Exception e) {
                            JOptionPane.showMessageDialog(GUIUtilities.getParentDialog( PMDRulesOptionPane.this ), "Error saving ruleset:\n" + e.getMessage(), "Error Saving Ruleset", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        );


        JLabel more_info_label = new JLabel( jEdit.getProperty( "net.sf.pmd.Please_see_http>//pmd.sf.net/_for_more_information", "Please see http://pmd.sf.net/ for more information" ) );

        setBorder( BorderFactory.createEmptyBorder( 12, 11, 11, 12 ) );
        add( "0, 0,  1, 1,  W, w,  3", rules_label );
        add( "0, 1,  1, 1,  W, w,  3", useDefaultRules );
        add( "0, 2,  1, 10,  0, w, 3", rules_pane );
        add( "0, 12, 1, 1,  W, w,  3", exportButton );
        add( "0, 13, 1, 1,  W, w,  3", exampleLabel );
        add( "0, 14, 1, 6,  0, wh, 3", example_pane );
        add( "0, 20, 1, 1,  W, w,  3", more_info_label );
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
                        String description = ( ( RuleNode ) userObject ).getRule().getDescription();
                        description = description.trim();
                        List<String> examples = ( ( RuleNode ) userObject ).getRule().getExamples();
                        exampleTextArea.setLineWrap( false );
                        exampleTextArea.setWrapStyleWord( false );
                        exampleTextArea.setText( description + StringList.join( examples, "\n---------\n" ) );
                        exampleTextArea.setCaretPosition( 0 );
                    }
                    else if ( userObject instanceof RuleSetNode ) {
                        changeExampleLabel( jEdit.getProperty( "net.sf.pmd.Description", "Description" ) );
                        String description = ( ( RuleSetNode ) userObject ).getRuleSet().getDescription();
                        description = description.trim();
                        description = description.replaceAll( "\n", " " );
                        exampleTextArea.setLineWrap( true );
                        exampleTextArea.setWrapStyleWord( true );
                        exampleTextArea.setText( description );
                        exampleTextArea.setCaretPosition( 0 );
                    }
                }
            }
        }
    }

    private void changeExampleLabel( final String text ) {
        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        exampleLabel.setText( text );
                        exampleLabel.repaint();
                    }
                }
                                  );
    }
}