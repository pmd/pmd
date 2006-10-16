package net.sourceforge.pmd.util.designer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.pmd.PMD;

/**
 * This class is responsible for creating the
 * content panel for the Create Rule XML Frame.
 */
public class CreateXMLRulePanel extends JPanel implements ActionListener{
	
	private static final JTextField rulenameField = new JTextField(30);
	private static final JTextField rulemsgField = new JTextField(30);
	private static final JTextArea ruledescField = new JTextArea(5,30);
	private static final JTextArea ruleXMLArea = new JTextArea(30, 30);
	private JTextArea xpathQueryArea = new JTextArea();
	private CodeEditorTextPane codeEditorPane = new CodeEditorTextPane();
	
	public CreateXMLRulePanel(JTextArea xpathQueryArea, CodeEditorTextPane codeEditorPane){
		super();
		this.xpathQueryArea = xpathQueryArea;
		this.codeEditorPane = codeEditorPane;
		GridBagConstraints gbc = new GridBagConstraints();
		// We use a gridbaglayout for a nice and sturdy look and feel
		GridBagLayout gbl = new GridBagLayout(); 
	    setLayout(gbl);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.EAST;
	    gbc.weightx = 0.5;
	    JLabel rulenameLabel = new JLabel("Rule name : ");
	    gbl.setConstraints(rulenameLabel, gbc);
	    add(rulenameLabel);
	    gbc.weightx = 0.5;
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.gridx = 1;
	    gbl.setConstraints(rulenameField, gbc);
	    add(rulenameField);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.anchor = GridBagConstraints.EAST;
	    gbc.weightx = 0.5;
	    JLabel rulemsgLabel = new JLabel("Rule msg : ");
	    gbl.setConstraints(rulemsgLabel, gbc);
	    add(rulemsgLabel);
	    gbc.gridx = 1;
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.weightx = 0.5;
	    gbl.setConstraints(rulemsgField, gbc);
	    add(rulemsgField);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.anchor = GridBagConstraints.EAST;
	    gbc.weightx = 0.5;
	    JLabel ruledescLabel = new JLabel("Rule desc : ");
	    gbl.setConstraints(ruledescLabel,gbc);
	    add(ruledescLabel);
	    gbc.gridx = 1;
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.weightx = 0.5;
	    gbl.setConstraints(ruledescField,gbc);
	    add(ruledescField);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    gbc.anchor = GridBagConstraints.NORTH;
	    JButton createRuleBtn = new JButton("Create rule XML");
	    createRuleBtn.addActionListener(this);
	    gbl.setConstraints(createRuleBtn, gbc);
	    add(createRuleBtn);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 4;
	    gbc.anchor = GridBagConstraints.NORTH;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 1.0;
	    gbc.weighty = 1.0;
	    JScrollPane ruleXMLPane = new JScrollPane(ruleXMLArea);
	    gbl.setConstraints(ruleXMLPane, gbc);
	    add(ruleXMLPane);
	    
	    repaint();
	}


	/**
	 * We let our class implement the ActionListener interface
	 * and use it to generate the xml code when the user presses
	 * the "Create rule XML" button.
	 * 
	 */
	public void actionPerformed(ActionEvent exception) {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("<rule  name=\"" + rulenameField.getText() + "\"" + PMD.EOL);
	    buffer.append("  message=\"" + rulemsgField.getText() + "\"" + PMD.EOL);
	    buffer.append("  class=\"" + (xpathQueryArea.getText().length() == 0 ? "" : "net.sourceforge.pmd.rules.XPathRule") + "\">" + PMD.EOL);
	    buffer.append("  <description>" + PMD.EOL);
	    buffer.append("  " + ruledescField.getText() + PMD.EOL);
	    buffer.append("  </description>" + PMD.EOL);
	    if (xpathQueryArea.getText().length() != 0) {
	    	buffer.append("  <properties>" + PMD.EOL);
	    	buffer.append("    <property name=\"xpath\">" + PMD.EOL);
	    	buffer.append("    <value>" + PMD.EOL);
	    	buffer.append("<![CDATA[" + PMD.EOL);
	    	buffer.append(xpathQueryArea.getText() + PMD.EOL);
	    	buffer.append("]]>" + PMD.EOL);
	    	buffer.append("    </value>" + PMD.EOL);
	    	buffer.append("    </property>" + PMD.EOL);
	    	buffer.append("  </properties>" + PMD.EOL);
	    }
	    buffer.append("  <priority>3</priority>" + PMD.EOL);
	    buffer.append("  <example>" + PMD.EOL);
	    buffer.append("<![CDATA[" + PMD.EOL);
	    buffer.append(codeEditorPane.getText());
	    buffer.append("]]>" + PMD.EOL);
	    buffer.append("  </example>" + PMD.EOL);
	    buffer.append("</rule>" + PMD.EOL);
	
	    ruleXMLArea.setText(buffer.toString());
	    repaint();
	}	
	
}

