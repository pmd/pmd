/*
 * @(#)RuleSetDialog.java $Revision$ ($Date$)
 * Copyright (c) 2004
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.gel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sourceforge.pmd.RuleSet;

import net.sourceforge.pmd.gel.data.RuleSetsTableModel;
import net.sourceforge.pmd.gel.data.RuleSetTableModel;

/**
 * A <em>RuleSet</em> choose dialog.
 *
 * @author Andrey Lumyanski
 * @version $Revision$ ($Date$)
 */
public class RuleSetDialog extends JDialog
	implements ActionListener, ListSelectionListener, TableModelListener {

	private JButton buttonProcess;
	private JButton buttonCancel;
	private JTable tableRuleSets;
	private JTextArea textareaRSDescription;
	private JLabel labelRules;
	private JTable tableRules;
	private JTextArea textareaRuleDescription;
	private JTextArea textareaCodeExample;
	private boolean isCanceled;

	private RuleSet ruleSet[];
	private RuleSetsTableModel dmRuleSets;
	private RuleSetTableModel dmRules;

	/**
	 * Creates a <code>RuleSetDialog</code> object.
	 * @param rs RuleSet[] a array of <code>RuleSet</code>
	 */
	public RuleSetDialog(RuleSet rs[]) {
		super((Frame)null, "PMD: select rules", true);

		ruleSet = rs;
		dmRuleSets = new RuleSetsTableModel(ruleSet);
		dmRules = new RuleSetTableModel();

		Border border1 = new EmptyBorder(2, 10, 2, 10);
		Border border2 = new EmptyBorder(2, 10, 2, 10);

		JPanel panelRS = new JPanel(new BorderLayout());

		Dimension size = new Dimension(20, 20);

		Font font = new Font("Helvetica", Font.PLAIN, 10);
		Color color = new Color(0x00, 0x00, 0x80);
		if (font != null) {
			UIManager.put("Label.font", font);
			UIManager.put("Button.font", font);
		}
		if (color != null) {
			UIManager.put("Label.foreground", color);
			UIManager.put("Button.foreground", color);
		}

		JLabel labelRuleSets = new JLabel("Rule sets:");
		panelRS.add(labelRuleSets, BorderLayout.NORTH);

		tableRuleSets = new JTable(dmRuleSets);
		tableRuleSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRuleSets.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tableRuleSets.getColumnModel().getColumn(0).setMinWidth(50);
		tableRuleSets.getColumnModel().getColumn(0).setMaxWidth(50);
		tableRuleSets.getSelectionModel().addListSelectionListener(this);
		dmRuleSets.addTableModelListener(this);
		panelRS.add(new JScrollPane(tableRuleSets));

		JPanel panelRSDescription = new JPanel(new BorderLayout());
		JLabel labelRSDescription = new JLabel("Rule set description:");
		textareaRSDescription = new JTextArea();
		textareaRSDescription.setLineWrap(true);
		textareaRSDescription.setWrapStyleWord(true);
		textareaRSDescription.setEditable(false);
		panelRSDescription.add(labelRSDescription, BorderLayout.NORTH);
		panelRSDescription.add(new JScrollPane(textareaRSDescription));
		size = new Dimension(300, 100);
		panelRSDescription.setPreferredSize(size);
		panelRSDescription.setBorder(border2);
		panelRS.add(panelRSDescription, BorderLayout.EAST);

		JPanel panelRules = new JPanel(new BorderLayout());
		labelRules = new JLabel("Rules:");
		panelRules.add(labelRules, BorderLayout.NORTH);

		tableRules = new JTable(dmRules);
		tableRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRules.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tableRules.getColumnModel().getColumn(0).setMinWidth(50);
		tableRules.getColumnModel().getColumn(0).setMaxWidth(50);
		tableRules.getSelectionModel().addListSelectionListener(this);

		JPanel panelRule = new JPanel(new BorderLayout());

		JPanel panelRD = new JPanel(new BorderLayout());
		JLabel labelRuleDescription = new JLabel("Rule description:");
		panelRD.add(labelRuleDescription, BorderLayout.NORTH);
		textareaRuleDescription = new JTextArea();
		textareaRuleDescription.setLineWrap(true);
		textareaRuleDescription.setWrapStyleWord(true);
		textareaRuleDescription.setEditable(false);
		panelRD.add(new JScrollPane(textareaRuleDescription));

		size = new Dimension(100, 100);
		panelRD.setPreferredSize(size);
		panelRule.add(panelRD, BorderLayout.NORTH);

		JPanel panelRuleExample = new JPanel(new BorderLayout());
		JLabel labelCodeExample = new JLabel("Code example:");
		panelRuleExample.add(labelCodeExample, BorderLayout.NORTH);
		textareaCodeExample = new JTextArea();
		textareaCodeExample.setEditable(false);
		panelRuleExample.add(new JScrollPane(textareaCodeExample));

		panelRule.add(panelRuleExample);
		panelRule.setBorder(border2);
		size = new Dimension(300, 100);
		panelRule.setPreferredSize(size);

		panelRules.add(new JScrollPane(tableRules));
		panelRules.add(panelRule, BorderLayout.EAST);

		JPanel panelButtons = new JPanel(new GridLayout(1, 2, 5, 5));
		buttonProcess = new JButton("Process");
		buttonProcess.addActionListener(this);
		panelButtons.add(buttonProcess);
		buttonProcess.setDefaultCapable(true);

		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(this);
		panelButtons.add(buttonCancel);

		JPanel panelFooter = new JPanel(new BorderLayout());
		size = new Dimension(300, 20);
		panelButtons.setPreferredSize(size);
		panelButtons.setBorder(border2);
		panelFooter.add(panelButtons, BorderLayout.EAST);

		String version = null;
		try {
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream("plugin.properties"));
			version = props.getProperty("PMDPlugin.version", "");
		} catch (Exception e) {
			version = "";
		}

		if (version.length() > 0) {
			version = " Ver. " + version;
		}

		JLabel labelInfo =
			new JLabel("PMD for Gel" + version + ". Author: Andrey Lumyanski");
		panelFooter.add(labelInfo);

		size = new Dimension(240, 100);
		panelRS.setPreferredSize(size);
		panelRS.setBorder(border1);
		getContentPane().add(panelRS, BorderLayout.NORTH);

		panelRules.setBorder(border1);
		getContentPane().add(panelRules);

		size = new Dimension(300, 30);
		panelFooter.setPreferredSize(size);
		panelFooter.setBorder(border1);
		getContentPane().add(panelFooter, BorderLayout.SOUTH);

		setSize(640, 480);

		isCanceled = true;
	}

	/**
	 * Processes action from child controls.
	 * <strong>Note!</strong> Do not invoke this method manually.
	 *
	 * @param evt ActionEvent event info
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == buttonProcess) {
			isCanceled = false;
		}
		dispose();
	}

	/**
	 * Processes selection event from child <code>JTable</code> controls.
	 * <strong>Note!</strong> Do not invoke this method manually.
	 *
	 * @param evt ListSelectionEvent event info
	 */
	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getSource() == tableRuleSets.getSelectionModel()) {
			int selectedRow = tableRuleSets.getSelectedRow();
			if (selectedRow > -1) {
				labelRules.setText(ruleSet[selectedRow].getName() + " rules:");
				textareaRSDescription.setText(
					ruleSet[selectedRow].getDescription());
				dmRules.setData(ruleSet[selectedRow]);
			} else {
				labelRules.setText("Rules:");
				textareaRSDescription.setText("");
				dmRules.setData(null);
			}
			return;
		}
		if (evt.getSource() == tableRules.getSelectionModel()) {
			int selectedRow = tableRules.getSelectedRow();
			if (selectedRow > -1) {
				textareaRuleDescription.setText(
					dmRules.getRule(selectedRow).getDescription());
				textareaCodeExample.setText(
					dmRules.getRule(selectedRow).getExample());
			} else {
				textareaRuleDescription.setText("");
				textareaCodeExample.setText("");
			}
			return;
		}
	}

	/**
	 * Processes tableChanged event from child <code>JTable</code> control.
	 * <strong>Note!</strong> Do not invoke this method manually.
	 *
	 * @param evt TableModelEvent event info
	 */
	public void tableChanged(TableModelEvent evt) {
		if (evt.getSource() == dmRuleSets) {
			if (evt.getType() == TableModelEvent.UPDATE) {
				if (ruleSet[evt.getFirstRow()].include()) {
					tableRules.tableChanged(
						new TableModelEvent(
							dmRules, 0, dmRules.getRowCount()-1, 0));
				}
			}
		}

	}

	/**
	 * Returns <code>true</code> if dialog was canceled.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if dialog was canceled
	 */
	public boolean isCanceled() {
		return isCanceled;
	}
}