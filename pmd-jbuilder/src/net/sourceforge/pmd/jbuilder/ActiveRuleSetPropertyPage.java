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

import com.borland.primetime.help.HelpTopic;
import com.borland.primetime.help.ZipHelpTopic;
import com.borland.primetime.properties.GlobalProperty;
import com.borland.primetime.properties.PropertyPage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;



public class ActiveRuleSetPropertyPage extends PropertyPage {
    private BorderLayout borderLayout1 = new BorderLayout();
    private JSplitPane jSplitPane1 = new JSplitPane();
    private Border border1;
    private TitledBorder titledBorder1;
    private Border border2;
    private TitledBorder titledBorder2;
    private DefaultListModel dlmAvailableRuleSets = new DefaultListModel();
    private DefaultListModel dlmSelectedRuleSets = new DefaultListModel();
    private JPanel jpAvailableRuleSets = new JPanel();
    private JScrollPane jspAvailableRuleSets = new JScrollPane();
    private JList jlistAvailableRuleSets = new JList();
    private JPanel jpSelectedRuleSets = new JPanel();
    private JList jlistSelectedRuleSets = new JList();
    private JScrollPane jspSelecedRuleSets = new JScrollPane();
    private BorderLayout borderLayout2 = new BorderLayout();
    private BorderLayout borderLayout3 = new BorderLayout();
    private JButton jbSelectRuleSets = new JButton();
    private JButton jbDeselectRuleSets = new JButton();
    private Border border3;
    private Border border4;
    static ActiveRuleSetPropertyPage currentInstance = null;

    /**
     * Constuctor
     */
    public ActiveRuleSetPropertyPage () {
        currentInstance = this;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void reinit() {
        dlmSelectedRuleSets.clear();
        dlmAvailableRuleSets.clear();
        initRuleSplitPanes();
        this.updateUI();
    }


    /**
     * Initialize the splitpanes that are used in this interface
     * The left pane contains the list of available rule sets while the right pane
     * contains the list of selected rule sets
     */
    private void initRuleSplitPanes () {
        //loop through the sets of rules and place them in the appropriate pane based upon their setting
        Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator();
        while (iter.hasNext()) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            ListEntry le = new ListEntry(rsp.getActiveRuleSet().getName(), rsp.getGlobalProperty());
            if (Boolean.valueOf(rsp.getGlobalProperty().getValue()).booleanValue()) {
                dlmSelectedRuleSets.addElement(le);
            }
            else {
                dlmAvailableRuleSets.addElement(le);
            }
        }
    }

