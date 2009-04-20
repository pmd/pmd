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
import org.gjt.sp.util.StringList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.List;

public class PMDRulesOptionPane extends AbstractOptionPane implements OptionPane {

    SelectedRules rules;

    JTextArea exampleTextArea = new JTextArea( 15, 50 );

    JTextField txtCustomRules;

    CheckboxTree tree;


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

        setLayout( new FlowLayout( FlowLayout.LEADING ) );

        JPanel rulesPanel = new JPanel( new BorderLayout() );
        rulesPanel.add( new JLabel( "Please see http://pmd.sf.net/ for more information" ), BorderLayout.NORTH );
        rulesPanel.setBorder( BorderFactory.createTitledBorder( "Rules" ) );

        // use a checkbox tree for displaying the rules.  This lets the rules be
        // grouped by ruleset, and makes it very easy to select an entire set of
        // rules with a single click. The tree is only 2 levels
        // deep, the first level is the ruleset level, the second level contains the
        // individual rules.  Using the PROPAGATE_PRESERVING_UNCHECK checking mode
        // means the ruleset will be checked if one or more of the rules it contains
        // is checked.
        if ( rules == null ) {
            JOptionPane.showMessageDialog( null, "Error loading rules. Check any custom rulesets for errors.", "Error Loading Rules", JOptionPane.ERROR_MESSAGE );
        }
        else {
            tree = new CheckboxTree( rules.getRoot() );
            tree.getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
            tree.setCheckingPaths( rules.getCheckingModel().getCheckingPaths() );
            tree.setRootVisible( false );
            tree.addMouseMotionListener( new MyMouseMotionAdapter() );
            rulesPanel.add( new JScrollPane( tree ), BorderLayout.CENTER );
        }

        // Custom Rule Panel Definition.
        JPanel pnlCustomRules = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        pnlCustomRules.add( new JLabel( "Path to custom rules.xml files(seperated by comma)" ) );
        pnlCustomRules.add( ( txtCustomRules = new JTextField( jEdit.getProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, "" ), 30 ) ) );

        rulesPanel.add( pnlCustomRules, BorderLayout.SOUTH );

        JPanel textPanel = new JPanel( new BorderLayout() );
        textPanel.setBorder( BorderFactory.createTitledBorder( "Example" ) );
        textPanel.add( new JScrollPane( exampleTextArea ), BorderLayout.CENTER );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( rulesPanel, BorderLayout.CENTER );
        mainPanel.add( textPanel, BorderLayout.SOUTH );

        addComponent( mainPanel );
    }

    public void _save() {
        if ( rules != null ) {
            rules.save( tree.getCheckingModel() );
        }

        if ( txtCustomRules != null ) {
            jEdit.setProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, txtCustomRules
                    .getText() );
        }
    }

    private class MyMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
        public void mouseMoved( MouseEvent event ) {
            TreePath path = tree.getPathForLocation( event.getX(), event.getY() );
            if ( path != null ) {
                DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
                if ( node != null ) {
                    Object userObject = node.getUserObject();
                    if ( userObject instanceof RuleNode ) {
                        List<String> examples = ( ( RuleNode ) userObject ).getRule().getExamples();
                        exampleTextArea.setLineWrap( false );
                        exampleTextArea.setWrapStyleWord( false );
                        exampleTextArea.setText( StringList.join( examples, "\n---------\n" ) );
                        exampleTextArea.setCaretPosition( 0 );
                    }
                    else if ( userObject instanceof RuleSetNode ) {
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
}