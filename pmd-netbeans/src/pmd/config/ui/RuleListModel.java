/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

import net.sourceforge.pmd.Rule;
import pmd.config.ConfigUtils;

/**
 * Common base class for list models for PMD rule lists.
 */
public abstract class RuleListModel extends AbstractListModel {
	
	/** The data in the list. */
	protected List list;
	
	/** Constructor for use by subclasses. */
	protected RuleListModel() {
		list = new ArrayList();
		refresh();
	}

	/**
	 * This implementation just delegates to the internal list.
	 */
	public Object getElementAt(int index) {
		return list.get(index);
	}


	/**
	 * This implementation just delegates to the internal list.
	 */
	public int getSize() {
		return list.size();
	}


	/**
	 * Set the internal list that this UI data model wraps.
	 *
	 * @param list The new list value, not null.
	 */
	public void setList(List list) {
		this.list = list;
	}


	/**
	 * Adds the given rule to the list.
	 *
	 * @param rule The rule to add to the list, not null.
	 */
	public void addRule(Rule rule) {
		if(!list.contains(rule)) {
			list.add(rule);
			Collections.sort(list, new RuleComparator());
			fireIntervalAdded(this, list.size(), list.size()); // FIXME: bogus indices!
		}
	}


	/**
	 * Removes <code>o</code> from the list
	 *
	 * @param o the object to remove.
	 */
	public void remove(Object o) {
		int i = list.indexOf(o);
		list.remove(o);
		fireIntervalRemoved(this, i, i);
	}


	/** Removes all elements in the list */
	public void removeAll() {
		int num = list.size();
		list.clear();
		fireIntervalRemoved(this, 0, num - 1);
	}


	/**
	 * Adds all rules of the given collection to the list.
	 *
	 * @param coll The collection of rules to add. Each element in this collection must be a Rule.
	 */
	public void addAll(Collection coll) {
		int i = list.size();
		list.addAll(coll);
		Collections.sort(list, new RuleComparator() );
		fireIntervalAdded(this, i, list.size()); // FIXME: bogus indices!
	}


	/**
	 * Returns the inner list backing this instance
	 *
	 * @return The inner list, not null.
	 */
	public List getList() {
		return list;
	}


	/** Resets the list to a starting state. */
	public abstract void refresh();
}
