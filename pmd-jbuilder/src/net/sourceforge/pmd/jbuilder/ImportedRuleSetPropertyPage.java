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



public class ImportedRuleSetPropertyPage extends PropertyPage {
    private MessageCategory msgCat = new MessageCategory("PMD Import Status");
    private DefaultListModel dlmImportedRuleSets = new DefaultListModel();
    private JScrollPane spImportedRuleSets = new JScrollPane();
    private JList listImportedRuleSets = new JList();
    private JPanel jPanel1 = new JPanel();
    private JTextField tfRuleSetFileName = new JTextField();
    private JButton btnImportRuleSet = new JButton();
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private BoxLayout2 boxLayout21 = new BoxLayout2();
    private JPanel jPanel2 = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JButton btnRemoveRuleSet = new JButton();
    private Border border1;
    private TitledBorder titledBorder1;

    /**
     * Constuctor
     */
    public ImportedRuleSetPropertyPage () {
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
        border1 = BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178));
        titledBorder1 = new TitledBorder(border1,"Imported RuleSets");
        this.setLayout(verticalFlowLayout1);
        jPanel1.setLayout(boxLayout21);
        btnImportRuleSet.setMaximumSize(new Dimension(175, 27));
        btnImportRuleSet.setMinimumSize(new Dimension(175, 27));
        btnImportRuleSet.setPreferredSize(new Dimension(175, 27));
        btnImportRuleSet.setText("Import RuleSet");
        btnImportRuleSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnImportRuleSet_actionPerformed(e);
            }
        });
        listImportedRuleSets.setBorder(titledBorder1);
        jPanel2.setLayout(borderLayout1);
        btnRemoveRuleSet.setText("Remove RuleSet");
        btnRemoveRuleSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnRemoveRuleSet_actionPerformed(e);
            }
        });
        spImportedRuleSets.setPreferredSize(new Dimension(260, 250));
        this.add(spImportedRuleSets, null);
        this.add(jPanel1, null);
        jPanel1.add(btnImportRuleSet, null);
        jPanel1.add(tfRuleSetFileName, null);
        this.add(jPanel2, null);
        jPanel2.add(btnRemoveRuleSet, BorderLayout.WEST);
        spImportedRuleSets.getViewport().add(listImportedRuleSets, null);
    }


    private void init2() {
        listImportedRuleSets.setModel(dlmImportedRuleSets);
        //update the list with the
        for (Iterator iter = ImportedRuleSetPropertyGroup.currentInstance.getRuleSets().iterator(); iter.hasNext(); ) {
            RuleSet rs = (RuleSet)iter.next();
            dlmImportedRuleSets.addElement(rs.getName());
        }
    }

    /**
     * Called by JBuilder when the user selects OK from the proeprties dialog
     * We use this method to save the values of the properties based upon what list
     * they ended up in when the user was finished
     */
    public void writeProperties () {
    }

    /**
     * get the Help TOpic
     * @return help topic
     */
    public HelpTopic getHelpTopic () {
        return new ZipHelpTopic(
         null,
         getClass().getResource("/import-ruleset-props.html").toString());

    }

    /**
     * Called by JBuilder to setup the initial property settings.
     * We don't use this since we to all the property setup in the jbInit() method.
     */
    public void readProperties () {}

    void btnImportRuleSet_actionPerformed(ActionEvent e) {
        String fileName = tfRuleSetFileName.getText();
        if (fileName != null && !fileName.trim().equals("")) {
            if (!fileName.toLowerCase().startsWith("rulesets/")) {
                fileName = "rulesets/"+fileName;
            }
            if (!fileName.toLowerCase().endsWith(".xml")) {
                fileName += ".xml";
            }
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
                RuleSetFactory rsf = new RuleSetFactory();
                RuleSet rs = rsf.createRuleSet(is);

                //if this rule set doesn't already exist in the imported list, then import it
                if (!dlmImportedRuleSets.contains(rs.getName())) {
                    dlmImportedRuleSets.addElement(rs.getName());
                    ImportedRuleSetPropertyGroup.currentInstance.addRuleSet(fileName, rs);
                    tfRuleSetFileName.setText("");
                }
                else {
                    Message msg = new Message("Rule Set: " + fileName + " already exists");
                    msg.setForeground(Color.red);
                    Browser.getActiveBrowser().getMessageView().addMessage(msgCat, msg);
                }
            }
            catch (Exception ex) {
                Message msg = new Message("Error importing: " + fileName);
                msg.setForeground(Color.red);
                Browser.getActiveBrowser().getMessageView().addMessage(msgCat, msg);
            }
        }
    }

    void btnRemoveRuleSet_actionPerformed(ActionEvent e) {
        int index = listImportedRuleSets.getSelectedIndex();
        String ruleSetName = (String)dlmImportedRuleSets.elementAt(index);
        dlmImportedRuleSets.remove(index);
        ImportedRuleSetPropertyGroup.currentInstance.removeRuleSet(ruleSetName);

    }
}


