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


/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class ConfigureRuleSetPropertyPage extends PropertyPage {
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
        /**@todo Implement this com.borland.primetime.properties.PropertyPage abstract method*/
    }
    public HelpTopic getHelpTopic() {
        /**@todo Implement this com.borland.primetime.properties.PropertyPage abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getHelpTopic() not yet implemented.");
    }
    public void readProperties() {
        /**@todo Implement this com.borland.primetime.properties.PropertyPage abstract method*/
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
        dlmRules.addElement(new RuleData("rule 1"));
        dlmRules.addElement(new RuleData("Rule 2"));


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
            c.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            c.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            c.setFont(list.getFont());

            return c;
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
                    list.repaint();
            }
    }

    class RuleData
    {
        protected String ruleName;
        protected boolean selected;

        public RuleData(String ruleName)
        {
            this.ruleName = ruleName;
            selected = false;
        }

        public String getName() { return ruleName; }

        public void setSelected(boolean selected) { this.selected = selected;}

        public void invertSelected() { this.selected = !this.selected; }

        public boolean isSelected() { return this.selected; }

        public String toString() { return this.ruleName;}
    }

