package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.*;
import com.borland.primetime.help.HelpTopic;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


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

    public ConfigureRuleSetPropertyPage() {
        try {
            jbInit();
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
        this.add(splitPaneConfRuleSets,  BorderLayout.CENTER);
        splitPaneConfRuleSets.add(spRuleSets, JSplitPane.TOP);
        spRuleSets.getViewport().add(listRuleSets, null);
        splitPaneConfRuleSets.add(spRules, JSplitPane.BOTTOM);
        spRules.getViewport().add(listRules, null);
        splitPaneConfRuleSets.setDividerLocation(200);
    }
}