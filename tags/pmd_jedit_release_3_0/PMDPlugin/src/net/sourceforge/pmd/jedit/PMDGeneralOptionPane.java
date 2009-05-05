/*
* User: tom
* Date: Jul 8, 2002
* Time: 4:29:19 PM
*/
package net.sourceforge.pmd.jedit;


import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class PMDGeneralOptionPane extends AbstractOptionPane implements OptionPane {


    private JCheckBox chkRunPMDOnSave, chkShowProgressBar, chkIgnoreLiterals, chkPrintRule;

    JTextField txtMinTileSize;

    JTextField txtCustomRules;

    JComboBox comboRenderer;

    JComboBox javaVersionBox;

    public PMDGeneralOptionPane() {
        super( PMDJEditPlugin.NAME );
    }

    public void _init() {
        removeAll();

        setLayout( new FlowLayout( FlowLayout.LEADING ) );


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

        comboRenderer = new JComboBox( new String[] { "None", "Text", "Html", "XML", "CSV"
                                                    }
                                     );
        comboRenderer.setSelectedItem( jEdit.getProperty( PMDJEditPlugin.RENDERER ) );
        JLabel lblRenderer = new JLabel( "Export Output as " );

        pnlTileSize.add( lblRenderer );
        pnlTileSize.add( comboRenderer );

        int stidx = jEdit.getIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY, 1 );
        javaVersionBox.setSelectedIndex( stidx );

        pnlTileSize.add( chkShowProgressBar );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add(new JLabel("PMD General Options"), BorderLayout.NORTH);

        pnlSouth.add( javaVersionBox );
        pnlSouth.add( chkRunPMDOnSave );
        pnlSouth.add( chkIgnoreLiterals );
        pnlSouth.add( chkPrintRule );
        pnlSouth.add( pnlTileSize );
        mainPanel.add( pnlSouth, BorderLayout.SOUTH );
        addComponent( mainPanel );
    }

    public void _save() {

        jEdit.setIntegerProperty( PMDJEditPlugin.JAVA_VERSION_PROPERTY,
                javaVersionBox.getSelectedIndex() );
        jEdit.setIntegerProperty( PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY,
                ( txtMinTileSize.getText().length() == 0 ) ? 100 : Integer
                .parseInt( txtMinTileSize.getText() ) );

        // If the user has checked "Run PMD on save", set the plugin to load on jEdit
        // start up so that the "run on save" feature works right away.  Unchecking
        // "Run PMD on save" sets the "activate" property back to "defer", which is
        // the default.
        boolean on_save = chkRunPMDOnSave.isSelected();
        jEdit.setBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE, on_save );
        jEdit.setProperty( "plugin.net.sourceforge.pmd.jedit.PMDJEditPlugin.activate", on_save ? "startup" : "defer" );

        jEdit.setBooleanProperty( PMDJEditPlugin.IGNORE_LITERALS, chkIgnoreLiterals.isSelected() );
        jEdit.setProperty( PMDJEditPlugin.RENDERER, ( String ) comboRenderer.getSelectedItem() );
        jEdit.setBooleanProperty( PMDJEditPlugin.SHOW_PROGRESS, chkShowProgressBar
                .isSelected() );
        jEdit.setBooleanProperty( PMDJEditPlugin.PRINT_RULE, chkPrintRule.isSelected() );
    }

}