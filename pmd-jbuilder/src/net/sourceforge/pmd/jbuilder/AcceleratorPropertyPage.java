/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  net.sourceforge.pmd.jbuilder;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.borland.primetime.help.*;
import com.borland.primetime.ide.*;
import com.borland.primetime.properties.*;
import com.borland.jbcl.layout.*;
import java.io.InputStream;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSet;



public class AcceleratorPropertyPage extends PropertyPage {
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JComboBox jComboBox1 = new JComboBox();
    private JPanel jPanel2 = new JPanel();
    private FlowLayout flowLayout1 = new FlowLayout();
    private JTextField jTextField2 = new JTextField();
    private JTextField jTextField3 = new JTextField();
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private JPanel jPanel3 = new JPanel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private int[][]keys = new int[2][2];
    private JTextField jTextField1 = new JTextField();
    private JLabel jLabel4 = new JLabel();
    private JPanel jPanel4 = new JPanel();
    private JCheckBox jCheckBox1 = new JCheckBox();//data structure to hold keycode and modifier info for 2 distinct actions

    /**
     * Constuctor
     */
    public AcceleratorPropertyPage () {
        try {
            jbInit();
            init2();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Initialize the interface components
     * @exception Exception thows any exceptions that occur
     */
    protected void jbInit () throws Exception {
        this.setLayout(verticalFlowLayout1);
        jLabel1.setText("Select the Action");
        jPanel2.setLayout(flowLayout1);
        jTextField2.setEnabled(false);
        jTextField2.setPreferredSize(new Dimension(60, 21));
        jTextField2.setEditable(false);
        jTextField3.setEnabled(false);
        jTextField3.setPreferredSize(new Dimension(60, 21));
        jTextField3.setEditable(false);
        jLabel2.setPreferredSize(new Dimension(60, 17));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Mod");
        jLabel3.setPreferredSize(new Dimension(60, 17));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("Key");
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                jComboBox1_itemStateChanged(e);
            }
        });
        jLabel4.setText("enter");
        jTextField1.setPreferredSize(new Dimension(60, 21));
        jCheckBox1.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox1.setText("Enabled");
        this.add(jPanel1, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jComboBox1, null);
        this.add(jPanel3, null);
        jPanel3.add(jLabel2, null);
        jPanel3.add(jLabel3, null);
        jPanel3.add(jLabel4, null);
        this.add(jPanel2, null);
        jPanel2.add(jTextField3, null);
        jPanel2.add(jTextField2, null);
        jPanel2.add(jTextField1, null);
        this.add(jPanel4, null);
        jPanel4.add(jCheckBox1, null);
    }

    private void initKeys() {
        keys[0][0] = AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.getInteger();
        keys[0][1] = AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.getInteger();
        keys[1][0] = AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.getInteger();
        keys[1][1] = AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.getInteger();

    }

    /**
     * non-Jbuilder specific initialization stuff
     */
    private void init2() {
        //initialize the keys data structure
        initKeys();

        //initialize the combo box with it's values
        jComboBox1.addItem("Check File");  //item 0
        jComboBox1.addItem("Check Project");  //item 1

        //initialize the checkbox
        jCheckBox1.setSelected(AcceleratorPropertyGroup.PROP_KEYS_ENABLED.getBoolean());

        //initialize the text fields and register the key listener
        int selectedItem = jComboBox1.getSelectedIndex();
        jTextField2.setText(KeyEvent.getKeyText(keys[selectedItem][0]));
        jTextField3.setText(KeyEvent.getKeyModifiersText(keys[selectedItem][1]));
        jTextField1.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e)
             {
                 int item = jComboBox1.getSelectedIndex();
                 keys[item][0] = e.getKeyCode();
                 keys[item][1] = e.getModifiers();
                 if(e.isActionKey())
                 {
                     jTextField2.setText(KeyEvent.getKeyText(keys[item][0]));
                     jTextField3.setText(KeyEvent.getKeyModifiersText(keys[item][1]));
                     jTextField1.setText("");

                 }
             }

             public void keyTyped(KeyEvent e)
             {
                 int item = jComboBox1.getSelectedIndex();
                 jTextField2.setText(KeyEvent.getKeyText(keys[item][0]));
                 jTextField3.setText(KeyEvent.getKeyModifiersText(keys[item][1]));
                 jTextField1.setText("");
             }

        });
    }

    public void writeProperties () {
        //we  need to tell the PMDOpenbTool to clear it's current key bindings before we save the new ones
        PMDOpenTool.clearShortCuts();

        //now we can save the new key bindings to the global properties
        AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.setInteger(keys[0][0]);
        AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.setInteger(keys[0][1]);
        AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.setInteger(keys[1][0]);
        AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.setInteger(keys[1][1]);
        AcceleratorPropertyGroup.PROP_KEYS_ENABLED.setBoolean(jCheckBox1.isSelected());

        //now we can tell PMDOpenTool to recreate the bindings based on the new global values if they are enabled
        if (jCheckBox1.isSelected()) {
            PMDOpenTool.registerShortCuts();
        }

    }

    /**
     * get the Help TOpic
     * @return help topic
     */
    public HelpTopic getHelpTopic () {
        return null;
    }

    /**
     * Called by JBuilder to setup the initial property settings.
     */
    public void readProperties () {}

    void jComboBox1_itemStateChanged(ItemEvent e) {
        int selectedItem = jComboBox1.getSelectedIndex();
        jTextField2.setText(KeyEvent.getKeyText(keys[selectedItem][0]));
        jTextField3.setText(KeyEvent.getKeyModifiersText(keys[selectedItem][1]));

    }

}


