package net.sourceforge.pmd.jbuilder;

import com.borland.jbcl.layout.VerticalFlowLayout;
import com.borland.primetime.help.HelpTopic;
import com.borland.primetime.help.ZipHelpTopic;
import com.borland.primetime.properties.PropertyPage;

import javax.swing.*;
import java.awt.*;


/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class CPDPropertyPage extends PropertyPage {
    static CPDPropertyPage currentInstance = null;
    private JPanel jPanel1 = new JPanel();
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private JLabel jLabel1 = new JLabel();
    private JTextField jTextField1 = new JTextField();

    public CPDPropertyPage() {
        currentInstance = this;
        try {
            jbInit();
            init2();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This methiod is called by JBuilder when the user presses "OK" in the property dialog
     */
    public void writeProperties() {
        try {
            int minTokenCount = Integer.parseInt(jTextField1.getText());
            CPDPropertyGroup.PROP_MIN_TOKEN_COUNT.setInteger(minTokenCount);
        }
        catch (Exception ex) {

        }

    }

    /**
     * This methiod is called by JBuilder
     */
    public HelpTopic getHelpTopic() {
        return new ZipHelpTopic(
         null,
         getClass().getResource("/html/cpd-props.html").toString());
    }

    /**
     * This methiod is called by JBuilder
     */
    public void readProperties() {
    }

    /**
     * JBuilder-constructed initialization
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.setLayout(verticalFlowLayout1);
        jLabel1.setText("Minimum Token Count");
        jTextField1.setPreferredSize(new Dimension(40, 21));
        this.add(jPanel1, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jTextField1, null);
    }

    /**
     * additional intiialzation
     */
    private void init2() {
        jTextField1.setText(String.valueOf(CPDPropertyGroup.PROP_MIN_TOKEN_COUNT.getInteger()));
    }

}

