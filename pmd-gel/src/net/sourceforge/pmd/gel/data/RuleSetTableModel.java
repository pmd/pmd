/*
 * @(#)RuleSetTableModel.java $Revision$ ($Date$)
 * Copyright (c) 2004
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.gel.data;

import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;


/**
 * A <em>RuleSet</em> TableModel.
 *
 * @author Andrey Lumyanski
 * @version $Revision$ ($Date$)
 */
public class RuleSetTableModel extends AbstractTableModel {
	private static final String[] columnName = {
		"Include", "Name"
	};
	private static final Class[] columnClass = {
		Boolean.class, String.class
	};

	private RuleSet ruleSet;
	private Rule rule[];
	private int ruleCount;

	/**
	 * Creates a <code>RuleSetTableModel</code> object.
	 */
	public RuleSetTableModel() {
		super();
		ruleSet = null;
		rule = null;
		ruleCount = 0;
	}

	/**
	 * Returns the columns count.
	 *
	 * @return the columns count.
	 */
	public int getColumnCount() {
		return columnName.length;
	}

	/**
	 * Always returns String.class
	 *
	 * @param index int the column index
	 *
	 * @return String.class
	 */
	public Class getColumnClass(int index) {
		return columnClass[index];
	}

	/**
	 * Returns the column name.
	 *
	 * @param index int the column index
	 *
	 * @return the column name
	 */
	public String getColumnName(int index) {
		return columnName[index];
	}

	/**
	 * Returns the number of rows in the model.
	 *
	 * @return the number of rows in the model.
	 */
	public int getRowCount() {
		return ruleCount;
	}

	/**
	 * Returns <code>true</code> for column #0 and <code>false</code> for all
	 * other columns.
	 *
	 * @param rowIndex - the row whose value to be queried
	 * @param columnIndex - the column whose value to be queried
	 *
	 * @return <code>true</code> if the cell is editable
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param rowIndex  the row whose value is to be queried
	 * @param columnIndex  the column whose value is to be queried
	 *
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		switch (columnIndex) {
		case 0:
			value = Boolean.valueOf(rule[rowIndex].include());
			break;
		case 1:
			value = rule[rowIndex].getName();
			break;
		}

		return value;
	}

	/**
	 * Sets the value in the cell at <code>columnIndex</code>
	 * and <code>rowIndex</code> to <code>value</code>.
	 *
	 * @param value the new value
	 * @param rowIndex  the row whose value is to be changed
	 * @param columnIndex  the column whose value is to be changed
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			rule[rowIndex].setInclude(((Boolean)value).booleanValue());
		}
	}

	/**
	 * Sets a data for table model.
	 * Fires <code>tableDataChanged</code> event.
	 *
	 * @param rs RuleSet a <code>RuleSet</code> instance
	 */
	public void setData(RuleSet rs) {
		if (rs != ruleSet) {
			ruleSet = rs;
			ruleCount = (ruleSet == null) ? 0 : ruleSet.size();
			if (rule == null || ruleCount > rule.length) {
				rule = new Rule[ruleCount];
			}
			if (ruleSet != null) {
				Iterator it = ruleSet.getRules().iterator();
				int i = 0;
				while (it.hasNext()) {
					rule[i++] = (Rule) it.next();
				}
			}
			fireTableDataChanged();
		}
	}

	/**
	 * Returns a <code>Rule</code> by the index.
	 *
	 * @param index int a rule index
	 * @returna <code>Rule</code> by the index.
	 */
	public Rule getRule(int index) {
		return rule[index];
	}
}