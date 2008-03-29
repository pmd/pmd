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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.List;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    SelectedRules rules;

    JTextArea exampleTextArea = new JTextArea( 15, 50 );

    private JCheckBox chkRunPMDOnSave, chkShowProgressBar, chkIgnoreLiterals, chkPrintRule;

    JTextField txtMinTileSize;

    JTextField txtCustomRules;

    JComboBox comboRenderer;

    JComboBox javaVersionBox;

    CheckboxTree tree;

    public PMDOptionPane() {
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
        tree = new CheckboxTree( rules.getRoot() );
        tree.getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_UNCHECK );
        tree.setCheckingPaths( rules.getCheckingModel().getCheckingPaths() );
        tree.setRootVisible( false );
        tree.addMouseMotionListener( new MyMouseMotionAdapter() );
        rulesPanel.add( new JScrollPane( tree ), BorderLayout.CENTER );

        // Custom Rule Panel Definition.
        JPanel pnlCustomRules = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        pnlCustomRules.add( new JLabel( "Path to custom rules.xml files(seperated by comma)" ) );
        pnlCustomRules.add( ( txtCustomRules = new JTextField( jEdit.getProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, "" ), 30 ) ) );

        rulesPanel.add( pnlCustomRules, BorderLayout.SOUTH );

        JPanel textPanel = new JPanel( new BorderLayout() );
        textPanel.setBorder( BorderFactory.createTitledBorder( "Example" ) );
        textPanel.add( new JScrollPane( exampleTextArea ), BorderLayout.CENTER );
        javaVersionBox = new JComboBox( PMDJEditPlugin.sourceTypes );

        chkRunPMDOnSave = new JCheckBox( "Run PMD on Save", jEdit.getBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE ) );
        chkShowProgressBar = new JCheckBox( "Show PMD Progress Bar", jEdit.getBooleanProperty( PMDJEditPlugin.SHOW_PROGRESS ) );
        chkIgnoreLiterals = new JCheckBox( "Ignore Literals & identifiers when detecting Duplicate Code", jEdit.getBooleanProperty( PMDJEditPlugin.IGNORE_LITERALS ) );
        chkPrintRule = new JCheckBox( "Print Rulename in ErrorList", jEdit.getBooleanProperty( PMDJEditPlugin.PRINT_RULE ) );

        JPanel pnlSouth = new JPanel( new GridLayout( 0, 1 ) );

        JPanel pnlTileSize = new JPanel();
        ( ( FlowLayout ) pnlTileSize.getLayout() ).setAlignment( FlowLayout.LEFT );
        JLabel lblMinTileSize = new JLabel( "Minimum Tile Size :" );
        txtMinTileSize = new JTextField( jEdit.getProperty( PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY, "100" ), 5 );
        pnlTileSize.add( lblMinTileSize );
        pnlTileSize.add( txtMinTileSize );

        comboRenderer = new JComboBox( new String[] { "None", "Text", "Html", "XML", "CSV" } );
        comboRenderer.setSelectedItem( jEdit.getProperty( PMDJEditPlugin.RENDERER ) );
        JLabel lblRenderer = new JLabel( "Export Output as " );

        pnlTileSize.add( lblRenderer );
        pnlTileSize.add( comboRenderer );

        int stidx = jEdit.getIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY, 1 );
        javaVersionBox.setSelectedIndex( stidx );

        pnlTileSize.add( chkShowProgressBar );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        mainPanel.add( rulesPanel, BorderLayout.NORTH );
        mainPanel.add( textPanel, BorderLayout.CENTER );

        pnlSouth.add( javaVersionBox );
        pnlSouth.add( chkRunPMDOnSave );
        pnlSouth.add( chkIgnoreLiterals );
        pnlSouth.add( chkPrintRule );
        pnlSouth.add( pnlTileSize );
        mainPanel.add( pnlSouth, BorderLayout.SOUTH );
        addComponent( mainPanel );
    }

    public void _save() {
        rules.save( tree.getCheckingModel() );

        jEdit.setIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY,
                javaVersionBox.getSelectedIndex() );
        jEdit.setIntegerProperty( PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY,
                ( txtMinTileSize.getText().length() == 0 ) ? 100 : Integer
                .parseInt( txtMinTileSize.getText() ) );
        jEdit.setBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE,
                ( chkRunPMDOnSave.isSelected() ) );
        jEdit.setBooleanProperty( PMDJEditPlugin.IGNORE_LITERALS,
                ( chkIgnoreLiterals.isSelected() ) );
        jEdit.setProperty( PMDJEditPlugin.RENDERER, ( String ) comboRenderer.getSelectedItem() );
        jEdit.setBooleanProperty( PMDJEditPlugin.SHOW_PROGRESS, chkShowProgressBar
                .isSelected() );
        jEdit.setBooleanProperty( PMDJEditPlugin.PRINT_RULE, chkPrintRule.isSelected() );

        if ( txtCustomRules != null ) {
            jEdit.setProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, txtCustomRules
                    .getText() );
        }
    }

    private class MyMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
        public void mouseMoved( MouseEvent e ) {
            TreePath path = tree.getPathForLocation( e.getX(), e.getY() );
            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
            if ( node != null ) {
                Object userObject = node.getUserObject();
                if ( userObject instanceof RuleNode ) {
                    List<String> examples = ( ( ( RuleNode ) userObject ).getRule() ).getExamples();
                    exampleTextArea.setText( StringList.join( examples, "\n---------\n" ) );
                    exampleTextArea.setCaretPosition( 0 );
                }
            }
        }
    }
}