package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.*;
import com.borland.primetime.help.HelpTopic;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import net.sourceforge.pmd.Rule;
import com.borland.primetime.ide.MessageCategory;
import com.borland.primetime.ide.MessageView;
import com.borland.primetime.ide.Browser;


/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class ConfigureRuleSetPropertyPage extends PropertyPage {
    private static MessageCategory msgCat = new MessageCategory("test");
    private BorderLayout borderLayout1 = new BorderLayout();
    private JSplitPane splitPaneConfRuleSets = new JSplitPane();
    private JScrollPane spRuleSets = new JScrollPane();
    private JScrollPane spRules = new JScrollPane();
    private JList listRuleSets = new JList();
    private JList listRules = new JList();
    private DefaultListModel dlmRuleSets = new DefaultListModel();
    private DefaultListModel dlmRules = new DefaultListModel();

    public ConfigureRuleSetPropertyPage() {
        try {
            jbInit();
            init2();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void writeProperties() {
        /**
         * Go through all the ruleSetProperties objects and revalidate them to persist
         * their global properties
         */
        for (Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext(); ) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            rsp.revalidateRules();
        }
    }
    public HelpTopic getHelpTopic() {
        /**@todo Implement this com.borland.primetime.properties.PropertyPage abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getHelpTopic() not yet implemented.");
    }
    public void readProperties() {
        /**
         * Go through all the ruleSetProperties objects and reset them to the
         * GlobalProeprty values
         */
        for (Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext(); ) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            rsp.revalidateRules();
            rsp.resetRuleSelectionState();
        }
        this.listRules.updateUI();
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        spRuleSets.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Rule Sets"));
        spRules.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Rules"));
        listRuleSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(splitPaneConfRuleSets,  BorderLayout.CENTER);
        splitPaneConfRuleSets.add(spRuleSets, JSplitPane.TOP);
        spRuleSets.getViewport().add(listRuleSets, null);
        splitPaneConfRuleSets.add(spRules, JSplitPane.BOTTOM);
        spRules.getViewport().add(listRules, null);
        splitPaneConfRuleSets.setDividerLocation(200);
    }

    private void init2() {
        listRules.setCellRenderer(new CheckCellRenderer());
        CheckListener cl = new CheckListener(listRules);
        listRules.addMouseListener(cl);
        listRules.addKeyListener(cl);
        listRules.setModel(dlmRules);

        listRuleSets.setModel(dlmRuleSets);
        listRuleSets.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                listRuleSets_valueChanged(e);
            }
        }
        );
        //get the list if rule sets and populate the listRuleSets model
        for (Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext();) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            dlmRuleSets.addElement(rsp.getActiveRuleSet().getName());
            listRuleSets.updateUI();
        }

    }


    /**
     * When a ruleset is selected we need to update the listRules list
     * @param e selection event
     */
    private void listRuleSets_valueChanged(ListSelectionEvent e) {
        this.dlmRules.clear();
        String selectedRuleSet = ((JList)e.getSource()).getSelectedValue().toString();
        //get the RuleSetProperty for this ruleset
        RuleSetProperty rsp = (RuleSetProperty)ActiveRuleSetPropertyGroup.currentInstance.ruleSets.get(selectedRuleSet);
        //iterate over the rules from of the rule set
        //We need to iterate over the originalRuleSet because the active rule set only
        //has the rules that have been enabled
        for (Iterator iter = rsp.getOriginalRuleSet().getRules().iterator(); iter.hasNext(); ) {
            Rule rule = (Rule)iter.next();
            String ruleName = rule.getName();
            dlmRules.addElement(new RuleData(ruleName, rsp.isRuleSelected(ruleName)));
        }

    }

    class CheckListener implements MouseListener, KeyListener
    {
            protected JList list;

            public CheckListener(JList list)
            {
                    this.list = list;
            }

            public void mouseClicked(MouseEvent e)
            {
                    if (e.getX() < 20)
                            doCheck();
            }

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}

            public void keyPressed(KeyEvent e)
            {
                    if (e.getKeyChar() == ' ')
                            doCheck();
            }

            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}

            protected void doCheck()
            {
                    int index = list.getSelectedIndex();
                    if (index < 0)
                            return;
                    RuleData rd = (RuleData)list.getModel().getElementAt(index);
                    rd.invertSelected();

                    //get the currently selected rule set
                    String ruleSetName = listRuleSets.getSelectedValue().toString();
                    //get the RuleSetProperty object
                    RuleSetProperty rsp = (RuleSetProperty)ActiveRuleSetPropertyGroup.currentInstance.ruleSets.get(ruleSetName);
                    //update the selection setting for this rule in the rule set property
                    rsp.setRuleSelected(rd.getName(), rd.isSelected());

                    list.repaint();
            }
    }

}


class CheckCellRenderer extends JPanel
            implements ListCellRenderer
    {
        protected JCheckBox check;
        protected static Border m_noFocusBorder = new EmptyBorder(1, 1, 1, 1);


        public CheckCellRenderer()
        {
            super();
            setOpaque(true);
            setBorder(m_noFocusBorder);
        }

        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        public Component getListCellRendererComponent (
                JList list,
                Object value,                    // value to display
                int index,                  // cell index
                boolean isSelected,         // is the cell selected
                boolean cellHasFocus)       // the list and the cell have the focus
        {
            RuleData rd = (RuleData)value;
            JCheckBox c = new JCheckBox(rd.getName(), rd.isSelected());
            //c.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            //c.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            c.setBackground(Color.white);
            c.setFont(list.getFont());

            return c;
        }
    }


    class RuleData
    {
        protected String ruleName;
        protected boolean selected;

        public RuleData(String ruleName)
        {
            this.ruleName = ruleName;
            selected = true;
        }
        public RuleData(String ruleName, boolean isSelected) {
            this(ruleName);
            this.selected = isSelected;
        }

        public String getName() { return ruleName; }

        public void setSelected(boolean selected) { this.selected = selected;}

        public void invertSelected() { this.selected = !this.selected; }

        public boolean isSelected() { return this.selected; }

        public String toString() { return this.ruleName;}
    }

