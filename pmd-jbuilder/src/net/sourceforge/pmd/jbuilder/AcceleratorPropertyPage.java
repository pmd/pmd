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
    int keycode;
    int modifiers;
    private FlowLayout flowLayout1 = new FlowLayout();
    private JTextField jTextField2 = new JTextField();
    private JTextField jTextField3 = new JTextField();
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private JPanel jPanel3 = new JPanel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();

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
        jTextField2.setPreferredSize(new Dimension(60, 21));
        jTextField3.setEnabled(false);
        jTextField3.setPreferredSize(new Dimension(60, 21));
        jTextField3.setEditable(false);
        jLabel2.setPreferredSize(new Dimension(60, 17));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Mod");
        jLabel3.setPreferredSize(new Dimension(60, 17));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("Key");
        this.add(jPanel1, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jComboBox1, null);
        this.add(jPanel3, null);
        jPanel3.add(jLabel2, null);
        jPanel3.add(jLabel3, null);
        this.add(jPanel2, null);
        jPanel2.add(jTextField3, null);
        jPanel2.add(jTextField2, null);
    }


    /**
     * non-Jbuilder specific initialization stuff
     */
    private void init2() {
        jComboBox1.addItem("Check File");
        jComboBox1.addItem("Check Project");
        jTextField2.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e)
             {
                 keycode = e.getKeyCode();
                 modifiers = e.getModifiers();
                 if(e.isActionKey())
                 {
                     jTextField2.setText(KeyEvent.getKeyText(keycode));
                     jTextField3.setText(KeyEvent.getKeyModifiersText(modifiers));
                     //jTextField2.setText("");

                 }
             }

             public void keyTyped(KeyEvent e)
             {
                 jTextField2.setText(KeyEvent.getKeyText(keycode));
                 jTextField3.setText(KeyEvent.getKeyModifiersText(modifiers));
                 //jTextField2.setText("");
             }

        });
    }

    public void writeProperties () {
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

}