    /**
     * Initialize the interface components
     * @exception Exception thows any exceptions that occur
     */
    protected void jbInit () throws Exception {
        border1 = BorderFactory.createEtchedBorder(Color.white, new Color(178, 178, 178));
        titledBorder1 = new TitledBorder(border1, "Available Rule Sets");
        border2 = BorderFactory.createEtchedBorder(Color.white, new Color(178, 178, 178));
        titledBorder2 = new TitledBorder(border2, "Selected Rule Sets");
        border3 = BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151));
        border4 = BorderFactory.createCompoundBorder(border3, titledBorder1);
        this.setLayout(borderLayout1);
        jlistAvailableRuleSets.setModel(dlmAvailableRuleSets);
        jlistAvailableRuleSets.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jlistAvailableRuleSets_mouseClicked(e);
            }
        });
        jlistSelectedRuleSets.setModel(dlmSelectedRuleSets);
        jlistSelectedRuleSets.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jlistSelectedRuleSets_mouseClicked(e);
            }
        });
        jpAvailableRuleSets.setLayout(borderLayout2);
        jpSelectedRuleSets.setLayout(borderLayout3);
        jbSelectRuleSets.setText("Select ===>>>");
        jbSelectRuleSets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed (ActionEvent e) {
                jbSelectRuleSets_actionPerformed(e);
            }
        });
        jbDeselectRuleSets.setText("<<<===Remove");
        jbDeselectRuleSets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed (ActionEvent e) {
                jbDeselectRuleSets_actionPerformed(e);
            }
        });
        jpAvailableRuleSets.setBorder(titledBorder1);
        jpSelectedRuleSets.setBorder(titledBorder2);
        this.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jpAvailableRuleSets, JSplitPane.LEFT);
        jSplitPane1.add(jpSelectedRuleSets, JSplitPane.RIGHT);
        jpAvailableRuleSets.add(jspAvailableRuleSets, BorderLayout.CENTER);
        jpSelectedRuleSets.add(jspSelecedRuleSets, BorderLayout.CENTER);
        jspSelecedRuleSets.getViewport().add(jlistSelectedRuleSets, null);
        jspAvailableRuleSets.getViewport().add(jlistAvailableRuleSets, null);
        jpAvailableRuleSets.add(jbSelectRuleSets, BorderLayout.SOUTH);
        jpSelectedRuleSets.add(jbDeselectRuleSets, BorderLayout.SOUTH);
        initRuleSplitPanes();
        jSplitPane1.setDividerLocation(200);
    }

    /**
     * Called by JBuilder when the user selects OK from the proeprties dialog
     * We use this method to save the values of the properties based upon what list
     * they ended up in when the user was finished
     */
    public void writeProperties () {
        //set the properties for the items items in the selected list to true
        for (Enumeration e = dlmSelectedRuleSets.elements(); e.hasMoreElements();) {
            ListEntry le = (ListEntry)e.nextElement();
            le.getProp().setValue("true");
        }
        //set the properties for the items items in the available list to false
        for (Enumeration e = dlmAvailableRuleSets.elements(); e.hasMoreElements();) {
            ListEntry le = (ListEntry)e.nextElement();
            le.getProp().setValue("false");
        }
    }

    /**
     * get the Help TOpic
     * @return help topic
     */
    public HelpTopic getHelpTopic () {
        return new ZipHelpTopic(
         null,
         getClass().getResource("/html/active-ruleset-props.html").toString());
    }

    /**
     * Called by JBuilder to setup the initial property settings.
     * We don't use this since we to all the property setup in the jbInit() method.
     */
    public void readProperties () {}

    /**
     * Called when the jbSelectRuleSets button is pressed
     * @param e action event
     */
    void jbSelectRuleSets_actionPerformed (ActionEvent e) {
        selectRules();
    }

    /**
     * Find any rules that are selected in the available list and move them to
     * the selected list
     */
    private void selectRules() {
        //get the selected elements in the available list and move to the selected list
        int selectedIndex = jlistAvailableRuleSets.getSelectedIndex();
        while (selectedIndex != -1) {
            ListEntry le = (ListEntry)dlmAvailableRuleSets.get(selectedIndex);
            dlmSelectedRuleSets.addElement(le);
            RuleSetProperty rsp = (RuleSetProperty)ActiveRuleSetPropertyGroup.currentInstance.ruleSets.get(le.getDisplayName());
            rsp.getGlobalProperty().setValue("true");   //set the global property value to true
            dlmAvailableRuleSets.remove(selectedIndex);
            selectedIndex = jlistAvailableRuleSets.getSelectedIndex();
        }
        jlistSelectedRuleSets.updateUI();
        jlistAvailableRuleSets.updateUI();
    }

    /**
     * Called when the jbDeselectRuleSets button is pressed
     * @param e action event
     */
    void jbDeselectRuleSets_actionPerformed (ActionEvent e) {
        deselectRules();
    }

    /**
     * Find any rules that are selected in the selected list and move them to the
     * available list
     */
    private void deselectRules() {
        //get the selected elements in the selected list and move to the available list
        int selectedIndex = jlistSelectedRuleSets.getSelectedIndex();
        while (selectedIndex != -1) {
            ListEntry le = (ListEntry)dlmSelectedRuleSets.get(selectedIndex);
            dlmAvailableRuleSets.addElement(le);
            RuleSetProperty rsp = (RuleSetProperty)ActiveRuleSetPropertyGroup.currentInstance.ruleSets.get(le.getDisplayName());
            rsp.getGlobalProperty().setValue("false");   //set the global property value to false
            dlmSelectedRuleSets.remove(selectedIndex);
            selectedIndex = jlistSelectedRuleSets.getSelectedIndex();
        }
        jlistSelectedRuleSets.updateUI();
        jlistAvailableRuleSets.updateUI();
    }

    /**
     * Called when the user double-clicks an item in the jlistSelectedRuleSets list
     * Move the item to the available list
     * @param e mouse event
     */
    void jlistSelectedRuleSets_mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2)
            deselectRules();
    }

    /**
     * Called when the user double-clicks an item in the jlistAvailableRuleSets list
     * move the item to the selected list
     * @param e mouse event
     */
    void jlistAvailableRuleSets_mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2)
            selectRules();
    }
}


/**
 * Wraps the entries that are places in the ListBox objects so that they can
 * track the GlobalProperty that's associated with each entry.
 */
class ListEntry {
    GlobalProperty prop;
    String displayName;

    /**
     * Constructor
     * @param name name as it is to appear in the list box
     * @param prop the GlobalProperty associated with this name
     */
    public ListEntry (String name, GlobalProperty prop) {
        this.displayName = name;
        this.prop = prop;
    }

    /**
     * get the GlobalProperty
     * @return GlobalProperty object
     */
    public GlobalProperty getProp () {
        return  prop;
    }

    /**
     * Get the display name
     * @return display name
     */
    public String getDisplayName () {
        return  displayName;
    }

    /**
     * Use the display name as the string representation
     * @return display name
     */
    public String toString () {
        return  displayName;
    }
}

